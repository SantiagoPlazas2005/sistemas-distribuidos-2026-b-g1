package com.synkro.workflow.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Reads or creates the trace context of every request and returns it in the response. */
@Component
public class TraceContextFilter extends OncePerRequestFilter {

    private final StructuredLogger logger;

    public TraceContextFilter(StructuredLogger logger) {
        this.logger = logger;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long start = System.nanoTime();
        TraceContext incoming = TraceContext.parse(request.getHeader("traceparent"));
        TraceContext trace = incoming != null ? incoming.child() : TraceContext.create();
        String header = request.getHeader("X-Correlation-Id");
        String correlationId = (header == null || header.isBlank()) ? UUID.randomUUID().toString() : header;

        RequestContext.set(new RequestContext(trace, correlationId));
        response.setHeader("traceparent", trace.toTraceparent());
        response.setHeader("X-Correlation-Id", correlationId);
        try {
            chain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            logger.info("http.request.completed", request.getMethod() + " " + request.getRequestURI(),
                    durationMs, Map.of("http_status", response.getStatus()));
            RequestContext.clear();
        }
    }
}
