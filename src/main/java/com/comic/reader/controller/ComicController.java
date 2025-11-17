package com.comic.reader.controller;
// 需要添加这个import语句
import com.comic.reader.Service.ComicService;
import com.comic.reader.dto.ComicRequest;
import com.comic.reader.dto.ComicResponse;
import com.comic.reader.dto.query.ComicQuery;
import com.comic.reader.dto.response.ComicDetailResponse;
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
    public Result listComics(@RequestParam(required = false)ComicQuery comicQuery) {

        try {
            PageResult<ComicResponse> comics = comicService.getComics(comicQuery);
            return Result.success(comics);
        } catch (Exception e) {
            return Result.error("漫画列表获取失败"+ e.getMessage());
        }
    }

    @GetMapping("/{id}")         // 漫画详情
    public Result getComicDetail(@PathVariable Long id) {
        log.info("获取漫画详情: {}", id);
        try {
            comicService.getComicDetail(id);
            return Result.success(comicService.getComicDetail(id));
        }
        catch (Exception e) {
            return Result.error("漫画详情获取失败"+ e.getMessage());
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
