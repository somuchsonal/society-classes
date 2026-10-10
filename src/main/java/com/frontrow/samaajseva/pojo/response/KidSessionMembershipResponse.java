package com.frontrow.samaajseva.pojo.response;

import com.frontrow.samaajseva.enums.SessionType;

import java.time.LocalDate;

public interface KidSessionMembershipResponse {

    Long getKidId();

    Long getSessionId();

    LocalDate getSessionDate();

    SessionType getSessionType();

    Long getActiveMembershipId();

    Long getUnusedMembershipId();

    Integer getValidityInMonths();
}
