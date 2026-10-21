package com.synkro.workflow;

import com.synkro.workflow.observability.RequestContext;
import com.synkro.workflow.observability.StructuredLogger;
import com.synkro.workflow.observability.TraceContext;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/** Step 1 of the saga (validate-customer): GET /customers/{id} on synkro-customers-api. */
@Component
public class CustomersClient {

    private static final int TIMEOUT_MS = 5000;

    private final RestClient http;
    private final StructuredLogger logger;

    public CustomersClient(StructuredLogger logger, @Value("${customers.base-url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT_MS);
        factory.setReadTimeout(TIMEOUT_MS);
        this.http = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
        this.logger = logger;
    }

    /** Returns true when the customer exists and is active, false when customers-api answers 404. */
    public boolean isActive(UUID customerId) {
        RequestContext ctx = RequestContext.get();
        TraceContext outgoing = ctx.trace().child();
        long start = System.nanoTime();
        try {
            http.get().uri("/customers/{id}", customerId)
                    .header("traceparent", outgoing.toTraceparent())
                    .header("X-Correlation-Id", ctx.correlationId())
                    .retrieve()
                    .toBodilessEntity();
            logCall("customers.call.completed", "GET /customers/{id}", start, 200);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            logCall("customers.call.completed", "GET /customers/{id}", start, 404);
            return false;
        } catch (ResourceAccessException e) {
            logger.warn("customers.call.timeout", "synkro-customers-api did not answer",
                    elapsedMs(start), Map.of("saga_step", "validate-customer"));
            throw e;
        }
    }

    private void logCall(String event, String message, long start, int status) {
        logger.info(event, message, elapsedMs(start),
                Map.of("saga_step", "validate-customer", "http_status", status));
    }

    private static long elapsedMs(long start) {
        return (System.nanoTime() - start) / 1_000_000;
    }
}
