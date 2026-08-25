<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       03-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 03

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group - synkro-tech
- SPRINT_GOAL: Correct the PDR and ADR-001 to align with the actual course requirements (business-only PDR, single logical database, explicit context map), then populate the docs repository's 00-governance and 02-domain sections with SynkroTech-specific content.
<!-- CONFIG-END -->

## Docs Repository

| Board Name          | URL                                              |
| -------------------- | ------------------------------------------------ |
| synkro-docs Repository | https://github.com/code-corhuila/synkro-docs.git |

## Team Members

| Full Name                          | GitHub User                                                 |
| ----------------------------       | ------------------------------------------                  |
| Sergio Andres Ordoñez Diaz         | https://github.com/SergioAndres17                           |
| Fredman Santiago Plazas Artunduaga | https://github.com/SantiagoPlazas2005                       |
| Jordan Ramirez Gallego             | https://github.com/JordanRG420                              |
| Angel Gustavo Solano Trujillo      |  https://github.com/AsolanoT                                |


## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-PDR-08 | Correct sections 03 - Open questions and 04 - Business glossary in the business PDR | done | https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/blob/a8d2bd29f812bbeb1c8575283411d4a1748c95c4/03-week/hu-status/docs/01_PDR_negocio_v2.md |
| HU-ADR-06 (B) | Correct ADR-001: move from 4 independent databases to a single logical database with one schema per service | done | https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/blob/a8d2bd29f812bbeb1c8575283411d4a1748c95c4/03-week/hu-status/docs/adr-001-architecture_v2.md|
| HU-DOM-03 | Catalog domain events at the Created/Updated/Deactivated level | done | https |
| HU-DOCS-10 | Define the security policy, RBAC, and system roles | done | https://o.md |
| HU-DOCS-11 | Define technical security rules (OWASP Top 10) | done | https://gs |

## 2. My individual contribution

**Context for the correction:** the team's decision was to move from 4 physically independent PostgreSQL databases to a single logical database shared by the 4 microservices, given that a shared infrastructure with strict logical isolation (one schema per service, one dedicated database user per service) better fits the current scale of the project without adding unnecessary operational overhead. We also reviewed the business PDR and found it mixed technical architecture content into what should be a preliminary business discovery document — the PDR should only contain business context, needs, expected processes, open questions and a business glossary.

- I corrected sections **03 — Preguntas abiertas** and **04 — Glosario de negocio** of the business PDR, listing the unresolved business questions (discounts/promotions, payment methods, stock concurrency, multiple branches) with their impact and current status, and rewriting the glossary to contain only business terms (client, product, stock, sale, traceability) instead of the mix of business and technical terms it had before.
- Together with Angel, I updated the **"Alternativas consideradas"** and **"Consecuencias"** sections of ADR-001 to reflect the new single-database decision: I added the rejected alternative of keeping 4 physically separate databases, explaining why it was discarded (unnecessary operational complexity for the project's current scale, and it doesn't satisfy the single logical database requirement).
- I documented the new negative consequence introduced by this change: sharing one physical PostgreSQL instance means a performance issue or outage at the instance level affects all 4 microservices simultaneously, even though their data stays logically isolated by schema.
- I kept the ADR's immutability rule section intact, reinforcing that any future change to this decision must be documented as a new `adr-002-*.md` file rather than editing ADR-001 directly.

**Continuing this week, once the ADR correction closed, I moved on to populating the actual `docs` repository scaffold provided by the instructor:**

- I wrote `domain-events.md` at the level the team agreed on: essential Created/Updated/Deactivated events per entity across all 4 bounded contexts (e.g. `UserRegistered`, `ClientDeactivated`, `ProductUpdated`, `SaleRegistered`), explicitly noting that none of them have real consumers yet since the MVP uses synchronous REST with no active event bus, and leaving a clear extension note for adding finer business-specific events (like `StockDepleted`) if the team activates the optional asynchronous phase later.
- I defined `security-policy.md`: JWT configuration (RS256, 1h access / 7d refresh with rotation), the RBAC model with the project's real roles (ADMIN, SALESPERSON, INVENTORY) and their permission scopes, secret management rules, and the OWASP Top 10 checklist mapped to controls already decided in ADR-001 (like per-schema database users).
- I defined `security-rules.md`, the technical companion to the policy: concrete implementation rules per OWASP category for both stacks (Java/Spring Security, Go/`go-playground/validator`), covering input validation, safe error handling, and dependency scanning.

## 3. Blockers and risks
- Several open questions listed in PDR section 03 (discounts, payment methods, stock concurrency) are still pending validation with the business side; until they're resolved, some data model fields may need to change once implementation starts.
- The single point of failure consequence documented in the ADR (one PostgreSQL instance for all 4 services) has no mitigation plan yet — this should be discussed once we get to defining infrastructure/deployment.
- `domain-events.md`'s events are documented but currently have zero real consumers — there's a risk the team writes them once and never keeps them in sync with actual code once implementation starts, unless we set a reminder to revisit this file per service.

## 4. Plan for next week
- Follow up on the open questions from PDR section 03 with the rest of the team to resolve as many as possible before backend implementation starts.
- Support completing `05-architecture` and `06-data`, since those are the next milestone before starting code implementation.
- Start drafting the base hexagonal folder template documentation for the Sales service (Go), since it's the service most affected by the consequences documented in the ADR this week.

## 5. Compliance self-check
- [ ] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- Corrected business PDR: [`01_PDR_negocio_v1.md`](./docs/01_PDR_negocio_v2.md)
- Corrected ADR-001 (single logical database): [`adr-001-architecture_v2.md`](./docs/adr-001-architecture_v2.md)
- Domain events: [`domain-events.md`](./docs/02-domain/domain-events.md)
- Security policy: [`security-policy.md`](./docs/00-governance/security-policy.md)
- Security rules: [`security-rules.md`](./docs/00-governance/security-rules.md)
     
