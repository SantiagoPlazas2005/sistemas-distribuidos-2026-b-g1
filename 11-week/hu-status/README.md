<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       11-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 11

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group - synkro-tech
- SPRINT_GOAL: Make the register-a-sale journey of SynkroTech observable end to end: trace map, structured log contract, instrumentation code with a test in synkro-workflow, and a metric catalog (workshop delivery 1, checkpoints CP0 to CP4).
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| W11-CP1 | Critical journey and trace map (register a sale) | done | [505ef22](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/505ef22) |
| W11-CP2 | Structured log contract | done | [753acb9](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/753acb9) |
| W11-CP3 | Instrumentation code and test in synkro-workflow | done | [bf3c354](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/bf3c354), [8bacfff](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/8bacfff) |
| W11-CP4 | Golden signals and metric catalog | done | [b1bbf38](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/b1bbf38) |

## 2. My individual contribution
- **CP0 and CP1:** created `11-week/workshop/` with the CONFIG block and mapped the journey `POST /api/v1/sagas/register-sale` across synkro-api-gateway, synkro-workflow, synkro-customers-api, synkro-products-api and synkro-sales-api, with the hop table, a Mermaid sequence diagram and the span tree (`01-journey.md`).
- **CP2:** wrote the log contract (`02-log-contract.md`): the 11 required fields, the domain fields (`saga_id`, `saga_step`, `customer_id`, `product_id`), the never-log list, seven sample lines with one `trace_id` and the log levels.
- **CP3:** built a minimal Java 21 / Spring Boot 3.5 module in `03-instrumentation/synkro-workflow`. A filter reads or creates the W3C `traceparent`, a JSON logger follows the CP2 contract, and `POST /sagas/register-sale` runs step 1 (`validate-customer`) and calls `GET /customers/{id}` with the same `trace_id` and a new `span_id`. It exposes `workflow_requests_total` and `workflow_request_duration_seconds`. `TracePropagationTest` passes (1 test, 0 failures); the console output is in `HOW-TO-RUN.md`.
- **CP4:** wrote the metric catalog (`04-metrics.md`) with the four golden signals for each service of the journey, label cardinality counted from the real OpenAPI contracts, forbidden labels and histogram buckets.

## 3. Blockers and risks
- The CP3 module is a minimal slice: only step 1 of the saga, no retries (up to 3 attempts, `cross-cutting.md` §8) and no circuit breaker.
- When `synkro-customers-api` is unreachable the module answers Spring's default 500 body, not the project's error envelope (`07-api/guidelines.md`).
- The event `customers.call.timeout` is also logged when the host name does not resolve, so it does not tell "no answer" from "does not exist".
- ADR-007 does not say whether the correlation id is stored in `saga_instance`, so a saga resumed after a restart could lose its trace.
- The documents do not give the request timeout of `POST /sagas/register-sale`; the top histogram bucket (10 s) depends on it.
- Three commits made from the GitHub web editor kept the default message (`Create ...`) and do not follow Conventional Commits.

## 4. Plan for next week
- Delivery 2 (CP6 to CP10): SLOs and error budget for the journey, burn-rate alerts, a runbook for the fast alert, an incident drill with a blameless postmortem, and the implementation plan.
- Decide with the team the request timeout of the saga and whether `trace_id` is saved with the saga state.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` (except the three web-editor commits noted in section 3)
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) - not applicable: workshop work was pushed to `main` of my fork
- [ ] Testable acceptance criteria - not applicable: the workshop defines checkpoints, not user stories
- [x] Tests added/updated (unit / integration) - `TracePropagationTest`
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) - not evaluated: the module is an instrumentation slice without domain code
- [x] No secrets; config via environment variables - the downstream URL is the property `customers.base-url`

## 6. Evidence links
- Workshop folder: https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/tree/main/11-week/workshop
- Instrumentation module: https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/tree/main/11-week/workshop/03-instrumentation/synkro-workflow
- Metric catalog: https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/blob/main/11-week/workshop/04-metrics.md
