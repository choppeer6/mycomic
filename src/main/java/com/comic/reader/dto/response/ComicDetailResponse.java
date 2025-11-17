package com.comic.reader.dto.response;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

//漫画详细信息响应
@Data
public class ComicDetailResponse {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String coverImage;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 分类信息
    private CategoryInfo category;

    // 统计信息
    private Integer chapterCount;    // 章节数量
    private Integer totalPages;      // 总页数
    private String latestChapter;    // 最新章节标题
    private LocalDateTime latestUpdateTime; // 最后更新时间

    // 章节列表（简略信息）
    private List<ChapterSimpleVO> chapters;

    @Data
    public static class CategoryInfo {
        private Long id;
        private String name;
    }

    @Data
    public static class ChapterSimpleVO {
        private Long id;
        private Integer chapterNumber;
        private String title;
        private Integer pageCount;
        private LocalDateTime createTime;
    }
}