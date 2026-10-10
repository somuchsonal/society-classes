package com.frontrow.samaajseva.pojo.response;

import java.time.LocalDate;

public interface SessionResponse {

    LocalDate getStartDate();

    Long getBatchId();

    Long getCategoryId();
}
