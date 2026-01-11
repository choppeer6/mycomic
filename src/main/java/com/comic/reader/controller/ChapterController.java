package com.comic.reader.controller;

import com.comic.reader.service.ChapterService;
import com.comic.reader.dto.ChapterRequest;
import com.comic.reader.dto.ChapterResponse;
import com.comic.reader.util.Result;
import com.comic.reader.vo.ReaderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @GetMapping("/comic/{comicId}")
    public Result getChaptersByComic(@PathVariable Long comicId) {
        try {
            List<ChapterResponse> chapters = chapterService.getChaptersByComic(comicId);
            return Result.success(chapters);
        } catch (Exception e) {
            return Result.error("获取章节列表失败: " + e.getMessage());
        }
    }

    @PostMapping
    public Result addChapter(ChapterRequest request) { // 注意：这里不加 @RequestBody，因为包含文件上传
        try {
            chapterService.addChapter(request);
            return Result.success();
        } catch (Exception e) {
            log.error("添加章节失败", e);
            return Result.error("添加章节失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/read")
    public Result getChapterForReading(@PathVariable Long id) {
        try {
            ReaderVO readerVO = chapterService.getChapterForReading(id);
            return Result.success(readerVO);
        } catch (Exception e) {
            return Result.error("获取阅读内容失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteChapter(@PathVariable Long id) {
        try {
            chapterService.deleteChapter(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除章节失败: " + e.getMessage());
        }
    }
}
