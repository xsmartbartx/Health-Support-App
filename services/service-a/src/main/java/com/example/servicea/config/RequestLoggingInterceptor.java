package com.example.servicea.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        request.setAttribute("correlationId", correlationId);

        logger.info("Incoming {} request: {} from {}", 
            request.getMethod(), 
            request.getRequestURI(), 
            request.getRemoteAddr());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
            Object handler, Exception ex) throws Exception {
        logger.info("Request completed with status: {}", response.getStatus());
        MDC.remove("correlationId");
    }
}
