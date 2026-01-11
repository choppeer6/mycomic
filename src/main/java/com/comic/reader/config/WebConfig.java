package com.comic.reader.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传的图片目录
        // 注意：file: 前缀表示文件系统路径
        // 如果 uploadDir 是相对路径，需要转换为绝对路径或者确保它相对于工作目录正确
        String path = "file:" + uploadDir;
        if (!path.endsWith("/")) {
            path += "/";
        }
        registry.addResourceHandler("/images/**")
                .addResourceLocations(path);
                
        // 映射静态资源
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 将根路径转发到 index.html
        registry.addViewController("/").setViewName("forward:/templates/index.html");
    }
}
