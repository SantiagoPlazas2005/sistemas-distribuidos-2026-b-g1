# CP8 - Runbook: RegisterSaleAvailabilityFastBurn

Alert: `RegisterSaleAvailabilityFastBurn` (`06-alerts.yml`), severity `page`, team `synkro-tech`. It fires when more than 7.2 % of the requests to `POST /sagas/register-sale` end in `error` during the last hour and the last 5 minutes. It means that the 30-day error budget (`05-slo.md`) is burning 14.4 times too fast.

## Symptoms

- Salespeople cannot register sales: the portal shows errors when they finish a sale.
- `POST /sagas/register-sale` answers 5xx, or sagas end `COMPENSATED` at the same step again and again.
- In the logs, many `http.request.completed` lines with `http_status` 500 and events such as `customers.call.timeout`.

## How to confirm

1. Error ratio of the last 5 minutes (it must be above 0.072):

~~~
sum(rate(workflow_requests_total{route="/sagas/register-sale",outcome="error"}[5m]))
/
sum(rate(workflow_requests_total{route="/sagas/register-sale"}[5m]))
~~~

2. Which participant fails (the metrics `workflow_downstream_calls_total` and `workflow_saga_finished_total` are still "proposed" in `04-metrics.md`; until they exist, use the logs of step 3):

~~~
sum by (target, outcome) (rate(workflow_downstream_calls_total[5m]))
sum by (status) (increase(workflow_saga_finished_total[15m]))
~~~

3. Follow one failed request across the services. Take its `trace_id` (or the `traceId` of the error response, which is the `X-Correlation-Id`) and search every log:

~~~
docker compose --env-file env/dev.env logs | grep '"trace_id":"<trace_id>"'
docker compose --env-file env/dev.env logs | grep '<X-Correlation-Id>'
~~~

The first form follows the log contract of `02-log-contract.md`; the second works with the current format of `cross-cutting.md` §2, which still uses `correlationId`. The line with `level` WARN or ERROR and the earliest `timestamp` names the service that failed first.

## Mitigations, from lowest to highest risk

Only what our architecture supports is listed.

1. **Let the circuit breaker work and fix the participant.** `synkro-workflow` already has a circuit breaker on each call (`pattern-guide.md` §1): after 3 consecutive failures the circuit opens, calls fail at once instead of waiting 5 s, and it tries again after 15 s. Sales end `COMPENSATED` instead of hanging. Risk: none. Action: find the failing participant with the confirmation steps and fix or restart it.
2. **Restart the failing service**, or `synkro-workflow` itself:
   `docker compose --env-file env/dev.env restart <service>`
   After a restart of `synkro-workflow`, the sagas that were `RUNNING` resume from the state saved in `workflow_schema` (ADR-007). After a restart of a participant, the circuit closes again after the 15 s cooldown. Risk: low to medium, because requests in flight at that moment fail.
3. **Roll back the last change of the failing service**, only if the alert started right after a deploy. Deploy again the previous version of that service with `docker compose`. Risk: higher, because the schema of its database may already have changed (Flyway or Liquibase migrations). The documents do not describe a rollback procedure, so the team must agree on one (see the implementation plan).

Not available today:
- **Feature flag:** none exists in the project, so a risky feature cannot be turned off without a deploy.
- **Scale out:** the MVP runs one instance of each service (`pattern-guide.md` §1), so adding instances is not an option.

## Escalation

The team has four members and no fixed on-call roles (`01-context/scope.md`).

| When | Who |
|---|---|
| The alert fires | The first team member who sees it takes the incident and follows this runbook |
| No improvement after 15 minutes, or the failing service belongs to another member | The Technical Lead, Angel Gustavo Solano Trujillo (@AsolanoT), and the owner of the failing service |
| The budget reaches 100 % (`05-slo.md`) | The Product Owner (the course professor, @ariel5253) is informed and the release freeze of the policy starts |

## Verify recovery

- The error ratio of the confirmation query stays below 0.072 for at least 5 minutes, and the alert resolves by itself.
- Register one test sale from the portal: the saga must reach `COMPLETED` and `GET /api/v1/sagas/{id}` shows its `saleId`.
- Look for sagas that ended `FAILED` during the incident: they may have left stock reserved, and a person must decide what to do with each one (ADR-007).
- Write down the start time, the end time and the cause, because they are the input of the postmortem.
