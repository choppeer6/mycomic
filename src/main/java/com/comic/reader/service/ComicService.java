package com.comic.reader.service;

import com.comic.reader.dto.ComicRequest;
import com.comic.reader.dto.ComicResponse;
import com.comic.reader.dto.query.ComicQuery;
import com.comic.reader.dto.response.ComicDetailResponse;
import com.comic.reader.util.page.PageResult;

public interface ComicService {
    PageResult<ComicResponse> getComics(ComicQuery query);
    ComicDetailResponse getComicDetail(Long id);
    void addComic(ComicRequest request);
    void updateComic(Long id, ComicRequest request);
    void deleteComic(Long id);
}