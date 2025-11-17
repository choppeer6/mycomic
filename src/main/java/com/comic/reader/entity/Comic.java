package com.comic.reader.entity;


import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "comic")
@Data
public class Comic {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "author", length = 100)
    private String author;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "cover_image", length = 500)
    private String coverImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category categoryId;

    @ColumnDefault("1")
    @Column(name = "status")
    private Integer status;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "create_time")
    private Instant createTime;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "update_time")
    private Instant updateTime;

    @OneToMany(mappedBy = "comic")
    private Set<Chapter> chapters = new LinkedHashSet<>();

}
