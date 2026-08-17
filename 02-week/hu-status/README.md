# Weekly status - Week 02

- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group - PRJ-GESTION-VENTAS-V1
- SPRINT_GOAL: Document the Consequences section of the architecture ADR for the sales management system, capturing the trade-offs of the service split and communication strategy.
<!-- CONFIG-END -->

## Team Members
| Full Name                          | GitHub User                           |
| ----------------------------------- | -------------------------------------- |
| Sergio Andres Ordoñez Diaz         | https://github.com/SergioAndres17      |
| Fredman Santiago Plazas Artunduaga | https://github.com/SantiagoPlazas2005  |
| Jordan Ramirez Gallego             | https://github.com/JordanRG420         |
| Angel Gustavo Solano Trujillo      | https://github.com/AsolanoT            |

## 1. User stories worked on this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-pdr-04 | Risks, work plan, acceptance criteria and glossary | done | [[commit or PR URL]](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/blob/main/01-week/hu-status/pdr.md) |
| HU-ADR-04 | Document Consequences and Immutability of the ADR | done | (https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/blob/4be18bd7190f1b2bcfd50c2e0436755d84aa8a0d/02-week/hu-status/adr-001-architecture.md) |

## 2. My individual contribution
- I documented the positive consequences of the chosen architecture: a clear separation of responsibilities by bounded context, a balanced split of 2 services in Java and 2 in Go (meeting the course requirement), and a centralized Auth service that simplifies role and security management.
- I documented the negative consequences: the Sales service concentrates more responsibility than ideal, since it orchestrates Customers, Products, and report generation; synchronous communication between services introduces temporal coupling and points of failure (if Products goes down, Sales cannot create sales); and there are no ACID transactions between services, so eventual consistency must be handled explicitly.
- I wrote this as the Consequences section of the ADR, following the same format as the Context, Decision, and Alternatives sections already defined by the team.

## 3. Blockers and risks
- The current design makes Sales a de facto single point of failure for checkout, since it depends synchronously on both Customers and Products; without a mitigation (timeouts, circuit breaker, or moving parts of this to async later), a partial outage in either dependency cascades into Sales.
- Eventual consistency between services is named as a consequence but not yet designed: there is no defined strategy for reconciling cases like "stock reserved but the sale ultimately failed."

## 4. Plan for next week
- Propose concrete mitigations for the negative consequences (timeouts and a circuit breaker on the Sales → Products and Sales → Customers calls).
- Coordinate with whoever defined the Decision and Alternatives sections to make sure the full ADR is internally consistent before it is marked as Accepted.
- Start slicing the accepted ADR into backlog stories with testable acceptance criteria.

## 5. Compliance self-check
- [ ] Conventional Commits - `type(scope): summary`
- [ ] HU branch by environment + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Verifiable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (the domain has no I/O)
- [x] No secrets; configuration via environment variables

## 6. Evidence links
- Contribution to ADR Consequences: [`adr-001-architecture.md`](./docs/adr/adr-001-architecture.md)
