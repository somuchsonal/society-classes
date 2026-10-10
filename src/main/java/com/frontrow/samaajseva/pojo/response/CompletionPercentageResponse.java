package com.frontrow.samaajseva.pojo.response;

import com.frontrow.samaajseva.enums.Level;

public interface CompletionPercentageResponse {

    Integer getTotalTopicCount();

    Level getLevel();

    Integer getTotalCompletionCount();

}
