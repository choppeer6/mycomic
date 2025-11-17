package com.comic.reader.controller;

import com.comic.reader.Service.ChapterService;
import com.comic.reader.dto.ChapterRequest;
import com.comic.reader.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// ChapterController.java - 章节相关接口
@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;



    @GetMapping("/comic/{comicId}")     // 获取漫画的所有章节
    public Result getChaptersByComic(@PathVariable Long comicId) {
        // TODO: 获取漫画的所有章节

        return Result.success(null);
    }

    @PostMapping                        // 添加章节
    public Result addChapter(@ModelAttribute ChapterRequest request) {
        // TODO: 添加章节
        return Result.success(null);
    }

    @GetMapping("/{chapterId}/read")    // 阅读章节
    public Result readChapter(@PathVariable Long chapterId) {
        // TODO: 阅读章节
        return Result.success(null);
    }
}
