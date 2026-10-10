package com.frontrow.samaajseva.entity;

import com.frontrow.samaajseva.enums.Attendance;
import com.frontrow.samaajseva.enums.SessionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity(name = "kid_attendance")
@Table(name = "kid_attendance")
@Audited
public class KidAttendance extends CoreEntity {

    @Column(name = "kid_id", nullable = false)
    private Long kidId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance", nullable = false)
    private Attendance attendance;
}
