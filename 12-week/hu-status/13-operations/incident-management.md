# Incident Management

> An incident is any unplanned event that interrupts or degrades service.
> Effective incident response is trained before incidents occur, not during.
> Follow this playbook in order — in an incident, stress blocks free thinking.

---

## Severity classification

| Level | Name | Definition | Example | Target response time |
|-------|------|------------|---------|---------------|
| **P0** | Critical | System completely down. Total impact to all users. | API Gateway down, DB inaccessible | **Immediate** (<5 min) |
| **P1** | High | Core functionality degraded. Impact to > 50% of users. | Login or real sale registration unavailable | **15 minutes** |
| **P2** | Medium | Secondary functionality degraded. Workaround available. | Sales report query degraded | **1 hour** |
| **P3** | Low | Minor nuisance. No impact on business operations. | Button with incorrect label | **Next sprint** |

---

## Roles during an incident

| Role | Responsibility | When activated |
|------|----------------|----------------|
| **Incident Commander (IC)** | Coordinates response. Makes decisions. Communicates. | P0 and P1 always |
| **Tech Lead** | In-depth technical diagnosis | P0 and P1 always |
| **On-Call Engineer** | First responder. Executes technical actions | All incidents |
| **Comms Lead** | Communicates to external stakeholders | P0 and P1 |

---

## Response playbook

### Step 1: DETECT (0-5 min)

Detect through a real user/operator report, bounded engine/endpoint checks and available safe owner logs. Automated alert delivery is not established. Record an incident in the existing GitHub issue/project workflow with environment, exact root/lock/owner revisions, UTC time, symptoms and correlation; keep sensitive diagnostics access-restricted. Notify the team through its actual agreed channel. Do not assume PagerDuty or a service-specific runbook file exists.

### Step 2: CLASSIFY (5-10 min)

Determine affected roles/operations, engine reachability, data integrity and whether a safe workaround exists. For P0/P1 assign an Incident Commander and technical owner from the current team; record names in the incident. Response windows are internal targets, not an evidenced staffed on-call SLA. AsolanoT coordinates cross-service/Infra issues; SantiagoPlazas2005 coordinates operations, with the affected store/service owner.

### Step 3: COMMUNICATE (10-15 min)

Initial update in the team channel and to stakeholders:

```
Initial communication template:
─────────────────────────────────
🔴 INCIDENT P[N] — [INC-XXX]
Affected service: [name]
Impact: [What is not working for whom]
Status: Investigating
Next update: in 30 minutes
IC: [Incident Commander Name]
─────────────────────────────────
```

### Step 4: DIAGNOSE (10-30 min)

1. Identify the exact environment and recent deploy/configuration/migration change.
2. Use the selected candidate's `python scripts/synkro.py status <environment>` for engine status, then check actual owner readiness/business behavior. Gateway health alone proves no database; Products health is liveness only (TD-017).
3. Check owner-safe logs/audit and request correlation, recognizing Customers API's missing correlated request/error logging. Grafana/Prometheus connectivity does not prove available service spans/metrics.
4. Compare exact lock/revisions, secrets' source/readability (never contents), migration history/checksums and restricted grants. Do not print Compose/Docker environment expansion.
5. For a pending/uncertain sale preserve original key, caller/input, saga and reservation. Inspect evidence-backed status; malformed/later rejection, logout or journal expiry does not authorize stock release or a replacement sale.

Use the actual owner README and this section's runbooks. No Kubernetes, Jaeger/Kibana/CloudWatch or live incident tool integration is assumed.

### Step 5: MITIGATE (15-60 min)

Keep data and evidence first. Announce the proposed bounded action and obtain the assigned Incident Commander's authorization within the incident process; record operator, target and result. Stop affected writers/executors when consistency requires it. Retry only explicit safe/idempotent operations after understanding their outcome.

Use a reviewed forward fix where possible. Application rollback needs tested compatibility with current schemas/data and consistent participant state. Restoring a disposable target proves the backup test, not persistent cutover. Follow [backup/recovery](backup-and-recovery.md) for actual commands, prerequisites and remaining persistent-recovery gaps; do not remove volumes, repair histories or edit applied migrations.

A job panic can terminate the current Worker process; preserve diagnostics and restart only after classifying the fault. The current job is a placeholder, so restarting cannot restore missing low-stock automation. Lost ADMIN access is TD-009: InitialAdminCli is not recovery. Do not invent a feature flag, cross-region failover, Circuit Breaker or automatic saga resume as mitigation; TD-015 and ADR-015 retain their actual boundary.

### Step 6: RESOLVE AND COMMUNICATE

Verify the affected authorized business operation and durable effects with the actual owner data, not only container health. For sales reconcile saga/reservation/sale evidence before declaring resolution; stop if the outcome is ambiguous. Confirm with the affected user/PO where applicable, preserve safe evidence, and document remaining degraded behavior.

Report incident identity, root cause or remaining uncertainty, bounded correction, exact revision/configuration, verification result, impact duration and follow-up owner. Keep monitoring until stable; missing telemetry limits what can be concluded. Preserve the existing initial communication template and add the actual resolution to the incident record.

---

## Post-Mortem (Incident Retrospective)

The post-mortem is not to blame anyone — it is to understand and prevent.
Held within 2 business days after resolving the incident.

### Post-mortem structure

**INC-XXX — [Incident title]**

**Key data:**
- Start date and time: 
- Resolution date and time: 
- Total duration: 
- Severity: P[N]
- Affected users: [N]

**Timeline:**

| Time | Event | Action taken |
|------|-------|-------------|
| HH:MM | [What happened] | [Who did what] |

**Root cause:**
> Why did it happen? (5 Why's — get to the real cause, not the symptom)

**Contributing factors:**
> Factors that facilitated the incident (without blaming people)

**What went well:**
> [What part of the incident response was effective]

**What did not work:**
> [What part failed or could be improved]

**Action items:**

| Action | Owner | Deadline | Status |
|--------|-------|---------|--------|
| [Concrete action to prevent recurrence] | [Name] | [date] | Pending |

---

## Communication channels

| Channel | Purpose |
|---------|---------|
| Existing team chat/call | Direct coordination; record actual participants and decisions in the incident |
| GitHub issue / Project | Durable incident record, safe evidence references, owner and follow-up |
| Restricted operator storage | Sensitive diagnostics/backups, linked by safe reference only |

No Slack workspace/channel, PagerDuty rotation, Jira/Linear or public status page is established by this project snapshot. Notification/retention automation remains separate work.

---

## Correlations

- [Actual observability](observability.md)
- [Backup and recovery boundary](backup-and-recovery.md)
- [Initial administrator procedure](initial-administrator-initialization.md)
- [Deployment configuration](../05-architecture/deployment.md)
- [Release evidence and owners](../10-devops/releases/2.0.0.md)
