package com.frontrow.samaajseva.pojo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.frontrow.samaajseva.enums.Attendance;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KidAttendedResponseView {

    private Long kidId;

    private Long sessionId;

    private LocalDate sessionDate;

    private Attendance attendance;

}
