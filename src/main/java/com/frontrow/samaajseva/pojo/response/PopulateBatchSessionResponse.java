package com.frontrow.samaajseva.pojo.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public interface PopulateBatchSessionResponse {

    Long getBatchId();

    DayOfWeek getDayOfWeek();

    LocalTime getStartTime();

    LocalTime getEndTime();

    String getVenue();

    Date getScheduleCreatedAt();

    Long getInstructorId();

    LocalDate getPopulatedTill();

    LocalDate getMembershipsValidTill();

    Date getKidsAddedAt();

    Integer getMembershipsValidForMonths();
}
