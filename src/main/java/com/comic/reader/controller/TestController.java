package com.comic.reader.controller;

import com.comic.reader.entity.Category;
import com.comic.reader.entity.Chapter;
import com.comic.reader.entity.Comic;
import com.comic.reader.entity.Page;
import com.comic.reader.mapper.CategoryMapper;
import com.comic.reader.mapper.ChapterMapper;
import com.comic.reader.mapper.ComicMapper;
import com.comic.reader.mapper.PageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
public class TestController {

    @Autowired
    private ComicMapper comicMapper;
    
    @Autowired
    private ChapterMapper chapterMapper;
    
    @Autowired
    private PageMapper pageMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/api/test/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            List<Category> categories = categoryMapper.selectAll();
            List<Comic> comics = comicMapper.selectByKeyword(null);
            
            status.put("categoryCount", categories.size());
            status.put("comicCount", comics.size());
            status.put("categories", categories); // 看看具体有哪些分类
        } catch (Exception e) {
            status.put("error", e.getMessage());
        }
        return status;
    }

    @GetMapping("/api/test/init-data")
    @Transactional
    public String initData() {
        // 1. 初始化分类
        String[] categoryNames = {"热血", "恋爱", "冒险", "搞笑", "科幻", "悬疑"};
        List<Long> categoryIds = new ArrayList<>();
        
        List<Category> existingCategories = categoryMapper.selectAll();
        if (existingCategories.isEmpty()) {
            for (String name : categoryNames) {
                Category category = new Category();
                category.setName(name);
                category.setDescription(name + "类漫画");
                category.setCreateTime(Instant.now());
                categoryMapper.insert(category);
                categoryIds.add(category.getId());
            }
        } else {
            for (Category c : existingCategories) {
                categoryIds.add(c.getId());
            }
        }

        // 2. 初始化漫画
        // 先检查是否已经有漫画了，避免重复添加
        List<Comic> existingComics = comicMapper.selectByKeyword(null);
        if (!existingComics.isEmpty()) {
            return "数据库中已有 " + existingComics.size() + " 部漫画，跳过初始化。";
        }

        String[] titles = {"海贼王", "火影忍者", "进击的巨人", "鬼灭之刃", "咒术回战", "一拳超人", "名侦探柯南", "龙珠超"};
        String[] authors = {"尾田荣一郎", "岸本齐史", "谏山创", "吾峠呼世晴", "芥见下下", "ONE/村田雄介", "青山刚昌", "鸟山明"};
        String[] covers = {
            "https://img1.baidu.com/it/u=3480390676,3339476936&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=707",
            "https://img2.baidu.com/it/u=2634863674,1382474966&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=750",
            "https://img1.baidu.com/it/u=1966616150,2146512490&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=707",
            "https://img2.baidu.com/it/u=3681880960,3449638528&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=708",
            "https://img0.baidu.com/it/u=251610436,1938636725&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=707",
            "https://img1.baidu.com/it/u=372960649,343328429&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=713",
            "https://img2.baidu.com/it/u=156458316,4220896020&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=709",
            "https://img0.baidu.com/it/u=3927455211,1927673753&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=707"
        };

        Random random = new Random();

        for (int i = 0; i < titles.length; i++) {
            // 创建漫画
            Comic comic = new Comic();
            comic.setTitle(titles[i]);
            comic.setAuthor(authors[i]);
            comic.setDescription(titles[i] + " 是一部非常精彩的漫画，讲述了主角的热血冒险故事。");
            comic.setCoverImage(covers[i]);
            comic.setStatus(1); // 连载中
            comic.setCreateTime(Instant.now());
            comic.setUpdateTime(Instant.now());
            
            // 随机分配一个分类
            if (!categoryIds.isEmpty()) {
                Category category = new Category();
                category.setId(categoryIds.get(random.nextInt(categoryIds.size())));
                comic.setCategoryId(category);
            }
            
            comicMapper.insert(comic);
            
            // 为每部漫画创建 5-10 个章节
            int chapterCount = random.nextInt(6) + 5;
            for (int j = 1; j <= chapterCount; j++) {
                Chapter chapter = new Chapter();
                chapter.setComic(comic);
                chapter.setChapterNumber(j);
                chapter.setTitle("第 " + j + " 话");
                chapter.setPageCount(5); // 假设每章5页
                chapter.setCreateTime(Instant.now());
                
                chapterMapper.insert(chapter);
                
                // 为每章创建 5 页图片
                for (int k = 1; k <= 5; k++) {
                    Page page = new Page();
                    page.setChapter(chapter);
                    page.setPageNumber(k);
                    // 使用 picsum 生成随机图片
                    page.setImagePath("https://picsum.photos/800/1200?random=" + (i * 100 + j * 10 + k)); 
                    page.setCreateTime(Instant.now());
                    
                    pageMapper.insert(page);
                }
            }
        }

        return "测试数据生成成功！生成了 " + categoryIds.size() + " 个分类和 " + titles.length + " 部漫画。";
    }
}
