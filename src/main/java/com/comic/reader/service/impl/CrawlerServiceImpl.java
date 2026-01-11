package com.comic.reader.service.impl;

import com.comic.reader.entity.Category;
import com.comic.reader.entity.Chapter;
import com.comic.reader.entity.Comic;
import com.comic.reader.entity.Page;
import com.comic.reader.mapper.CategoryMapper;
import com.comic.reader.mapper.ChapterMapper;
import com.comic.reader.mapper.ComicMapper;
import com.comic.reader.mapper.PageMapper;
import com.comic.reader.service.CrawlerService;
import com.comic.reader.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class CrawlerServiceImpl implements CrawlerService {

    @Autowired
    private ComicMapper comicMapper;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private PageMapper pageMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private StorageService storageService;

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    // 模拟浏览器 User-Agent
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    @Override
    @Transactional
    public void crawlComic(String url) {
        try {
            log.info("开始抓取漫画: {}", url);
            Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();

            // 1. 解析漫画基本信息 (需要根据目标网站修改选择器)
            String title = doc.select("h1.title").text(); 
            String author = doc.select("span.author").text();
            String desc = doc.select("div.summary").text();
            String coverUrl = doc.select("img.cover").attr("src");

            if (title.isEmpty()) {
                log.error("无法解析标题，请检查选择器");
                return;
            }

            // 检查是否已存在
            if (!comicMapper.selectByKeyword(title).isEmpty()) {
                log.info("漫画已存在: {}", title);
                return;
            }

            // 下载封面并上传到 MinIO
            String coverPath = downloadImage(coverUrl, "covers");

            // 保存漫画
            Comic comic = new Comic();
            comic.setTitle(title);
            comic.setAuthor(author);
            comic.setDescription(desc);
            comic.setCoverImage(coverPath); // 存完整URL
            comic.setStatus(1);
            comic.setCreateTime(Instant.now());
            comic.setUpdateTime(Instant.now());
            
            // 默认分类
            Category defaultCat = categoryMapper.selectById(1L);
            if (defaultCat != null) {
                comic.setCategoryId(defaultCat);
            }

            comicMapper.insert(comic);
            log.info("漫画保存成功: {}", title);

            // 2. 解析章节列表
            Elements chapterLinks = doc.select("ul.chapter-list li a");
            List<Element> linksList = new ArrayList<>(chapterLinks);
            Collections.reverse(linksList);

            int chapterNum = 1;
            for (Element link : linksList) {
                String chapterUrl = link.attr("abs:href");
                String chapterTitle = link.text();
                
                crawlChapter(comic, chapterNum++, chapterTitle, chapterUrl);
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            log.error("抓取失败", e);
            throw new RuntimeException("抓取失败: " + e.getMessage());
        }
    }

    private void crawlChapter(Comic comic, int chapterNum, String title, String url) {
        try {
            log.info("开始抓取章节: {} - {}", title, url);
            Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();

            // 保存章节
            Chapter chapter = new Chapter();
            chapter.setComic(comic);
            chapter.setChapterNumber(chapterNum);
            chapter.setTitle(title);
            chapter.setCreateTime(Instant.now());
            chapterMapper.insert(chapter);

            // 3. 解析图片列表
            Elements images = doc.select("div.comic-content img");
            
            int pageNum = 1;
            for (Element img : images) {
                String imgUrl = img.attr("abs:src");
                if (imgUrl.isEmpty()) {
                    imgUrl = img.attr("abs:data-src");
                }

                if (!imgUrl.isEmpty()) {
                    // 下载图片并上传到 MinIO
                    String savePath = "comics/" + comic.getId() + "/chapter_" + chapter.getId();
                    String minioUrl = downloadImage(imgUrl, savePath);

                    Page page = new Page();
                    page.setChapter(chapter);
                    page.setPageNumber(pageNum++);
                    page.setImagePath(minioUrl); // 存完整URL
                    page.setCreateTime(Instant.now());
                    pageMapper.insert(page);
                }
            }
            
            chapter.setPageCount(pageNum - 1);
            chapterMapper.update(chapter);

        } catch (Exception e) {
            log.error("章节抓取失败: " + title, e);
        }
    }

    @Override
    @Transactional
    public String importLocalComics() {
        File importDir = new File(uploadDir, "import");
        if (!importDir.exists() || !importDir.isDirectory()) {
            return "导入目录不存在: " + importDir.getAbsolutePath() + "。请创建该目录并将漫画文件夹放入其中。";
        }

        File[] comicDirs = importDir.listFiles(File::isDirectory);
        if (comicDirs == null || comicDirs.length == 0) {
            return "导入目录为空，没有发现漫画文件夹。";
        }

        int successCount = 0;
        StringBuilder resultMsg = new StringBuilder();

        for (File comicDir : comicDirs) {
            try {
                importSingleComic(comicDir);
                successCount++;
                resultMsg.append("成功导入: ").append(comicDir.getName()).append("\n");
            } catch (Exception e) {
                log.error("导入漫画失败: " + comicDir.getName(), e);
                resultMsg.append("导入失败: ").append(comicDir.getName()).append(" - ").append(e.getMessage()).append("\n");
            }
        }

        return "导入完成。成功: " + successCount + "。\n" + resultMsg.toString();
    }

    private void importSingleComic(File comicDir) {
        String title = comicDir.getName();
        log.info("开始导入本地漫画: {}", title);

        // 1. 检查或创建漫画
        Comic comic;
        List<Comic> exist = comicMapper.selectByKeyword(title);
        comic = exist.stream().filter(c -> c.getTitle().equals(title)).findFirst().orElse(null);

        if (comic == null) {
            comic = new Comic();
            comic.setTitle(title);
            comic.setAuthor("本地导入");
            comic.setDescription("本地导入的漫画");
            comic.setStatus(1);
            comic.setCreateTime(Instant.now());
            comic.setUpdateTime(Instant.now());
            
            Category defaultCat = categoryMapper.selectById(1L);
            if (defaultCat != null) {
                comic.setCategoryId(defaultCat);
            }
            
            comicMapper.insert(comic);
        }

        // 2. 处理封面
        File[] coverFiles = comicDir.listFiles((dir, name) -> 
            name.toLowerCase().startsWith("cover.") || name.toLowerCase().equals("folder.jpg")
        );
        if (coverFiles != null && coverFiles.length > 0) {
            // 上传到 MinIO
            String coverUrl = storageService.upload(coverFiles[0], "covers/" + System.currentTimeMillis() + "_" + coverFiles[0].getName());
            comic.setCoverImage(coverUrl); // 存完整URL
            comicMapper.update(comic);
        }

        // 3. 处理章节
        File[] chapterDirs = comicDir.listFiles(File::isDirectory);
        if (chapterDirs != null) {
            Arrays.sort(chapterDirs, Comparator.comparing(File::getName));

            int chapterNum = 1;
            Integer maxChapter = chapterMapper.selectMaxChapterNumber(comic.getId());
            if (maxChapter != null) {
                chapterNum = maxChapter + 1;
            }

            for (File chapterDir : chapterDirs) {
                importSingleChapter(comic, chapterDir, chapterNum++);
            }
        }
    }

    private void importSingleChapter(Comic comic, File chapterDir, int chapterNum) {
        String chapterTitle = chapterDir.getName();
        
        Chapter chapter = new Chapter();
        chapter.setComic(comic);
        chapter.setChapterNumber(chapterNum);
        chapter.setTitle(chapterTitle);
        chapter.setCreateTime(Instant.now());
        chapterMapper.insert(chapter);

        // 处理图片
        File[] images = chapterDir.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".webp");
        });

        if (images != null) {
            Arrays.sort(images, Comparator.comparing(File::getName));

            int pageNum = 1;
            String saveSubDir = "comics/" + comic.getId() + "/chapter_" + chapter.getId();
            
            for (File image : images) {
                // 上传到 MinIO
                String fileName = System.currentTimeMillis() + "_" + image.getName();
                String minioUrl = storageService.upload(image, saveSubDir + "/" + fileName);
                
                Page page = new Page();
                page.setChapter(chapter);
                page.setPageNumber(pageNum++);
                page.setImagePath(minioUrl); // 存完整URL
                page.setCreateTime(Instant.now());
                pageMapper.insert(page);
            }
            
            chapter.setPageCount(pageNum - 1);
            chapterMapper.update(chapter);
        }
    }

    private String downloadImage(String imageUrl, String subDir) {
        try {
            URL url = new URL(imageUrl);
            String fileName = System.currentTimeMillis() + "_" + new File(url.getPath()).getName();
            
            // 确保文件名合法
            fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "");
            if (fileName.length() > 50) fileName = fileName.substring(fileName.length() - 50);

            try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                
                // 上传到 MinIO
                // 简单推断类型
                String contentType = "application/octet-stream";
                if (fileName.endsWith(".jpg")) contentType = "image/jpeg";
                else if (fileName.endsWith(".png")) contentType = "image/png";
                
                return storageService.upload(out.toByteArray(), contentType, subDir + "/" + fileName);
            }
            
        } catch (IOException e) {
            log.error("图片下载失败: " + imageUrl, e);
            return "";
        }
    }
}
