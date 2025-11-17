package com.comic.reader.vo;

import lombok.Data;

import java.util.List;

//阅读器视图
@Data
public class ReaderVO {
    private ChapterVO currentChapter;
    private List<PageVO> pages;

    // 阅读设置
    private ReaderConfig readerConfig;

    @Data
    public static class ReaderConfig {
        private String readDirection; // 阅读方向: ltr, rtl, vertical
        private Boolean fullScreen;   // 是否全屏
        private Boolean showHeader;   // 是否显示头部
        private Integer imageQuality; // 图片质量
    }
}