package com.comic.reader.mapper;

import com.comic.reader.entity.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Insert("INSERT INTO category(name, description, create_time) VALUES(#{name}, #{description}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);

    @Select("SELECT * FROM category")
    List<Category> selectAll();
    
    @Select("SELECT * FROM category WHERE id = #{id}")
    Category selectById(Long id);
}
