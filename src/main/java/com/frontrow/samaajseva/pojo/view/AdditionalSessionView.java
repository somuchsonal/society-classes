package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdditionalSessionView extends BaseView {

    private LocalDate startDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Long categoryId;

    private Long instructorId;

    private String link;

    private Boolean isCancelled;
}
