package com.comic.reader.vo;

import lombok.Data;
import java.time.LocalDateTime;

//漫画列表视图
@Data
public class ComicVO {
    private Long id;
    private String title;
    private String author;
    private String coverUrl;
    private String description;
    private Integer status;

    // 统计信息
    private Integer chapterCount;
    private Integer totalPages;
    private String latestChapter;
    private LocalDateTime latestUpdateTime;

    // 分类信息
    private String categoryName;

    // 格式化信息（用于前端显示）
    public String getStatusText() {
        return status == 1 ? "连载中" : "已完结";
    }

    public String getUpdateTimeText() {
        // 这里可以格式化时间显示
        return latestUpdateTime.toString();
    }
}