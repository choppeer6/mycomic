package com.comic.reader.mapper;

import com.comic.reader.entity.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

// ChapterMapper.java
@Mapper
public interface ChapterMapper {

    List<Chapter> selectByComicId(Long comicId);

    Chapter selectByComicAndNumber(@Param("comicId") Long comicId,
                                   @Param("chapterNumber") Integer chapterNumber);

    Integer selectMaxChapterNumber(Long comicId);

    // 其他CRUD方法
}