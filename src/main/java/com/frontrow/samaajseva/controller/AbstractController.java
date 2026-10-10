package com.frontrow.samaajseva.controller;

import com.frontrow.samaajseva.pojo.response.ErrorResponse;
import com.frontrow.samaajseva.pojo.response.Response;

public abstract class AbstractController {
    protected <T> Response<?> getSuccessResponse(T t) {
        return new Response<>(true, t, null);
    }

    protected <T> Response<?> getFailureResponse(T t, ErrorResponse error) {
        return new Response<>(true, t, error);
    }
}
