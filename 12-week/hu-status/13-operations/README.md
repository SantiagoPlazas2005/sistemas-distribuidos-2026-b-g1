# 13 — Operations

> **What is this?** How the system is operated in production: how problems are detected,
> how incidents are responded to, and how availability commitments are guaranteed.

## Initial administrator procedure

[Initial administrator initialization](initial-administrator-initialization.md) documents the delivered Auth InitialAdminCli after owning migrations: protected invocation, exit codes 0–5, audit verification and cleanup. It is an existing initialization command; administrator recovery remains TD-009. [Observability](observability.md) defines evidence handling.

## Why this section exists

A system that "works" in staging but is impossible to operate in production is useless.
The key operations questions are:
- Do we know when something fails BEFORE users call?
- Do we know exactly what to do when something fails at 3am?
- Do we have formal availability commitments?

---

## Key concepts: SLA, SLO, SLI

**SLI (Service Level Indicator):** the metric you measure — e.g.: percentage of successful requests.

**SLO (Service Level Objective):** the target for that SLI — e.g.: "99.9% of successful requests in 30 days".

**SLA (Service Level Agreement):** the contract with the customer about those targets, with consequences.

No external customer SLA or measured project SLO is established by this snapshot. Availability objectives require an agreed window and real instrumented evidence.

**Error Budget:** how much you can "fail" within the SLO. If the SLO is 99.9%, you have 43.8 minutes/month of available downtime.

---

## What is here and how to fill it in

### `observability.md` ⭐

[Observability](observability.md) distinguishes safe logs/audit and candidate Collector/Prometheus/Grafana connectivity from unverified service instrumentation, populated dashboards, durable collection and alert delivery. TD-006 remains open. There is no adopted ELK/Jaeger/PagerDuty stack.

### `incident-management.md` ⭐

[Incident management](incident-management.md) retains severity/roles/response/post-mortem structure using actual team coordination and GitHub records. It includes ambiguous sale/saga handling, database/readiness limits and safe mitigation. Response windows are internal targets; no staffed external SLA is asserted.

### `backup-and-recovery.md`

[Backup and recovery](backup-and-recovery.md) documents the candidate's protected engine bundles and guarded disposable restoration. Persistent cutover, cross-engine consistency, retention schedule and measured RTO/RPO remain TD-022; initialization is not administrator recovery. Preserve volumes and immutable migrations; prefer reviewed compatible forward fixes.

---

## Correlations with other sections

| This section is fed by... | And feeds into... |
|---------------------------|-------------------|
| `09-microservices/service-catalog.md` and owning repository README | Specific runbooks consolidated here |
| `05-architecture/overview.md` | What to monitor in each service |
| `10-devops/environments.md` | Alert configuration per environment |
| Incidents documented here | `15-project-control/technical-backlog.md` → post-incident improvements |

---

## Questions this section must answer

- How do we know the system is failing before users report it?
- What exactly do we do when service X fails at 3am?
- How much downtime can we have per month?
- How do we recover data if the DB fails?
