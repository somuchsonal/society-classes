package com.frontrow.samaajseva.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.List;

@Getter
@Setter
@Entity(name = "batch")
@Table(name = "batch")
@Audited
public class Batch extends CoreEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @Column(name = "society_id", insertable = false, updatable = false)
    private Long societyId;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @NotAudited
    private List<BatchSchedule> batchScheduleList;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
