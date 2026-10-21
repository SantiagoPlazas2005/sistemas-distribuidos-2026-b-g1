package com.synkro.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import com.synkro.workflow.observability.TraceContext;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TracePropagationTest {

    private static final AtomicReference<String> RECEIVED = new AtomicReference<>();
    private static final HttpServer FAKE_CUSTOMERS_API = startFakeCustomersApi();

    private static HttpServer startFakeCustomersApi() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
            server.createContext("/customers/", exchange -> {
                RECEIVED.set(exchange.getRequestHeaders().getFirst("traceparent"));
                byte[] body = "{\"active\":true}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            server.start();
            return server;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @DynamicPropertySource
    static void customersUrl(DynamicPropertyRegistry registry) {
        registry.add("customers.base-url",
                () -> "http://localhost:" + FAKE_CUSTOMERS_API.getAddress().getPort());
    }

    @AfterAll
    static void stopFakeCustomersApi() {
        FAKE_CUSTOMERS_API.stop(0);
    }

    @Autowired
    private TestRestTemplate rest;

    @Test
    void traceIdReceivedIsTheTraceIdSentDownstream() {
        String traceId = "4bf92f3577b34da6a3ce929d0e0e4736";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("traceparent", "00-" + traceId + "-00f067aa0ba902b7-01");
        HttpEntity<String> request = new HttpEntity<>(
                "{\"customerId\":\"2b7d9c10-4e83-47a5-b6f1-9d0a3c5e8f72\"}", headers);

        ResponseEntity<String> response = rest.postForEntity("/sagas/register-sale", request, String.class);

        TraceContext sent = TraceContext.parse(RECEIVED.get());
        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(sent).isNotNull();
        assertThat(sent.traceId()).isEqualTo(traceId);
        assertThat(sent.spanId()).isNotEqualTo("00f067aa0ba902b7");
    }
}
