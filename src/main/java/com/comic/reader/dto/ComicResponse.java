package com.comic.reader.dto;

import lombok.Data;

@Data
public class ComicResponse {
    private Long id;
    private String title;
    private String author;
    private String coverImage;
    private Integer chapterCount;
    // getter/setter
}