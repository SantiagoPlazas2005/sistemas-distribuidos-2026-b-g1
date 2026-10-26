# System Observability

> An unobservable system cannot be operated. Observability is not an optional feature
> added later — it is designed from the first sprint.
> The 3 pillars: Logs, Metrics, Traces.

> **Delivery note — Week 12:** the actual services are Java/Go with a React Host
> and React/Angular portals. The candidate configures Collector, Prometheus and
> Grafana; their healthy startup is separate from complete service instrumentation,
> centralized logging, alerts and measured SLOs. Pending work remains TD-006.

---

## The 3 pillars of observability

```text
Owner services ── safe stdout/stderr ── operator-controlled collection
      │                                 (durable central store not established)
      └─ target OTLP exporters ── Collector ── Prometheus ── Grafana
           (service coverage pending)       (candidate tooling verified)
```

The candidate uses Collector `0.123.0`, Prometheus `3.2.1` and Grafana `11.6.0`. OTLP and Collector health/metrics remain internal; only loopback 9090/3000 are published. No Elasticsearch, Kibana, Jaeger/Zipkin, Alertmanager, PagerDuty or Slack notification integration is established. Author A6 reports configuration/startup/connectivity at its own root; see the [release evidence](../10-devops/releases/2.0.0.md#author-execution-evidence).

---

## Logs

### Standard format (structured JSON)

The logging target is structured, safe event data with a correlation identifier. Verify actual owner output before relying on a field for diagnosis; not every service currently satisfies the target. Example shape (illustrative, not a captured event):

```json
{"timestamp":"2026-10-25T06:00:00Z","level":"INFO","service":"synkro-workflow","correlationId":"execution-id","message":"operation observed","outcome":"pending"}
```

**Required fields:**

| Field | Description |
|-------|-------------|
| `timestamp` | UTC event time |
| `level` | Severity |
| `service` | Actual owner service |
| `correlationId` | Request/job/controlled execution identity; not automatically a W3C span ID |
| `message` | Safe action/result, no credentials, bodies or personal details |

HTTP response `X-Correlation-Id` and error `traceId` are available where owner contracts implement them. Customers API's release explicitly records missing structured request/error correlation logs (owner issue #15); a correlated response does not fix that omission. Worker emits slog JSON for scheduler/placeholder execution, which proves no stock-alert effect. Store actual event field names with each evidence record instead of silently normalizing missing fields into a claimed pass.

### Log levels

| Level | When to use it | Example |
|-------|---------------|---------|
| `DEBUG` | Development info. Disabled in production | "SQL query executed: SELECT..." |
| `INFO` | Normal business events | "Order #123 created" |
| `WARN` | Abnormal but not failure | "Token about to expire", "Retry #2" |
| `ERROR` | Error that requires attention | "Failed to connect to PostgreSQL" |
| `FATAL` | Error that brings down the service | "Cannot start: port in use" |

### What NOT to log

Never record passwords/hashes, bearer/refresh/service tokens, authenticated connection URIs, private PEMs, complete request bodies, customer documents or raw SQL/driver exceptions. Avoid raw Compose/Docker environment output. Use operation/result/reason, duration/counts, safe IDs and correlation only; even identifying metadata is access-controlled and minimized.

### Implementation

Use each owner's actual logger/adapters, never add Node.js production snippets to describe Java/Go behavior. Restricted execution reports contain root/owner SHAs, run ID, safe command/scope, timestamps, exit/result and correlation. A source inspection or sanitized report is not a new executed acceptance test. No durable central log store/retention automation is established by the current root; verifying protected collection is an operational gate.

---

## Auth creation evidence and security logging (ADR-014)

Successful creations are queried from auth_schema.user_creation_audit, not inferred
from ordinary logs. Use the audit_id, created_user_id and correlation_id to connect
evidence to a request or controlled initialization execution. Field semantics and
permissions remain in `06-data/models.md` and `06-data/data-dictionary.md`.

Authorized operators inspect evidence through controlled Auth-scoped read access;
there is no new public audit endpoint and no cross-domain database access. Retain
successful records for the lifetime of the environment, alongside user history;
include them in database backups. No application purge or mutation is permitted.

Gateway, authentication adapter and use case emit structured rejection/failure
logs at the point of detection. Capture the available trusted identity or ANONYMOUS,
action, result, safe reason code, UTC timestamp and correlation identifier. Operator
no-op runs identify their deployment execution; they do not create success records.
Never include passwords, hashes, tokens, request bodies or a nonexistent target ID.

Collect security logs in the environment's durable logging destination. Retain them
for the lifetime of the academic environment; verify collection and restricted
access before real initialization. The candidate has an OTLP Collector, but durable centralized security-log collection is not established by its connectivity test. Follow the security policy/rules for retention (at least 90 days and the environment-lifetime requirement where longer); storage, access and collection must be verified independently. Failure or loss of required collection is a security incident.

Monitor audit-write failures, missing collection and attempted UPDATE/DELETE/TRUNCATE
of audit evidence. Audit-write failures must correlate with rolled-back creations,
never successful 201 responses. Metrics use outcome/reason labels, not user IDs.
Deletion/retention changes require a separately reviewed operational policy.

## Metrics

### RED Metrics (Rate, Errors, Duration)

Required targets remain rate, errors and duration for real endpoints/business operations:

| Metric | Type | Description |
|--------|------|-------------|
| `http_requests_total` | Counter target | Method, bounded route, status |
| `http_request_duration_seconds` | Histogram target | Latency, with bounded labels |
| `http_requests_in_flight` | Gauge target | Current HTTP work |

These names describe the designed series, not a verified export from every owner. The candidate's Prometheus scrape configuration and Collector self-health prove tools/connectivity only. Verify a real request changes the expected series and failure/latency signals before claiming implementation. Do not label metrics with user IDs, sale IDs, correlation IDs or unbounded URLs.

### Business metrics (USE method for resources)

| Business metric | Type | Description |
|----------------|------|-------------|
| Sale/saga outcome counts | Counter target | Completed, compensated, suspended/failed outcomes without personal labels |
| Worker run/failure duration | Counter/histogram target | Real low-stock work once implemented; placeholder completion is not the business signal |
| Engine/pool saturation | Gauge/histogram target | Connections, wait/query duration, capacity and database readiness |

No complete business/database export or alert behavior is demonstrated by the inspected candidate. TD-006 stays open with owner SantiagoPlazas2005 and the instrumented evidence required before operational reliance.

### Metrics endpoint

Do not assume every API implements `GET /metrics`. Candidate Collector exposes internal ports 8889/13133 and OTLP 4317/4318; engine/service exporters still require verified owner configuration. Keep monitoring endpoints off public domain routing. [Deployment](../05-architecture/deployment.md#8-observability) and the exact candidate files define actual ports, not the former generic example.

---

## Distributed traces

Tracing allows following a request across multiple services.

### Context propagation

The actual public contract uses `X-Correlation-Id` and common error `traceId`. Follow those values across the Gateway/Workflow/participants where owner logs support them. Matching correlation strings are not proof of W3C `traceparent` propagation, parent/child spans or automatic database instrumentation. Those need recorded real spans/exports, which the current evidence does not establish.

### Implementation with OpenTelemetry

Collector configuration is in the root candidate's `observability/otel-collector.yaml`; running it does not install agents/SDKs in services. Each owner must explicitly instrument inbound/outbound I/O and export using the configured internal endpoint, then prove receipt with safe sample requests and service identity. No synthetic NodeSDK example is treated as delivered code.

### Create manual spans for business logic

Add spans through the appropriate owner stack only when their need/attributes are reviewed. Do not attach tokens, bodies, customer details or passwords. Verify saga step/outcome/attempt boundaries without changing authorization or idempotency. The instrumentation/export tests and demonstrated trace remain required closing evidence for TD-006.

---

## Grafana Dashboards

### Main dashboard — Executive view

Required panels remain error rate, p95 latency, throughput and availability by service. Before relying on them, supply the real exported series, dashboard definition and a demonstrated healthy/failing request. Provisioning a Grafana datasource is not a populated dashboard or a measured SLO. No customer SLA or observed availability percentage is declared.

### Per-service dashboard

Each implemented dashboard should show RED metrics, Java/Go runtime and owning-store/pool behavior with bounded labels. Workflow requires pending/suspended work visibility; Worker requires real job outcomes once delivered. There is no broker/DLQ or Kubernetes pod monitoring in the MVP. Dashboard definitions and real populated evidence remain pending.

---

## Alerts

### Standard alert rules

| Alert | Condition | Severity | Notifies |
|-------|-----------|----------|---------|
| Service/engine unavailable | Bounded readiness fails beyond the agreed window | P1 target | Assigned operator; automated notification not established |
| Errors/latency above agreed objective | Requires real series and ratified threshold | P1/P2 target | Assigned operator; delivery pending TD-006 |
| Pool/disk saturation | Verified owning-engine capacity threshold | P2 target | Assigned operator; exporter/threshold evidence pending |
| Missing audit/log collection | Required evidence unavailable or audit insertion fails | P1 target | Auth/operations owner through actual team channel |

These are response targets, not installed/firing rules. No PagerDuty/Slack webhook, alert delivery, escalation automation or measured notification time is claimed. Store reviewed rules and evidence of a safe triggered/recovered test before declaring that mitigation complete.

---

## Correlations

- [Deployment and telemetry boundary](../05-architecture/deployment.md#8-observability)
- [Incident response](incident-management.md)
- [Administrator initialization and audit](initial-administrator-initialization.md)
- [Backup and evidence preservation](backup-and-recovery.md)
- [Operational monitoring backlog](../15-project-control/technical-backlog.md#td-006--operational-monitoring-for-the-shared-instance-and-the-mongodb-instance)
- [Bounded release evidence](../10-devops/releases/2.0.0.md)
