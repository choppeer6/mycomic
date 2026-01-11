package com.comic.reader.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;

public interface StorageService {
    /**
     * 上传 MultipartFile
     * @param file 文件
     * @param path 存储路径 (例如: comics/1/chapter/1/001.jpg)
     * @return 文件的完整访问 URL
     */
    String upload(MultipartFile file, String path);

    /**
     * 上传本地 File
     * @param file 本地文件
     * @param path 存储路径
     * @return 文件的完整访问 URL
     */
    String upload(File file, String path);
    
    /**
     * 上传字节数组 (用于爬虫下载的流)
     * @param data 字节数组
     * @param contentType 文件类型
     * @param path 存储路径
     * @return 文件的完整访问 URL
     */
    String upload(byte[] data, String contentType, String path);
}
