package com.comic.reader.service.impl;

import com.comic.reader.service.ChapterService;
import com.comic.reader.dto.ChapterRequest;
import com.comic.reader.dto.ChapterResponse;
import com.comic.reader.entity.Chapter;
import com.comic.reader.entity.Comic;
import com.comic.reader.entity.Page;
import com.comic.reader.mapper.ChapterMapper;
import com.comic.reader.mapper.ComicMapper;
import com.comic.reader.mapper.PageMapper;
import com.comic.reader.util.FileUtil;
import com.comic.reader.vo.ChapterVO;
import com.comic.reader.vo.PageVO;
import com.comic.reader.vo.ReaderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChapterServiceImpl implements ChapterService {

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private ComicMapper comicMapper;

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @Override
    public List<ChapterResponse> getChaptersByComic(Long comicId) {
        List<Chapter> chapters = chapterMapper.selectByComicId(comicId);
        return chapters.stream().map(chapter -> {
            ChapterResponse response = new ChapterResponse();
            BeanUtils.copyProperties(chapter, response);
            if (chapter.getComic() != null) {
                response.setComicTitle(chapter.getComic().getTitle());
            }
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public void addChapter(ChapterRequest request) {
        // 1. 验证漫画是否存在
        Comic comic = comicMapper.selectById(request.getComicId());
        if (comic == null) {
            throw new RuntimeException("漫画不存在");
        }

        // 2. 计算章节号 (自动递增)
        Integer maxChapterNum = chapterMapper.selectMaxChapterNumber(request.getComicId());
        int nextChapterNum = (maxChapterNum == null ? 0 : maxChapterNum) + 1;

        // 3. 创建章节记录
        Chapter chapter = new Chapter();
        chapter.setComic(comic);
        chapter.setChapterNumber(nextChapterNum);
        chapter.setTitle(request.getTitle());
        chapter.setPageCount(request.getPages() != null ? request.getPages().size() : 0);
        chapter.setCreateTime(Instant.now());
        
        chapterMapper.insert(chapter);

        // 4. 处理图片上传并保存页面信息
        if (request.getPages() != null && !request.getPages().isEmpty()) {
            saveChapterPages(chapter, request.getPages());
        }
        
        // 5. 更新漫画更新时间
        comic.setUpdateTime(Instant.now());
        comicMapper.update(comic);
    }

    private void saveChapterPages(Chapter chapter, List<MultipartFile> files) {
        // 创建章节目录: uploads/comic_{id}/chapter_{id}/
        String chapterPath = "comic_" + chapter.getComic().getId() + File.separator + "chapter_" + chapter.getId();
        File saveDir = new File(uploadDir, chapterPath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        int pageNum = 1;
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String fileName = FileUtil.generateFileName(file.getOriginalFilename());
            File destFile = new File(saveDir, fileName);
            
            // 保存文件
            FileUtil.saveFile(file, destFile.getAbsolutePath());

            // 保存页面记录
            Page page = new Page();
            page.setChapter(chapter);
            page.setPageNumber(pageNum++);
            // 存储相对路径，方便前端访问
            page.setImagePath(chapterPath + "/" + fileName); 
            page.setCreateTime(Instant.now());
            
            pageMapper.insert(page);
        }
    }

    @Override
    public ReaderVO getChapterForReading(Long chapterId) {
        // 1. 获取当前章节信息
        Chapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null) {
            throw new RuntimeException("章节不存在");
        }

        ReaderVO readerVO = new ReaderVO();
        
        // 2. 组装 ChapterVO
        ChapterVO chapterVO = new ChapterVO();
        BeanUtils.copyProperties(chapter, chapterVO);
        if (chapter.getCreateTime() != null) {
            chapterVO.setCreateTime(chapter.getCreateTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        
        // 漫画简要信息
        Comic comic = comicMapper.selectById(chapter.getComic().getId());
        if (comic != null) {
            ChapterVO.ComicSimpleInfo comicInfo = new ChapterVO.ComicSimpleInfo();
            BeanUtils.copyProperties(comic, comicInfo);
            chapterVO.setComic(comicInfo);
        }

        // 3. 获取页面列表
        List<Page> pages = pageMapper.selectByChapterId(chapterId);
        List<PageVO> pageVOs = pages.stream().map(page -> {
            PageVO vo = new PageVO();
            BeanUtils.copyProperties(page, vo);
            
            // 智能处理图片路径：如果是完整URL则直接使用，否则添加前缀
            String path = page.getImagePath();
            if (path != null && (path.startsWith("http://") || path.startsWith("https://"))) {
                vo.setImageUrl(path);
            } else {
                vo.setImageUrl("/images/" + path);
            }

            return vo;
        }).collect(Collectors.toList());
        
        readerVO.setPages(pageVOs);

        // 4. 组装导航信息 (上一章/下一章)
        ChapterVO.ChapterNavInfo nav = new ChapterVO.ChapterNavInfo();
        List<Chapter> allChapters = chapterMapper.selectByComicId(chapter.getComic().getId());
        // 确保按章节号排序
        allChapters.sort(Comparator.comparingInt(Chapter::getChapterNumber));
        
        int currentIndex = -1;
        for (int i = 0; i < allChapters.size(); i++) {
            if (allChapters.get(i).getId().equals(chapterId)) {
                currentIndex = i;
                break;
            }
        }
        
        nav.setTotalChapters(allChapters.size());
        nav.setCurrentIndex(currentIndex);
        
        if (currentIndex > 0) {
            Chapter prev = allChapters.get(currentIndex - 1);
            nav.setPrevChapterId(prev.getId());
            nav.setPrevChapterNumber(prev.getChapterNumber());
            nav.setPrevChapterTitle(prev.getTitle());
        }
        
        if (currentIndex < allChapters.size() - 1) {
            Chapter next = allChapters.get(currentIndex + 1);
            nav.setNextChapterId(next.getId());
            nav.setNextChapterNumber(next.getChapterNumber());
            nav.setNextChapterTitle(next.getTitle());
        }
        
        chapterVO.setNav(nav);
        readerVO.setCurrentChapter(chapterVO);

        return readerVO;
    }

    @Override
    public void deleteChapter(Long chapterId) {
        // 1. 删除页面记录 (实际项目中可能还需要删除物理文件)
        pageMapper.deleteByChapterId(chapterId);
        
        // 2. 删除章节记录
        chapterMapper.delete(chapterId);
    }
}
