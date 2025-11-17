package com.comic.reader.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ChapterResponse {
    private Long id;
    private String title;
    private Integer chapterNumber;
    private Integer pageCount;
    private String comicTitle;
    // getter/setter
}