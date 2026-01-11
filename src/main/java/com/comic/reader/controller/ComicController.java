package com.comic.reader.controller;

import com.comic.reader.service.ComicService;
import com.comic.reader.dto.ComicRequest;
import com.comic.reader.dto.ComicResponse;
import com.comic.reader.dto.query.ComicQuery;
import com.comic.reader.util.page.PageResult;
import com.comic.reader.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/comics")
public class ComicController {

    @Autowired
    private ComicService comicService;

    @GetMapping("/list")         // 漫画列表
    // 去掉 @RequestParam，因为 ComicQuery 是一个对象，Spring 会自动尝试从查询参数中绑定字段
    // 如果加上 @RequestParam，Spring 会试图寻找名为 "comicQuery" 的参数，或者需要自定义转换器
    // 对于对象参数绑定，通常不加注解，或者使用 @ModelAttribute
    public Result listComics(ComicQuery comicQuery) {
        try {
            // 如果参数为空，创建一个默认对象
            if (comicQuery == null) {
                comicQuery = new ComicQuery();
            }
            // 确保分页参数有默认值 (虽然 ComicQuery 类定义里有默认值，但为了保险起见)
            if (comicQuery.getPageNum() == null) comicQuery.setPageNum(1);
            if (comicQuery.getPageSize() == null) comicQuery.setPageSize(10);

            PageResult<ComicResponse> comics = comicService.getComics(comicQuery);
            return Result.success(comics);
        } catch (Exception e) {
            log.error("获取漫画列表失败", e);
            return Result.error("漫画列表获取失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")         // 漫画详情
    public Result getComicDetail(@PathVariable Long id) {
        log.info("获取漫画详情: {}", id);
        try {
            return Result.success(comicService.getComicDetail(id));
        }
        catch (Exception e) {
            log.error("获取漫画详情失败", e);
            return Result.error("漫画详情获取失败: "+ e.getMessage());
        }
    }

    @PostMapping                 // 添加漫画
    public Result addComic(@RequestBody ComicRequest request) {
        comicService.addComic(request);
        return Result.success();
    }

    @PutMapping("/{id}")         // 更新漫画
    public Result updateComic(@PathVariable Long id, @RequestBody ComicRequest request) {
        comicService.updateComic(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")      // 删除漫画
    public Result deleteComic(@PathVariable Long id) {
        comicService.deleteComic(id);
        return Result.success();
    }
}
