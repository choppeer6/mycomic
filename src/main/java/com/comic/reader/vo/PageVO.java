package com.comic.reader.vo;
import lombok.Data;

@Data
public class PageVO {
    private Long id;
    private Integer pageNumber;
    private String imagePath;
    private String imageUrl;     // 完整的访问URL

    // 图片信息
    private Integer width;
    private Integer height;
    private Long fileSize;
    private String format;       // 图片格式: jpg, png, webp等

    // 章节信息
    private ChapterInfo chapter;

    @Data
    public static class ChapterInfo {
        private Long id;
        private Integer chapterNumber;
        private String title;
        private Long comicId;
        private String comicTitle;
    }
}