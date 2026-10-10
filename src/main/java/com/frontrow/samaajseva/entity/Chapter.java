package com.frontrow.samaajseva.entity;

import com.frontrow.samaajseva.enums.ChapterType;
import com.frontrow.samaajseva.enums.Level;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity(name = "chapter")
@Table(name = "chapter")
@Audited
public class Chapter extends CoreEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private Level level;

    @Column(name = "sub_level", nullable = false)
    private Integer subLevel;

    @Column(name = "sub_sub_level", nullable = false)
    private Integer subSubLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "chapter_type", nullable = false)
    private ChapterType chapterType;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
