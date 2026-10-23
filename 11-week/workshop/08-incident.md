# CP9 - Incident drill and blameless postmortem

Journey: register a sale (`POST /api/v1/sagas/register-sale`).

## Scenario

The downstream dependency of the `reserve-stock` hop of the CP1 journey, `POST /stock-reservations` in `synkro-products-api`, answers in 3 seconds for 25 % of the requests during 40 minutes. The other requests answer normally. The call never reaches the 5 s timeout (`cross-cutting.md` §8), so it does not fail: it is slow.

Assumptions of the drill: 20 sales per working hour (`05-slo.md`), so about 13 requests in the 40 minutes and about 3 of them slow; `synkro-products-api` was deployed two minutes before the problem started.

## Trace of one slow request

Spans with durations (one `trace_id`):

- `POST /api/v1/sagas/register-sale` (synkro-api-gateway): 3,230 ms
  - `POST /sagas/register-sale` (synkro-workflow): 3,214 ms
    - `validate-customer`: 41 ms
      - `GET /customers/{id}` (synkro-customers-api): 12 ms
    - `reserve-stock`: 3,012 ms
      - `POST /stock-reservations` (synkro-products-api): 3,004 ms
    - `register-sale`: 118 ms
      - `POST /sales` (synkro-sales-api): 96 ms

The span of `reserve-stock` takes almost all the time of the request.

## Four log lines of that trace (CP2 format)

~~~
{"timestamp":"2026-10-21T15:12:07.102Z","level":"INFO","service":"synkro-workflow","version":"0.3.0","environment":"develop","trace_id":"b7e2a9c4d0f1385e6a2c4b9d1e7f0835","span_id":"4d8a1f6c2e9b7035","correlation_id":"9c1e5b3a-7f24-4d68-a0b9-3e6d1f8c2a47","event":"saga.started","message":"register-sale saga started","duration_ms":0,"saga_id":"e3a5c7b1-2d49-4f06-8b1a-6c0d9e4f7a52","customer_id":"2b7d9c10-4e83-47a5-b6f1-9d0a3c5e8f72"}
{"timestamp":"2026-10-21T15:12:10.154Z","level":"INFO","service":"synkro-products-api","version":"1.0.2","environment":"develop","trace_id":"b7e2a9c4d0f1385e6a2c4b9d1e7f0835","span_id":"a2f9c4e7b0d13568","correlation_id":"9c1e5b3a-7f24-4d68-a0b9-3e6d1f8c2a47","event":"stock.reservation.created","message":"stock reserved","duration_ms":3004,"product_id":"9e4b6a23-1c58-4d07-8f92-b3a5d7e0c146","http_status":201}
{"timestamp":"2026-10-21T15:12:10.155Z","level":"INFO","service":"synkro-workflow","version":"0.3.0","environment":"develop","trace_id":"b7e2a9c4d0f1385e6a2c4b9d1e7f0835","span_id":"4d8a1f6c2e9b7035","correlation_id":"9c1e5b3a-7f24-4d68-a0b9-3e6d1f8c2a47","event":"saga.step.completed","message":"reserve-stock completed","duration_ms":3012,"saga_id":"e3a5c7b1-2d49-4f06-8b1a-6c0d9e4f7a52","saga_step":"reserve-stock"}
{"timestamp":"2026-10-21T15:12:10.316Z","level":"INFO","service":"synkro-workflow","version":"0.3.0","environment":"develop","trace_id":"b7e2a9c4d0f1385e6a2c4b9d1e7f0835","span_id":"4d8a1f6c2e9b7035","correlation_id":"9c1e5b3a-7f24-4d68-a0b9-3e6d1f8c2a47","event":"http.request.completed","message":"POST /sagas/register-sale","duration_ms":3214,"http_status":201}
~~~

Every line is INFO: nothing in the logs says that something is wrong. Only `duration_ms` shows it.

## Values reached by the SLIs

| SLI (`05-slo.md`) | During the 40 minutes | Over 30 days (rest of the month healthy) | Target |
|---|---|---|---|
| Availability (no `error`) | 100 % (no 5xx, no timeout) | 100 % | 99.5 % |
| Latency (0.25 s or less) | about 75 % (25 % of the requests take 3 s) | 99.93 % (3.3 slow requests of 4,800) | 95 % |
| Saga integrity (no `FAILED`) | 100 % | 100 % | 99.9 % |

The latency budget spent is 3.3 requests of 240, about 1.4 %. In business minutes it is 25 % x 40 min = 10 min of a budget of 720 (5 % of 14,400 business minutes), also 1.4 %. The burn rate during the incident is 25 % / 5 % = 5.

## Which alert fires first

None. The two alerts of `06-alerts.yml` look at `outcome="error"`, and a 3 s answer ends in `success`. The error ratio stays at 0 %, so neither the fast nor the slow burn alert fires. The circuit breaker does not open either: it counts failures and timeouts, and these calls are slow but successful (`pattern-guide.md` §1).

A latency alert copied from the same pattern would not fire either: a burn rate of 5 is below the 6 of the slow alert. The incident is silent, and in the drill the first signal is a salesperson who tells the team that sales are slow.

## Which runbook step stops the bleeding

Steps of `07-runbook.md`:
1. The circuit breaker does not act (no failures).
2. Restarting `synkro-products-api` does not help if the cause is in the last deploy.
3. **Rolling back `synkro-products-api` to the previous version stops it**, because the problem started two minutes after its deploy.

## Blameless postmortem

### Timeline (times of the drill)

| Time | Event |
|---|---|
| 14:58 | `synkro-products-api` is deployed |
| 15:00 | 25 % of `POST /stock-reservations` start taking about 3 s |
| 15:12 | The example trace: 3,214 ms for one sale |
| 15:19 | A salesperson tells the team that sales are slow. No alert had fired |
| 15:24 | The first team member opens the runbook and searches the logs by `trace_id` |
| 15:31 | The `reserve-stock` span (3,012 ms) points to `synkro-products-api` (3,004 ms in its own log) |
| 15:34 | The team decides to roll back `synkro-products-api` (mitigation 3) |
| 15:40 | Rollback done, requests are fast again. The incident lasted 40 minutes |

### Impact

- About 13 requests in 40 minutes, about 3 of them slow.
- Availability budget: 0. Saga integrity budget: 0. Latency budget: about 1.4 % (3.3 of 240 requests, 10 of 720 business minutes).
- No sale was lost. The impact is that about 3 customers waited 3 s at the counter, against the P95 under 300 ms of `NFR-001`.

### Root cause

In the drill, the last deploy of `synkro-products-api` made 25 % of the stock reservations wait 3 s. The real technical cause would be found in its logs and in its code.

### Contributing factors

- No alert looks at latency, and the availability alerts only count errors.
- A slow call that succeeds does not trip the retries or the circuit breaker.
- The log lines of a slow call are INFO, so nothing stands out in the logs.
- The metric of time per step (`workflow_step_duration_seconds`) is only "proposed" in `04-metrics.md`.
- The documents do not describe how to roll back a service, and the person who found the problem had to decide it during the incident.

### Action items

1. **Latency alert.** As a team member on duty, I want an alert when many sales are slower than 0.25 s, so that a slow service without errors is detected.
   - Acceptance criteria: `06-alerts.yml` has a rule on `workflow_request_duration_seconds` with `severity`, `team` and `runbook_url`; a `promtool test rules` case where 25 % of the requests take 3 s makes it fire, and a case with 1 % slow requests does not; the threshold is chosen knowing that a burn rate of 5 must be detected.
2. **Slow-call log.** As a developer of `synkro-workflow`, I want a WARN line when a call to a participant takes more than 1 s, so that the logs show which step is slow.
   - Acceptance criteria: a test with a fake participant that answers in 1.5 s produces a line with `event` `products.call.slow`, `saga_step` and the same `trace_id`; a call of 0.2 s produces no such line.
3. **Documented rollback.** As a member of the team, I want a rollback procedure for each service, so that step 3 of the runbook can be done in minutes.
   - Acceptance criteria: the procedure is written in `deployment.md` with the command and the note about database migrations; it was executed once in `develop` on `synkro-products-api`; the measured time is written next to it and is 10 minutes or less.
