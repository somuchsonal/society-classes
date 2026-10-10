package com.frontrow.samaajseva.pojo.response;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Getter
@Setter
public class ErrorResponse {
    private int code;
    private String type;
    private String message;

    @ConstructorProperties({"errorCode", "type", "message"})
    public ErrorResponse(int code, String type, String message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }
}
