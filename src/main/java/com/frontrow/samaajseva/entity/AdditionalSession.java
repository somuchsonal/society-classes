package com.frontrow.samaajseva.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity(name = "additional_session")
@Table(name = "additional_session")
@Audited
public class AdditionalSession extends CoreEntity {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "is_cancelled", nullable = false)
    private Boolean isCancelled = false;
}
