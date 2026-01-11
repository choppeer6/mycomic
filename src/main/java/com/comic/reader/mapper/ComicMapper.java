package com.comic.reader.mapper;

import com.comic.reader.entity.Comic;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ComicMapper {

    @Insert("INSERT INTO comic(title, author, description, cover_image, category_id, status, create_time, update_time) " +
            "VALUES(#{title},#{author},#{description},#{coverImage},#{categoryId.id},#{status},#{createTime},#{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Comic comic);

    @Select("SELECT c.*, cat.id as cat_id, cat.name as cat_name FROM comic c " +
            "LEFT JOIN category cat ON c.category_id = cat.id " +
            "WHERE c.id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "author", column = "author"),
        @Result(property = "description", column = "description"),
        @Result(property = "coverImage", column = "cover_image"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "categoryId.id", column = "cat_id"),
        @Result(property = "categoryId.name", column = "cat_name")
    })
    Comic selectById(Long id);

    @Select("<script>" +
            "SELECT c.*, cat.id as cat_id, cat.name as cat_name FROM comic c " +
            "LEFT JOIN category cat ON c.category_id = cat.id " +
            "<where>" +
            "  <if test='keyword != null and keyword != \"\"'>" +
            "    AND c.title LIKE CONCAT('%', #{keyword}, '%')" +
            "  </if>" +
            "</where>" +
            "ORDER BY c.update_time DESC" +
            "</script>")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "author", column = "author"),
        @Result(property = "description", column = "description"),
        @Result(property = "coverImage", column = "cover_image"),
        @Result(property = "status", column = "status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "categoryId.id", column = "cat_id"),
        @Result(property = "categoryId.name", column = "cat_name")
    })
    List<Comic> selectByKeyword(@Param("keyword") String keyword);

    @Update("UPDATE comic SET " +
            "title = #{title}, " +
            "author = #{author}, " +
            "description = #{description}, " +
            "cover_image = #{coverImage}, " +
            "category_id = #{categoryId.id}, " +
            "status = #{status}, " +
            "update_time = #{updateTime} " +
            "WHERE id = #{id}")
    void update(Comic comic);

    @Delete("DELETE FROM comic WHERE id = #{id}")
    void delete(Long id);
}
