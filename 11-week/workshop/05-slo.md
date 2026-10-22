# CP6 - SLIs, SLOs and error budget

Journey: register a sale (`POST /api/v1/sagas/register-sale`), served by `synkro-workflow`.

## Scope

- `NFR-002` says that no Production environment exists for this project and that no SLO numbers are defined. These SLOs are targets for the journey in the environments that do exist (develop, and staging when it is decided). Every number below is a proposal of this workshop.
- `NFR-001` proposes (not ratified) a P95 latency under 300 ms for `POST /sagas/register-sale`. SLO 2 starts from that value.
- The metrics come from `04-metrics.md`. `workflow_requests_total` and `workflow_request_duration_seconds` exist in the CP3 module; `workflow_saga_finished_total` is still "proposed". No Prometheus is deployed yet (`NFR-005` marks metrics as aspirational), so the queries are the definition, not a running dashboard.
- Window of all the SLOs: 30 days, rolling.

## Traffic estimate

No real traffic exists (`NFR-001`: "no real traffic estimate exists"). `01-context/overview.md` describes SynkroTech SAS as a medium-sized company that sells technology products, with salespeople registering sales at the counter. Assumption:

| Item | Value | Reason |
|---|---|---|
| Salespeople registering sales | 4 | A medium company with a counter team (assumption) |
| Working hours per day | 8 | One shift |
| Sales per salesperson per hour | 5 | One sale every 12 minutes, because each sale includes finding the customer and the products |
| Sales per day | 160 | 4 x 8 x 5 |
| Requests per 30 days | 4,800 | 160 x 30 (a sale is one `POST /sagas/register-sale`) |

If the real number is different, only the budget in requests changes; the percentages and the queries stay the same.

## SLO 1 - Availability

- **SLI:** requests to `POST /sagas/register-sale` that do not end in `error` (5xx or exception) divided by all requests to that route. A 4xx, including a business rejection, is a valid answer (`outcome="client_error"`).
- **Target:** 99.5 % over 30 days.
- **PromQL:**

## SLO 2 - Latency

- **SLI:** requests to `POST /sagas/register-sale` that finish in 0.25 s or less divided by all requests to that route. 0.25 s is the histogram bucket just below the 300 ms of `NFR-001`, so the SLO is slightly stricter than the requirement.
- **Target:** 95 % over 30 days (the P95 of `NFR-001`).
- **PromQL:**

## SLO 3 - Saga integrity

- **Why it fits our product:** a sale that ends `FAILED` means that a compensation did not work after its retries, and a person must decide (ADR-007). It can leave reserved stock without a sale. Availability and latency do not see it.
- **SLI:** sagas that do not end `FAILED` divided by all finished sagas. `COMPENSATED` is not a failure of the system: the saga undid its work correctly.
- **Target:** 99.9 % over 30 days.
- **PromQL** (uses the proposed metric `workflow_saga_finished_total`):

## Error budget

Budget = 100 % minus the target. Requests use the 4,800 of the traffic estimate. Minutes are the budget applied to the 43,200 minutes of 30 days.

| SLO | Target | Budget (%) | Budget in requests (4,800) | Budget in minutes |
|---|---|---|---|---|
| Availability | 99.5 % | 0.5 % | 24 failed requests | 216 min of full outage |
| Latency | 95 % | 5 % | 240 slow requests | 2,160 min with every request slow |
| Saga integrity | 99.9 % | 0.1 % | 4 sagas ending FAILED (4.8 rounded down) | 43.2 min |

The minutes are calendar minutes. Our users only sell about 8 hours a day, so a full outage at night costs no requests. In business time the availability budget is 0.5 % x 30 x 8 x 60 = 72 minutes. The budget in requests is the number that really counts.

## Error budget policy

The "team" is the four members of the project. The owner is the person who owns the affected service.

| Budget consumed | What the team does |
|---|---|
| 50 % | The owner reviews the failed requests in the logs by `trace_id` and writes the likely causes in the team's next meeting. Changes to `synkro-workflow` need a second reviewer. |
| 75 % | Work on the journey stops, except stories that improve reliability (retries, circuit breaker, alerts). The team opens a draft postmortem for the events that consumed the budget. |
| 100 % | Release freeze for `synkro-workflow`, `synkro-customers-api`, `synkro-products-api` and `synkro-sales-api`, except fixes for the SLO. A blameless postmortem is mandatory. Normal work returns when the 30-day window is back under 100 % of the budget. |

## Limits of these numbers

- The traffic and the targets are assumptions, not measurements. The first step after having a real environment is to measure one week and then adjust the targets.
- The 0.25 s threshold must be checked against real data: the CP3 slice answered in 43 ms to 133 ms, but it runs only step 1 of the saga.
- Burn-rate alerts (CP7) will use these budgets: with a target of 99.5 %, a burn rate of 14.4 means an error rate of 7.2 % and a burn rate of 6 means 3 %.
