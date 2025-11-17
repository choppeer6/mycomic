package com.comic.reader.util.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
// PageResult.java - 分页结果
public class PageResult<T> {
    private Long total;
    private List<T> list;
    // getter/setter
}