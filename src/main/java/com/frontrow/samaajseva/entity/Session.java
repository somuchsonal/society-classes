package com.frontrow.samaajseva.entity;

import com.frontrow.samaajseva.enums.CancellationReason;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity(name = "session")
@Table(name = "session")
@Audited
public class Session extends CoreEntity {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Column(name = "batch_id", insertable = false, updatable = false)
    private Long batchId;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @Column(name = "venue", nullable = false)
    private String venue;

    @Column(name = "minutes_delayed")
    private Integer minutesDelayed;

    @Column(name = "is_cancelled", nullable = false)
    private Boolean isCancelled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancellation_reason", nullable = false)
    private CancellationReason cancellationReason;
}
