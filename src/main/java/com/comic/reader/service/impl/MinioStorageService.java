package com.comic.reader.service.impl;

import com.comic.reader.service.StorageService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetBucketPolicyArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
@Service
public class MinioStorageService implements StorageService {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    @PostConstruct
    public void init() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                
                // 设置桶策略为公开只读
                String policy = "{\n" +
                        "    \"Version\": \"2012-10-17\",\n" +
                        "    \"Statement\": [\n" +
                        "        {\n" +
                        "            \"Effect\": \"Allow\",\n" +
                        "            \"Principal\": {\n" +
                        "                \"AWS\": [\n" +
                        "                    \"*\"\n" +
                        "                ]\n" +
                        "            },\n" +
                        "            \"Action\": [\n" +
                        "                \"s3:GetObject\"\n" +
                        "            ],\n" +
                        "            \"Resource\": [\n" +
                        "                \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                        "            ]\n" +
                        "        }\n" +
                        "    ]\n" +
                        "}";
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
                log.info("MinIO Bucket '{}' created and policy set to public read.", bucketName);
            }
        } catch (Exception e) {
            log.error("MinIO init failed", e);
        }
    }

    @Override
    public String upload(MultipartFile file, String path) {
        try {
            return uploadStream(file.getInputStream(), file.getSize(), file.getContentType(), path);
        } catch (Exception e) {
            throw new RuntimeException("MinIO upload failed", e);
        }
    }

    @Override
    public String upload(File file, String path) {
        try (FileInputStream fis = new FileInputStream(file)) {
            // 简单的 ContentType 推断
            String contentType = "application/octet-stream";
            if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) contentType = "image/jpeg";
            else if (file.getName().endsWith(".png")) contentType = "image/png";
            else if (file.getName().endsWith(".webp")) contentType = "image/webp";
            
            return uploadStream(fis, file.length(), contentType, path);
        } catch (Exception e) {
            throw new RuntimeException("MinIO upload failed", e);
        }
    }

    @Override
    public String upload(byte[] data, String contentType, String path) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            return uploadStream(bais, data.length, contentType, path);
        } catch (Exception e) {
            throw new RuntimeException("MinIO upload failed", e);
        }
    }

    private String uploadStream(InputStream stream, long size, String contentType, String path) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(path)
                        .stream(stream, size, -1)
                        .contentType(contentType)
                        .build());

        return endpoint + "/" + bucketName + "/" + path;
    }
}
