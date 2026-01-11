package com.comic.reader.service;

public interface CrawlerService {
    /**
     * 抓取单本漫画 (网络爬虫)
     * @param url 漫画目录页URL
     */
    void crawlComic(String url);

    /**
     * 导入本地漫画
     * 扫描 uploads/import/ 目录下的文件夹
     * 结构: 漫画名/章节名/图片文件
     * @return 导入结果信息
     */
    String importLocalComics();
}
