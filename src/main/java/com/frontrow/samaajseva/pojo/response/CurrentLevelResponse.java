package com.frontrow.samaajseva.pojo.response;

import com.frontrow.samaajseva.enums.Level;

public interface CurrentLevelResponse {

    Level getLevel();

    Integer getSubLevel();

    Integer getSubSubLevel();
}
