package com.comic.reader.vo;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChapterVO {
    private Long id;
    private Integer chapterNumber;
    private String title;
    private Integer pageCount;
    private LocalDateTime createTime;

    // 漫画信息
    private ComicSimpleInfo comic;

    // 页面列表
    private List<PageSimpleVO> pages;

    // 导航信息
    private ChapterNavInfo nav;

    @Data
    public static class ComicSimpleInfo {
        private Long id;
        private String title;
        private String author;
        private String coverImage;
    }

    @Data
    public static class PageSimpleVO {
        private Long id;
        private Integer pageNumber;
        private String imageUrl;     // 完整的图片URL
        private Integer width;       // 图片宽度
        private Integer height;      // 图片高度
    }

    @Data
    public static class ChapterNavInfo {
        private Long prevChapterId;      // 上一章ID
        private Integer prevChapterNumber; // 上一章编号
        private String prevChapterTitle; // 上一章标题

        private Long nextChapterId;      // 下一章ID
        private Integer nextChapterNumber; // 下一章编号
        private String nextChapterTitle; // 下一章标题

        private Integer totalChapters;   // 总章节数
        private Integer currentIndex;    // 当前章节索引
    }
}