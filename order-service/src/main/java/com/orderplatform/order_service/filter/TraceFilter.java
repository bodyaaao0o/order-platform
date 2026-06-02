package com.orderplatform.order_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class TraceFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("HEADER X-trace-id = {}", request.getHeader("X-trace-id"));

        String traceId =
                request.getHeader("X-trace-id");

        if (traceId == null || traceId.isBlank()) {

            traceId = UUID.randomUUID().toString();
        }

        MDC.put(
                "traceId",
                traceId
        );

        log.info("MDC traceId after put = {}", MDC.get("traceId"));
        log.info("TRACE AFTER MDC");

        response.setHeader(
                "X-trace-id",
                traceId
        );

        try {

            filterChain.doFilter(
                    request,
                    response
            );

        } finally {

            MDC.clear();
        }
    }
}