package com.comic.reader.util;

import org.springframework.web.multipart.MultipartFile;

// FileUtil.java - 文件工具类
public class FileUtil {
    public static String generateFileName(String originalFileName) {
        return System.currentTimeMillis() + "_" + originalFileName;
    }
    public static boolean isImageFile(MultipartFile file) {
        return file.getContentType().startsWith("image/");
    }
    public static void saveFile(MultipartFile file, String path) {
        try {
            file.transferTo(new java.io.File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
