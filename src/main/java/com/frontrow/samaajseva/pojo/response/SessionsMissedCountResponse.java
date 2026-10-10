package com.frontrow.samaajseva.pojo.response;

import java.time.LocalDate;

public interface SessionsMissedCountResponse {

    Integer getMissedCount();

    LocalDate getMembershipStartDate();
}
