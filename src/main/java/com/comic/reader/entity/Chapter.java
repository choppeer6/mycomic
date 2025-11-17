package com.comic.reader.entity;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "chapter")
@Data
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comic_id", nullable = false)
    private Comic comic;

    @Column(name = "chapter_number", nullable = false)
    private Integer chapterNumber;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @ColumnDefault("0")
    @Column(name = "page_count")
    private Integer pageCount;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "create_time")
    private Instant createTime;

    @OneToMany(mappedBy = "chapter")
    private Set<Page> pages = new LinkedHashSet<>();


}
