package com.frontrow.samaajseva.pojo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.frontrow.samaajseva.entity.Batch;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PopulateBatchSessionSerializable {

    Batch batch;

    Long batchId;

    Date scheduleCreatedAt;

    DayOfWeek dayOfWeek;

    LocalTime startTime;

    LocalTime endTime;

    String venue;

    Long instructorId;

    LocalDate populatedTill;

    LocalDate membershipsValidTill;

    Date kidsAddedAt;

    Integer membershipsValidForMonths;
}
