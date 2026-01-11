package com.comic.reader.service.impl;

import com.aliyun.oss.OSS;
import com.comic.reader.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
@Service
@Primary // 标记为首选实现，覆盖 MinioStorageService
public class AliOssStorageService implements StorageService {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    
    @Value("${aliyun.oss.domain:}")
    private String domain;

    @Override
    public String upload(MultipartFile file, String path) {
        try {
            return uploadStream(file.getInputStream(), path);
        } catch (Exception e) {
            throw new RuntimeException("OSS upload failed", e);
        }
    }

    @Override
    public String upload(File file, String path) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return uploadStream(fis, path);
        } catch (Exception e) {
            throw new RuntimeException("OSS upload failed", e);
        }
    }

    @Override
    public String upload(byte[] data, String contentType, String path) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            // OSS 可以自动识别 ContentType，或者通过 ObjectMetadata 设置
            // 这里简化处理，直接上传流
            return uploadStream(bais, path);
        } catch (Exception e) {
            throw new RuntimeException("OSS upload failed", e);
        }
    }

    private String uploadStream(InputStream stream, String path) {
        // 确保路径不以 / 开头
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        ossClient.putObject(bucketName, path, stream);

        // 返回访问 URL
        if (domain != null && !domain.isEmpty()) {
            // 如果配置了自定义域名 (如 CDN)
            if (!domain.startsWith("http")) {
                return "https://" + domain + "/" + path;
            }
            return domain + "/" + path;
        }
        
        // 默认域名格式: https://bucket-name.endpoint/path
        return "https://" + bucketName + "." + endpoint + "/" + path;
    }
}
