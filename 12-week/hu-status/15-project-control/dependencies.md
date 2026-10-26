# External Dependencies

> Things the project needs that it does not control itself: decisions
> owed by someone outside the development team, infrastructure the team
> has not provisioned yet, and third-party software the system's own
> images are built from. SynkroTech has no external business integration
> (`01-context/scope.md`, "External Integrations": none) — everything
> here is course logistics or base infrastructure, not a payment gateway
> or a shipping provider.

---

## Active dependencies

| Dependency | Type | Required for | External owner | Expected date | Status |
|---|---|---|---|---|---|
| Repository provisioning/names | Course logistics | All 18 code repositories plus synkro-docs | Organization/instructor | Rechecked 2026-10-25 | Done — both Infra repositories exist under final names; provisioning no longer blocks Sales |
| External course confirmation of Accepted ADR-013 Option A | External assessment | Course classification only, not internal adoption | Instructor; AsolanoT retrieves evidence | Sprint 12 review; before claiming external confirmation | Q-005 partially answered; no instructor response invented |
| Persistent qa provision/operator/schedule | Decision/operations | Actual staged runtime, distinct from permanent branch/examples | PO/instructor; AsolanoT coordinates | Sprint 12 review; before promising a persistent qa run | Q-004 open; qa examples/branches/workflows exist, Infra qa remains scaffold |
| Actions execution and approved private sibling reads | Platform/access | Required exact-head CI/private integration | Organization administrator; AsolanoT coordinates | Sprint 12 review; before root promotion | Several owner main runs fail; Infra startup_failure has no confirmed cause here; private workflow access/reproduction pending |
| Owner release agreement | Cross-repository delivery | One compatible root lock | Mongo/Sales, Products Portal and Worker owners; SantiagoPlazas2005/AsolanoT coordinate | Before new lock validation/promotion | Sales DB 9cfbde7/344d917 pin conflict, Products Portal tag exclusion and absent Worker tag remain open |
| Independent reproduction/review | Team dependency | Acceptance based on actual reviewer execution | Different teammate, requested through normal review; AsolanoT coordinates | Before HU-INF-02 closure/promotion | Reviewer template exists, no completed independent run is recorded |
| PostgreSQL image `16.15-alpine3.23` | Third-party software | Candidate engine | PostgreSQL/Docker distribution; owner pins | Available in inspected candidate | Version pin is verified in source, not an image availability/security guarantee for a future run |
| Mongo image / keyfile/authenticated rs0 | Third-party software/configuration | Sales engine | Mongo owner | Delivered v2.0.0 | Single-node engine delivered; no failover guarantee |
| Flyway 11.20.3-alpine / Customers 10.20.1 | Third-party software | Owning schema runners | Respective DB owners | Delivered per owner revision | Distinct runner versions/commands; no uniform undo/file-secret API |
| Liquibase 4.31.1 + Mongo extension 4.29.1 | Third-party software | Sales migrations | Sales DB owner | Delivered v2.0.0 | Protected runner environment; repository-root mount limitation remains owner #17 |
| Gateway NGINX / frontend package runtimes | Third-party software | Owner builds | Respective owner lock/Dockerfile | Per selected revision | Validate pinned dependencies and scans on release; no floating upgrade implied |
| Collector 0.123.0, Prometheus 3.2.1, Grafana 11.6.0 | Third-party software | Candidate monitoring tools | Infra/operations owner | Configured; TD-006 before operational reliance | Tool connectivity reported; service/engine instrumentation, alerts and protected durable collection not established |

These statuses use the [release record](../10-devops/releases/2.0.0.md), exact owner sources and current issue/PR state. Source access is not successful private integration. No local-evidence alternative replaces required checks/review, and no excluded proposal supplies an adoption route.

---

## Explicitly not a dependency

| What | Why it is listed here instead of above |
|---|---|
| A payment gateway | Out of scope for the MVP (`scope.md`, "Candidates for Future Versions" #2) — no integration exists or is planned for this course delivery |
| Electronic invoicing (tax authority integration) | Explicitly out of scope (`scope.md`, "Explicitly Out of Scope" #2) |
| A message broker (RabbitMQ) | Deferred, not pending — ADR-007 Decision 5 is a decision already made, not a dependency waiting on someone else |
| Redis or any cache provider | Not adopted anywhere in the project (`05-architecture/overview.md` §8) |
| A JWKS provider or external identity provider | `synkro-auth-api` issues and validates its own RS256 keys; nothing external is involved (ADR-001 §1, ADR-006) |
| Native Federation, or any Angular-specific module-federation tooling | ADR-011 mounts the one Angular portal as a custom element, not through module federation of any kind — there is nothing to depend on here |

---

## Correlations

- Repository ecosystem this depends on provisioning → `09-microservices/service-catalog.md`
- The `qa` environment question → `15-project-control/open-questions.md` Q-004
- ADR-013 external course-confirmation question → `15-project-control/open-questions.md` Q-005
- Monitoring dependency and its technical-debt entry → `15-project-control/technical-backlog.md` TD-006
- Risks this connects to (shared instance and Sales' instance as single points of failure) → `15-project-control/risks.md` R-001, R-007
- Deployment topology and exact image tags → `05-architecture/deployment.md`
- Sales domain on MongoDB, infra repository naming → `05-architecture/decisions/records/ADR-010-sales-on-mongodb.md`
