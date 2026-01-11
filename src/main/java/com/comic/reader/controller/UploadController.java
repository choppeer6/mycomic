package com.comic.reader.controller;

import com.comic.reader.service.CrawlerService;
import com.comic.reader.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private CrawlerService crawlerService;

    @PostMapping("/image")      // 上传图片
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        //TODO 处理图片上传逻辑
        return Result.success();
    }

    @PostMapping("/batch")      // 批量上传
    public Result batchUpload(@RequestParam("files") MultipartFile[] files) {
        // 处理批量上传逻辑
        //TODO 批量上传逻辑
        return Result.success();
    }
    
    @PostMapping("/import-local")
    public Result importLocal() {
        try {
            String msg = crawlerService.importLocalComics();
            return Result.success(msg);
        } catch (Exception e) {
            return Result.error("导入失败: " + e.getMessage());
        }
    }
}
