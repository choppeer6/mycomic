package com.comic.reader.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    /**
     * 健康检查接口
     * GET /health
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", LocalDateTime.now());
        result.put("service", "漫画阅读系统");
        result.put("version", "1.0.0");
        return result;
    }
}