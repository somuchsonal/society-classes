package com.frontrow.samaajseva.filter;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import jakarta.servlet.http.HttpServletRequest;

public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

    @Override
    protected void beforeRequest(@NotNull HttpServletRequest httpServletRequest, @NotNull String message) {
    }

    @Override
    protected void afterRequest(HttpServletRequest httpServletRequest, @NotNull String message) {
        if (!httpServletRequest.getRequestURI().equals("/actuator/health-check") &&
                !httpServletRequest.getRequestURI().equals("/metrics"))
            this.logger.debug(message);
    }
}
