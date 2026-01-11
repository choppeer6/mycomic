package com.comic.reader.service.impl;

import com.comic.reader.service.ChapterService;
import com.comic.reader.service.ComicService;
import com.comic.reader.dto.ComicRequest;
import com.comic.reader.dto.ComicResponse;
import com.comic.reader.dto.query.ComicQuery;
import com.comic.reader.dto.response.ComicDetailResponse;
import com.comic.reader.entity.Category;
import com.comic.reader.entity.Chapter;
import com.comic.reader.entity.Comic;
import com.comic.reader.mapper.ChapterMapper;
import com.comic.reader.mapper.ComicMapper;
import com.comic.reader.util.page.PageResult;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Comparator;
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
    @Autowired
    private ChapterMapper chapterMapper;


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
        if (comic == null) {
            return null;
        }
        ComicDetailResponse comicDetailResponse = new ComicDetailResponse();
        BeanUtils.copyProperties(comic, comicDetailResponse);

        // 时间类型转换 Instant -> LocalDateTime
        if (comic.getCreateTime() != null) {
            comicDetailResponse.setCreateTime(comic.getCreateTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }
        if (comic.getUpdateTime() != null) {
            comicDetailResponse.setUpdateTime(comic.getUpdateTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
        }

        // 处理分类信息
        if (comic.getCategoryId() != null) {
            ComicDetailResponse.CategoryInfo categoryInfo = new ComicDetailResponse.CategoryInfo();
            categoryInfo.setId(comic.getCategoryId().getId());
            categoryInfo.setName(comic.getCategoryId().getName());
            comicDetailResponse.setCategory(categoryInfo);
        }

        // 获取章节列表
        List<Chapter> chapters = chapterMapper.selectByComicId(id);
        
        // 转换章节信息并排序
        List<ComicDetailResponse.ChapterSimpleVO> chapterVOs = chapters.stream()
                .sorted(Comparator.comparing(Chapter::getChapterNumber))
                .map(chapter -> {
                    ComicDetailResponse.ChapterSimpleVO vo = new ComicDetailResponse.ChapterSimpleVO();
                    BeanUtils.copyProperties(chapter, vo);
                    if (chapter.getCreateTime() != null) {
                        vo.setCreateTime(chapter.getCreateTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    }
                    return vo;
                }).collect(Collectors.toList());

        comicDetailResponse.setChapters(chapterVOs);

        // 统计信息
        comicDetailResponse.setChapterCount(chapters.size());
        comicDetailResponse.setTotalPages(chapters.stream().mapToInt(c -> c.getPageCount() == null ? 0 : c.getPageCount()).sum());
        
        if (!chapters.isEmpty()) {
            Chapter latest = chapters.get(chapters.size() - 1);
            comicDetailResponse.setLatestChapter(latest.getTitle());
            if (latest.getCreateTime() != null) {
                comicDetailResponse.setLatestUpdateTime(latest.getCreateTime().atZone(ZoneId.systemDefault()).toLocalDateTime());
            }
        }

        return comicDetailResponse;
    }


    // 添加
    @Override
    public void addComic(ComicRequest request) {
        Comic comic = new Comic();
        BeanUtils.copyProperties(request, comic);
        
        // 设置分类
        if (request.getCategoryId() != null) {
            Category category = new Category();
            category.setId(request.getCategoryId());
            comic.setCategoryId(category);
        }
        
        // 设置默认状态
        if (comic.getStatus() == null) {
            comic.setStatus(1);
        }
        
        comicMapper.insert(comic);
    }


    // 修改
    @Override
    public void updateComic(Long id, ComicRequest request) {

        Comic comic = comicMapper.selectById(id);
        if (comic != null) {
            BeanUtils.copyProperties(request, comic);
            
            // 设置分类
            if (request.getCategoryId() != null) {
                Category category = new Category();
                category.setId(request.getCategoryId());
                comic.setCategoryId(category);
            }

            comicMapper.update(comic);
        }
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