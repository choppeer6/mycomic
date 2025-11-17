package com.comic.reader.Service.impl;

import com.comic.reader.Service.ChapterService;
import com.comic.reader.Service.ComicService;
import com.comic.reader.dto.ComicRequest;
import com.comic.reader.dto.ComicResponse;
import com.comic.reader.dto.query.ComicQuery;
import com.comic.reader.dto.response.ComicDetailResponse;
import com.comic.reader.entity.Comic;
import com.comic.reader.mapper.ComicMapper;
import com.comic.reader.util.page.PageResult;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// service/impl/ - 接口实现
// ComicServiceImpl.java
@Service
@Transactional
public class ComicServiceImpl implements ComicService {

    @Autowired
    private ComicMapper comicMapper;
    @Autowired
    private ChapterService chapterService;



    // 实现所有接口方法
    @Override
    // 分页查询
    public PageResult<ComicResponse> getComics(ComicQuery query) {
        // 开始分页
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<Comic> comics = comicMapper.selectByKeyword(query.getKeyword());
        List<ComicResponse> comicResponses = comics.stream().map(comic -> {
            ComicResponse comicResponse = new ComicResponse();
            BeanUtils.copyProperties(comic, comicResponse);
            return comicResponse;
        }).collect(Collectors.toList());
        return new PageResult<>((long) comics.size(), comicResponses);
    }


    // 查询详情
    @Override
    public ComicDetailResponse getComicDetail(Long id) {
        Comic comic = comicMapper.selectById(id);
        ComicDetailResponse comicDetailResponse = new ComicDetailResponse();
        BeanUtils.copyProperties(comic, comicDetailResponse);

        //TODO: 获取章节列表
        comicDetailResponse.setChapters(null);
        return comicDetailResponse;
    }


    // 添加
    @Override
    public void addComic(ComicRequest request) {
        Comic comic = new Comic();
        BeanUtils.copyProperties(request, comic);
        comicMapper.insert(comic);
    }


    // 修改
    @Override
    public void updateComic(Long id, ComicRequest request) {

        Comic comic = comicMapper.selectById(id);
        BeanUtils.copyProperties(request, comic);
        comicMapper.update(comic);
    }


    // 删除
    @Override
    public void deleteComic(Long id) {

        comicMapper.delete(id);
        chapterService.deleteChapter(id);
    }
    // 实现所有接口方法
    // 包含业务逻辑验证
    // 调用Mapper进行数据操作
}