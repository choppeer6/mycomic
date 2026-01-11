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
import com.comic.reader.util.FileUtil;
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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    // 模拟浏览器 User-Agent
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";

    @Override
    @Transactional
    public void crawlComic(String url) {
        // ... (之前的爬虫代码保持不变) ...
        // 为了节省篇幅，这里省略之前的爬虫代码，实际文件中请保留
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
        // 精确匹配标题
        comic = exist.stream().filter(c -> c.getTitle().equals(title)).findFirst().orElse(null);

        if (comic == null) {
            comic = new Comic();
            comic.setTitle(title);
            comic.setAuthor("本地导入");
            comic.setDescription("本地导入的漫画");
            comic.setStatus(1);
            comic.setCreateTime(Instant.now());
            comic.setUpdateTime(Instant.now());
            
            // 默认分类
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
            String coverPath = copyFileToStandardDir(coverFiles[0], "covers");
            comic.setCoverImage("/images/" + coverPath); // 存相对路径
            comicMapper.update(comic);
        }

        // 3. 处理章节
        File[] chapterDirs = comicDir.listFiles(File::isDirectory);
        if (chapterDirs != null) {
            // 按文件名排序章节
            Arrays.sort(chapterDirs, Comparator.comparing(File::getName));

            int chapterNum = 1;
            // 如果已经有章节，接着后面的序号
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
        
        // 检查章节是否已存在 (简单通过标题判断，防止重复导入)
        // 这里为了简化，假设每次都是新导入，或者你可以添加逻辑跳过已存在的章节
        
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
            // 按文件名排序图片
            Arrays.sort(images, Comparator.comparing(File::getName));

            int pageNum = 1;
            String saveSubDir = "comic_" + comic.getId() + "/chapter_" + chapter.getId();
            
            for (File image : images) {
                String savedPath = copyFileToStandardDir(image, saveSubDir);
                
                Page page = new Page();
                page.setChapter(chapter);
                page.setPageNumber(pageNum++);
                page.setImagePath(savedPath);
                page.setCreateTime(Instant.now());
                pageMapper.insert(page);
            }
            
            chapter.setPageCount(pageNum - 1);
            chapterMapper.update(chapter);
        }
    }

    private String copyFileToStandardDir(File source, String subDir) {
        try {
            File destDir = new File(uploadDir, subDir);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            
            // 生成新文件名，防止冲突，或者保持原名
            // 这里保持原名，但加上时间戳前缀防止重名
            String fileName = System.currentTimeMillis() + "_" + source.getName();
            File destFile = new File(destDir, fileName);
            
            Files.copy(source.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            return subDir + "/" + fileName;
        } catch (IOException e) {
            log.error("文件复制失败: " + source.getAbsolutePath(), e);
            throw new RuntimeException("文件复制失败");
        }
    }
    
    // 辅助方法：下载图片 (保留之前的实现)
    private String downloadImage(String imageUrl, String subDir) {
        // ... (保留之前的实现)
        return "";
    }
}
