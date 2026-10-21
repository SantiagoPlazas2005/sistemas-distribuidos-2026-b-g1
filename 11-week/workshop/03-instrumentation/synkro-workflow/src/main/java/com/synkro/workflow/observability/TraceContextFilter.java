package com.synkro.workflow.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

/** Reads or creates the trace context of every request, returns it and records the request metrics. */
@Component
public class TraceContextFilter extends OncePerRequestFilter {

    private final StructuredLogger logger;
    private final MeterRegistry registry;

    public TraceContextFilter(StructuredLogger logger, MeterRegistry registry) {
        this.logger = logger;
        this.registry = registry;
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
        boolean failed = false;
        try {
            chain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException e) {
            failed = true;
            throw e;
        } finally {
            long elapsedNanos = System.nanoTime() - start;
            int status = failed ? 500 : response.getStatus();
            record(request, status, elapsedNanos);
            logger.info("http.request.completed", request.getMethod() + " " + request.getRequestURI(),
                    elapsedNanos / 1_000_000, Map.of("http_status", status));
            RequestContext.clear();
        }
    }

    private void record(HttpServletRequest request, int status, long elapsedNanos) {
        Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String route = pattern == null ? "unmatched" : pattern.toString();
        String outcome = status < 400 ? "success" : status < 500 ? "client_error" : "error";
        Counter.builder("workflow.requests")
                .tag("route", route).tag("method", request.getMethod()).tag("outcome", outcome)
                .register(registry).increment();
        Timer.builder("workflow.request.duration")
                .tag("route", route).tag("method", request.getMethod())
                .serviceLevelObjectives(Duration.ofMillis(25), Duration.ofMillis(50),
                        Duration.ofMillis(100), Duration.ofMillis(250), Duration.ofMillis(500),
                        Duration.ofSeconds(1), Duration.ofMillis(2500), Duration.ofSeconds(5),
                        Duration.ofSeconds(10))
                .register(registry).record(elapsedNanos, TimeUnit.NANOSECONDS);
    }
}
