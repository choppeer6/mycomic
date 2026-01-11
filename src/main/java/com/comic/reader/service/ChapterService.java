package com.comic.reader.service;

import com.comic.reader.dto.ChapterRequest;
import com.comic.reader.dto.ChapterResponse;
import com.comic.reader.vo.ReaderVO;

import java.util.List;

// ChapterService.java
public interface ChapterService {
    List<ChapterResponse> getChaptersByComic(Long comicId);
    void addChapter(ChapterRequest request);
    ReaderVO getChapterForReading(Long chapterId);
    void deleteChapter(Long chapterId);
}