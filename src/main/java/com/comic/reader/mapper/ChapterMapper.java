package com.comic.reader.mapper;

import com.comic.reader.entity.Chapter;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChapterMapper {

    @Select("SELECT * FROM chapter WHERE comic_id = #{comicId} ORDER BY chapter_number")
    List<Chapter> selectByComicId(Long comicId);

    @Select("SELECT * FROM chapter WHERE comic_id = #{comicId} AND chapter_number = #{chapterNumber}")
    Chapter selectByComicAndNumber(@Param("comicId") Long comicId,
                                   @Param("chapterNumber") Integer chapterNumber);

    @Select("SELECT MAX(chapter_number) FROM chapter WHERE comic_id = #{comicId}")
    Integer selectMaxChapterNumber(Long comicId);

    @Select("SELECT * FROM chapter WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "chapterNumber", column = "chapter_number"),
        @Result(property = "title", column = "title"),
        @Result(property = "pageCount", column = "page_count"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "comic.id", column = "comic_id")
    })
    Chapter selectById(Long id);

    @Insert("INSERT INTO chapter(comic_id, chapter_number, title, page_count, create_time) " +
            "VALUES(#{comic.id}, #{chapterNumber}, #{title}, #{pageCount}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Chapter chapter);

    @Update("UPDATE chapter SET title = #{title}, page_count = #{pageCount} WHERE id = #{id}")
    void update(Chapter chapter);

    @Delete("DELETE FROM chapter WHERE id = #{id}")
    void delete(Long id);
}
