package com.frontrow.samaajseva.pojo.response;

import com.frontrow.samaajseva.enums.Attendance;
import com.frontrow.samaajseva.enums.SessionType;

import java.time.LocalDate;

public interface KidAttendedResponse {

    Long getKidId();

    Long getSessionId();

    SessionType getSessionType();

    LocalDate getSessionDate();

    Boolean getIsSessionCancelled();

    Attendance getAttendance();

    Long getAttendanceId();

}
