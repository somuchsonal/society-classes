package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.SessionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstructorAttendanceView extends BaseView {

    private Long instructorId;

    private Long sessionId;

    private SessionType sessionType;

    private Double latitude;

    private Double longitude;
}
