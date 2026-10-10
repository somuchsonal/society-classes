package com.frontrow.samaajseva.pojo.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.enums.CancellationReason;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionView extends BaseView {

    private LocalDate startDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private BatchView batchView;

    private Long instructorId;

    private String venue;

    private Integer minutesDelayed;

    private Boolean isCancelled;

    private CancellationReason cancellationReason;

}
