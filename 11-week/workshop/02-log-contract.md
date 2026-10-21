# CP2 - Structured log contract

## Relation with the current standard

`05-architecture/cross-cutting.md` §2 already defines a JSON log with `timestamp`, `level`, `service`, `correlationId`, `message` and a nested `context`. This contract extends it for tracing:

- `correlationId` becomes `correlation_id` (snake_case in every field).
- New required fields: `version`, `environment`, `trace_id`, `span_id`, `event`, `duration_ms`.
- The nested `context` is flattened: domain fields are top-level keys.
- `correlation_id` (the `X-Correlation-Id` the project already uses) and `trace_id` (W3C `traceparent`) are different identifiers and both are logged.

## Field table

| Field | Type | Required | Example | Why it exists |
|---|---|---|---|---|
| timestamp | string (ISO-8601 UTC, ms) | required | 2026-10-20T19:41:02.114Z | Order events across services |
| level | string | required | WARN | Filter by severity (DEBUG, INFO, WARN, ERROR) |
| service | string | required | synkro-workflow | Repository name, to know who wrote the line |
| version | string | required | 0.3.0 | Link a failure to a release |
| environment | string | required | develop | Do not mix develop and production lines |
| trace_id | string (32 hex) | required | 7d3c1e8a5b2f49d6a0c4e91f3b6d8a52 | Everything that happened to one request |
| span_id | string (16 hex) | required | b9c1d4e7f2a03856 | The hop that wrote the line |
| correlation_id | string (uuid) | required | c0a8f1d2-5e47-4b19-8d36-2f7a9b6e4c10 | The `X-Correlation-Id` already used by errors and the gateway |
| event | string | required | saga.started | Stable name to search, instead of free text |
| message | string | required | register-sale saga started | Human-readable description |
| duration_ms | integer | required | 41 | Latency of the operation (0 if instantaneous) |

## Domain fields

| Field | Type | Required | Example | Why it exists |
|---|---|---|---|---|
| saga_id | string (uuid) | optional | 5f0e2a41-93b7-4c68-a1d5-8e3b7c9f0d24 | Follow one saga; also the base of `Idempotency-Key` (`<sagaId>:<step>`) |
| saga_step | string | optional | reserve-stock | Which step ran: validate-customer, reserve-stock, register-sale |
| saga_status | string | optional | COMPENSATED | RUNNING, COMPLETED, COMPENSATED or FAILED |
| customer_id | string (uuid) | optional | 2b7d9c10-4e83-47a5-b6f1-9d0a3c5e8f72 | Identify the customer without personal data |
| product_id | string (uuid) | optional | 9e4b6a23-1c58-4d07-8f92-b3a5d7e0c146 | Identify the product in a stock rejection |
| requested_quantity / available_quantity | integer | optional | 3 / 1 | Explain an insufficient-stock rejection |
| http_status | integer | optional | 422 | Result of the call |
| error_code | string | optional | BUSINESS_RULE_VIOLATION | Code from the closed catalog of `07-api/guidelines.md` |
| attempt | integer | optional | 1 | Which retry of a call failed |

## Never log

| Data | Rule |
|---|---|
| password, password hash | Never. Log nothing about it |
| accessToken, refreshToken, SERVICE_TOKEN | Never. Only the `jti` may be logged |
| RS256 private key, database credentials | Never |
| identityDocument (cedula or NIT) | Never. Use `customer_id`; if a document must be shown, only the last 3 digits (`***123`) |
| email | Never in full. Use `customer_id` or `user_id`; if needed, `s***@domain.com` |
| phone, address | Never |
| Idempotency-Key sent by a client | Only its first 8 characters (saga keys `<sagaId>:<step>` are safe) |
| Whole request or response bodies | Never. Log only the domain fields above |

There is no payment-card or health data in this system (money is only an amount in cents).

## Example: one failed sale, one trace_id

```json
{"timestamp":"2026-10-20T19:41:02.114Z","level":"INFO","service":"synkro-workflow","version":"0.3.0","environment":"develop","trace_id":"7d3c1e8a5b2f49d6a0c4e91f3b6d8a52","span_id":"b9c1d4e7f2a03856","correlation_id":"c0a8f1d2-5e47-4b19-8d36-2f7a9b6e4c10","event":"saga.started","message":"register-sale saga started","duration_ms":0,"saga_id":"5f0e2a41-93b7-4c68-a1d5-8e3b7c9f0d24","customer_id":"2b7d9c10-4e83-47a5-b6f1-9d0a3c5e8f72"}
{"timestamp":"2026-10-20T19:41:02.131Z","level":"INFO","service":"synkro-customers-api","version":"1.1.0","environment":"develop","trace_id":"7d3c1e8a5b2f49d6a0c4e91f3b6d8a52","span_id":"e4a7c2d9b1f06385","correlation_id":"c0a8f1d2-5e47-4b19-8d36-2f7a9b6e4c10","event":"customer.read","message":"customer found and active","duration_ms":12,"customer_id":"2b7d9c10-4e83-47a5-b6f1-9d0a3c5e8f72","http_status":200}
{"timestamp":"2026-10-20T19:41:02.155Z","level":"INFO","service":"synkro-workflow","version":"0.3.0","environment":"develop","trace_id":"7d3c1e8a5b2f49d6a0c4e91f3b6d8a52","span_id":"3a6f9d2c8e1b4705","correlation_id":"c0a8f1d2-5e47-4b19-8d36-2f7a9b6e4c10","event":"saga.step.completed","message":"validate-customer completed","duration_ms":41,"saga_id":"5f0e2a41-93b7-4c68-a1d5-8e3b7c9f0d24","saga_step":"validate-customer"}
{"timestamp":"2026-10-20T19:41:07.161Z","level":"WARN","service":"synkro-workflow","version":"0.3.0","environment":"develop","trace_id":"7d3c1e8a5b2f49d6a0c4e91f3b6d8a52","span_id":"7c2e5a8d1f4b9063","correlation_id":"c0a8f1d2-5e47-4b19-8d36-2f7a9b6e4c10","event":"products.call.timeout","message":"synkro-products-api did not answer within 5s, retrying","duration_ms":5001,"saga_id":"5f0e2a41-93b7-4c68-a1d5-8e3b7c9f0d24","saga_step":"reserve-stock","attempt":1}
{"timestamp":"2026-10-20T19:41:07.393Z","level":"WARN","service":"synkro-products-api","version":"1.0.2","environment":"develop","trace_id":"7d3c1e8a5b2f49d6a0c4e91f3b6d8a52","span_id":"d1f8b3a6c9e20574","correlation_id":"c0a8f1d2-5e47-4b19-8d36-2f7a9b6e4c10","event":"stock.reservation.rejected","message":"requested quantity exceeds available stock","duration_ms":23,"product_id":"9e4b6a23-1c58-4d07-8f92-b3a5d7e0c146","requested_quantity":3,"available_quantity":1,"http_status":422,"error_code":"BUSINESS_RULE_VIOLATION"}
{"timestamp":"2026-10-20T19:41:07.399Z","level":"INFO","service":"synkro-workflow","version":"0.3.0","environment":"develop","trace_id":"7d3c1e8a5b2f49d6a0c4e91f3b6d8a52","span_id":"7c2e5a8d1f4b9063","correlation_id":"c0a8f1d2-5e47-4b19-8d36-2f7a9b6e4c10","event":"saga.compensation.skipped","message":"nothing was reserved, no stock to release","duration_ms":4,"saga_id":"5f0e2a41-93b7-4c68-a1d5-8e3b7c9f0d24","saga_step":"reserve-stock"}
{"timestamp":"2026-10-20T19:41:07.402Z","level":"WARN","service":"synkro-workflow","version":"0.3.0","environment":"develop","trace_id":"7d3c1e8a5b2f49d6a0c4e91f3b6d8a52","span_id":"b9c1d4e7f2a03856","correlation_id":"c0a8f1d2-5e47-4b19-8d36-2f7a9b6e4c10","event":"saga.finished","message":"saga ended COMPENSATED at reserve-stock","duration_ms":5288,"saga_id":"5f0e2a41-93b7-4c68-a1d5-8e3b7c9f0d24","saga_step":"reserve-stock","saga_status":"COMPENSATED"}
```

Reading the seven lines filtered by `trace_id`: the customer is valid, the first call to Products times out, the retry reaches Products and is rejected for insufficient stock (a business rule, never retried), nothing was reserved, and the saga ends COMPENSATED.

## Log levels

| Level | When we use it | Example in our project |
|---|---|---|
| ERROR | Unexpected failure or a case that needs a person | A saga ends FAILED because `release-stock` still fails after its bounded retries; a 500 INTERNAL_ERROR in `synkro-products-api` |
| WARN | Recoverable problem or an expected business rejection | A timeout to `synkro-products-api` that is retried; a 422 for insufficient stock; a saga that ends COMPENSATED |
| INFO | Normal facts of the flow | `saga.started`, `saga.step.completed`, `customer.read` |
| DEBUG | Internal state for development only, off in production | Saga state before it is saved to `saga_instance` (never with sensitive data) |
