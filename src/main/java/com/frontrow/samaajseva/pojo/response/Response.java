package com.frontrow.samaajseva.pojo.response;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Getter
@Setter
public class Response<T> {
    private boolean success;
    private T data;
    private ErrorResponse error;

    @ConstructorProperties({"success", "data", "error"})
    public Response(boolean success, T data, ErrorResponse error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }
}

