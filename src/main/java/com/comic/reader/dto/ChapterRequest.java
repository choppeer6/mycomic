package com.comic.reader.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ChapterRequest {
    private Long comicId;
    private String title;
    private List<MultipartFile> pages; // 页面图片文件
    // getter/setter
}