package com.comic.reader.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

// MyBatisConfig.java - MyBatis配置
@Configuration
@MapperScan("com.comic.reader.mapper")
public class MyBatisConfig {
    // MyBatis相关配置
}