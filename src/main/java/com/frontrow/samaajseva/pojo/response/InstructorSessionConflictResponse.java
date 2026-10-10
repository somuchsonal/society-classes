package com.frontrow.samaajseva.pojo.response;

import com.frontrow.samaajseva.enums.SessionType;

public interface InstructorSessionConflictResponse {

    Long getSessionId();

    SessionType getSessionType();
}
