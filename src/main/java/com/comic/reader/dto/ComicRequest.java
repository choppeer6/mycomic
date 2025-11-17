package com.comic.reader.dto;

import lombok.Data;

@Data
public class ComicRequest {
    private String title;
    private String author;
    private String description;
    private Long categoryId;
    // 验证注解 + getter/setter
}
