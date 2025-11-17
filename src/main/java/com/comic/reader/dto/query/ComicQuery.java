package com.comic.reader.dto.query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//漫画查询条件
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComicQuery {
    private String keyword;      // 搜索关键词
    private Long categoryId;     // 分类ID
    private Integer status;      // 状态
    private String author;       // 作者
    private Integer pageNum = 1; // 页码
    private Integer pageSize = 10; // 每页大小

    // 计算偏移量
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
