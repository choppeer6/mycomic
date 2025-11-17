package com.comic.reader.entity;


import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "page")
@Data
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "page_number", nullable = false)
    private Integer pageNumber;

    @Column(name = "image_path", nullable = false, length = 500)
    private String imagePath;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "create_time")
    private Instant createTime;

}
