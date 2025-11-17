package com.comic.reader.mapper;

import com.comic.reader.entity.Comic;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ComicMapper {

    @Insert("INSERT INTO comic(title, author, description, cover_image, category_id, status, create_time, update_time) " +
            "VALUES(#{titlw},#{author},#{description},#{coverImage},#{category},#{status},#{createTime},#{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Comic comic);

    @Select("SELECT * FROM comic WHERE id = #{id}")
    Comic selectById(Long id);

    @Select("SELECT * FROM comic WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<Comic> selectByKeyword(String keyword);

    @Update("UPDATE comic SET " +
            "title = #{title}, " +
            "author = #{author}, " +
            "description = #{description}, " +
            "cover_image = #{coverImage}, " +
            "category_id = #{category}, " +
            "status = #{status}, " +
            "update_time = #{updateTime} " +
            "WHERE id = #{id}")
    void update(Comic comic);
    @Delete("DELETE FROM comic WHERE id = #{id}")
    void delete(Long id);
}