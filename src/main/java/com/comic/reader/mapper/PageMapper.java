package com.comic.reader.mapper;

import com.comic.reader.entity.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PageMapper {

    @Select("SELECT * FROM page WHERE chapter_id = #{chapterId} ORDER BY page_number")
    List<Page> selectByChapterId(Long chapterId);

    @Insert("INSERT INTO page(chapter_id, page_number, image_path, create_time) " +
            "VALUES(#{chapter.id}, #{pageNumber}, #{imagePath}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Page page);

    @Delete("DELETE FROM page WHERE chapter_id = #{chapterId}")
    void deleteByChapterId(Long chapterId);
}
