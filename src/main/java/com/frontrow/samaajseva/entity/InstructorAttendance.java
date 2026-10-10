package com.frontrow.samaajseva.entity;

import com.frontrow.samaajseva.enums.SessionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity(name = "instructor_attendance")
@Table(name = "instructor_attendance")
@Audited
public class InstructorAttendance extends CoreEntity {

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false)
    private SessionType sessionType;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;
}
