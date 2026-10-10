package com.frontrow.samaajseva.interceptor;

import com.frontrow.samaajseva.utils.Constants;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;


@Component
public class MDCInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        MDC.put(Constants.HEADER_REQUEST_ID, request.getHeader(Constants.HEADER_REQUEST_ID));
        MDC.put(Constants.HEADER_USER_AGENT, request.getHeader(Constants.HEADER_USER_AGENT));
        MDC.put(Constants.HEADER_USER_ID, request.getHeader(Constants.HEADER_USER_ID));
        MDC.put(Constants.HEADER_DOMAIN, request.getHeader(Constants.HEADER_DOMAIN));
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) {
        MDC.clear();
    }
}