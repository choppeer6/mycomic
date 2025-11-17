package com.comic.reader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "漫画阅读系统已启动！时间: " + LocalDateTime.now();
    }

    @GetMapping("/test")
    public String test() {
        return "测试接口正常！";
    }


}