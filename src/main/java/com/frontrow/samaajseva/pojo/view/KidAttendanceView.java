package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.Attendance;
import com.frontrow.samaajseva.enums.SessionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KidAttendanceView extends BaseView {

    private Long kidId;

    private Long sessionId;

    private SessionType sessionType;

    private Attendance attendance;
}
