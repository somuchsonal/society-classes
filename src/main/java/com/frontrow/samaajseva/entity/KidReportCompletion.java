package com.frontrow.samaajseva.entity;

import com.frontrow.samaajseva.enums.SessionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "kid_report_completion")
@Table(name = "kid_report_completion")
public class KidReportCompletion extends CoreEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kid_id", nullable = false)
    private Kid kid;

    @Column(name = "kid_id", insertable = false, updatable = false)
    private Long kidId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "chapter_id", insertable = false, updatable = false)
    private Long chapterId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

}
