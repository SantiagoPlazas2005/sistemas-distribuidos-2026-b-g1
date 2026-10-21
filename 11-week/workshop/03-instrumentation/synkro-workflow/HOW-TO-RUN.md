# HOW-TO-RUN - synkro-workflow instrumentation module

Minimal slice of `synkro-workflow` (Java 21, Spring Boot 3.5, Maven). It reads or creates the W3C `traceparent`, writes JSON logs that follow `02-log-contract.md`, calls `GET /customers/{id}` on `synkro-customers-api` with the same `trace_id`, and exposes `workflow_requests_total` and `workflow_request_duration_seconds` on `/actuator/prometheus`.

## Requirements

- Java 21 and Maven 3.9.
- No other service is needed for the test: it starts a fake customers API.

## Install and test

```
cd 11-week/workshop/03-instrumentation/synkro-workflow
mvn test
```

## Run

```
mvn spring-boot:run
```

## Try it (second terminal)

```
curl -i -X POST -H "Content-Type: application/json" -d '{}' http://localhost:8080/sagas/register-sale
curl -i -X POST -H "Content-Type: application/json" -H "traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01" -d '{"customerId":"2b7d9c10-4e83-47a5-b6f1-9d0a3c5e8f72"}' http://localhost:8080/sagas/register-sale
curl -s http://localhost:8080/actuator/prometheus | grep -E "^workflow_"
```

The second request ends in 500 because `synkro-customers-api` is not reachable from this machine. That is expected: it shows a failed downstream call under the same `trace_id`.

## Real console output

Copied from my machine (GitHub Codespaces), trimmed to the relevant lines.
]633;E;echo;8f8179d6-067b-4679-a439-9b2674a22701]633;C
### mvn test

```
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 6.197 s -- in com.synkro.workflow.TracePropagationTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Request without customerId (400)

```
HTTP/1.1 400 
traceparent: 00-e5fc1cb9c1f8b65bbe50c4d32d238677-ec0ff463a50b5ab9-01
X-Correlation-Id: a4c69a9f-1b90-4485-b8bd-34949da2a8a0
{"message":"customerId is required","traceId":"a4c69a9f-1b90-4485-b8bd-34949da2a8a0","error":"VALIDATION_ERROR"}
```
]633;E;echo;f0f49545-6463-445a-95f3-f4d5dca57e13]633;C
### Request with a traceparent (500, customers-api unreachable)

```
HTTP/1.1 500 
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-8bee259d2f040578-01
X-Correlation-Id: 1ae8f0d2-1615-4e5a-b53d-d48ba92b35a6
{"timestamp":"2026-10-21T22:55:31.824+00:00","status":500,"error":"Internal Server Error","path":"/sagas/register-sale"}
```
]633;E;echo;f0f49545-6463-445a-95f3-f4d5dca57e13]633;C
### JSON log lines of the service

```
{"timestamp":"2026-10-21T22:55:23.194Z","level":"INFO","service":"synkro-workflow","version":"0.0.1","environment":"develop","trace_id":"e5fc1cb9c1f8b65bbe50c4d32d238677","span_id":"ec0ff463a50b5ab9","correlation_id":"a4c69a9f-1b90-4485-b8bd-34949da2a8a0","event":"http.request.completed","message":"POST /sagas/register-sale","duration_ms":94,"http_status":400}
{"timestamp":"2026-10-21T22:55:31.765Z","level":"WARN","service":"synkro-workflow","version":"0.0.1","environment":"develop","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"8bee259d2f040578","correlation_id":"1ae8f0d2-1615-4e5a-b53d-d48ba92b35a6","event":"customers.call.timeout","message":"synkro-customers-api did not answer","duration_ms":118,"saga_step":"validate-customer"}
{"timestamp":"2026-10-21T22:55:31.769Z","level":"INFO","service":"synkro-workflow","version":"0.0.1","environment":"develop","trace_id":"4bf92f3577b34da6a3ce929d0e0e4736","span_id":"8bee259d2f040578","correlation_id":"1ae8f0d2-1615-4e5a-b53d-d48ba92b35a6","event":"http.request.completed","message":"POST /sagas/register-sale","duration_ms":123,"http_status":500}
```
]633;E;echo;f0f49545-6463-445a-95f3-f4d5dca57e13]633;C
### Metrics

```
workflow_request_duration_seconds_bucket{method="POST",route="/sagas/register-sale",le="0.025"} 0
workflow_request_duration_seconds_bucket{method="POST",route="/sagas/register-sale",le="0.05"} 0
workflow_request_duration_seconds_bucket{method="POST",route="/sagas/register-sale",le="0.1"} 1
workflow_request_duration_seconds_bucket{method="POST",route="/sagas/register-sale",le="0.25"} 2
workflow_request_duration_seconds_bucket{method="POST",route="/sagas/register-sale",le="0.5"} 2
workflow_request_duration_seconds_bucket{method="POST",route="/sagas/register-sale",le="1.0"} 2
workflow_request_duration_seconds_bucket{method="POST",route="/sagas/register-sale",le="2.5"} 2
workflow_request_duration_seconds_bucket{method="POST",route="/sagas/register-sale",le="5.0"} 2
workflow_request_duration_seconds_bucket{method="POST",route="/sagas/register-sale",le="10.0"} 2
workflow_request_duration_seconds_bucket{method="POST",route="/sagas/register-sale",le="+Inf"} 2
workflow_request_duration_seconds_count{method="POST",route="/sagas/register-sale"} 2
workflow_request_duration_seconds_sum{method="POST",route="/sagas/register-sale"} 0.218777126
workflow_request_duration_seconds_max{method="POST",route="/sagas/register-sale"} 0.123879074
workflow_requests_total{method="POST",outcome="client_error",route="/sagas/register-sale"} 1.0
workflow_requests_total{method="POST",outcome="error",route="/sagas/register-sale"} 1.0
```
