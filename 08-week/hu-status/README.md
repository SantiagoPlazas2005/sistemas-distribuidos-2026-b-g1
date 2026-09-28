<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       08-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 08

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group 10 - synkro-tech
- SPRINT_GOAL: Close HU-08 (professor review feedback) and HU-09 (first version of the API contracts), and replace the architecture decisions that no longer held — shared database instance, gateway-only token validation, stateless saga, broker without a real consumer, undecided cross-cutting stack — through ADR-005 to ADR-008, rewriting every dependent document (HU-10).
<!-- CONFIG-END -->

## Docs Repository

| Board Name             | URL                                              |
|------------------------|--------------------------------------------------|
| synkro-docs Repository | https://github.com/code-corhuila/synkro-docs.git |

## Team Members

| Full Name                          | GitHub User                               |
|------------------------------------|-------------------------------------------|
| Sergio Andres Ordoñez Diaz         | https://github.com/SergioAndres17         |
| Fredman Santiago Plazas Artunduaga | https://github.com/SantiagoPlazas2005     |
| Jordan Ramirez Gallego             | https://github.com/JordanRG420            |
| Angel Gustavo Solano Trujillo      |  https://github.com/AsolanoT              |

## 1. User stories worked this week

| HU ID      | Title                                                        | Status | Evidence (PR or commit URL)                                                                       |
|------------|---------------------------------------------------------------|--------|---------------------------------------------------------------------------------------------------|
| HU-DOCS-41 | Pre-ADR-003 JWT and CORS model in `security-policy.md` and `cross-cutting.md` (HU-08, with Sergio) | done   | https://github.com/code-corhuila/synkro-docs/pull/56 |
| HU-DOCS-37 | OpenAPI contract: customers (HU-09)                           | done   | https://github.com/code-corhuila/synkro-docs/pull/53 |
| HU-DOCS-38 | OpenAPI contract: products (HU-09)                            | done   | https://github.com/code-corhuila/synkro-docs/pull/55 |
| HU-DOCS-44 | `deployment.md` part 1: composition, database instances and migrations (HU-10) | done   | `05-architecture/deployment.md` |
| HU-DOCS-45 | `deployment.md` part 2: configuration, development identity and observability (HU-10) | done   | `05-architecture/deployment.md` |

## 2. My individual contribution

**HU-DOCS-41 — pre-ADR-003 security model (HU-08, with Sergio):**
- Aligned `security-policy.md` and `cross-cutting.md` with the gateway model of ADR-003, the accepted decision at the time. HU-10 later superseded it with per-service validation (ADR-006), as recorded in the HU-08 closing comment.

**HU-DOCS-37 and HU-DOCS-38 — customers and products contracts (HU-09):**
- Wrote the first OpenAPI contracts for the customers service (create, read, update, deactivate) and the products service (create product, stock update).
- Flagged explicitly, instead of inventing a fix, the endpoints the contracts needed but ADR-001 §8 did not include: search by identity document and category management. Those gaps became ADR-004.

**HU-DOCS-44 — `deployment.md` part 1 (HU-10):**
- Rewrote the topology:
  - One `platform` network.
  - One PostgreSQL instance per domain plus `workflow-db`.
  - Only the gateway (`:8000`) and the frontend (`:5173`) published.
  - No message broker.
- Documented composition by sibling repositories, each `-db` with its Flyway migration runner (never started by `up`), and the migration commands.
- Stated explicitly that the old shared `synkrotech_db` was never provisioned and nothing is migrated, answering finding 1 of the ADR-005 review.

**HU-DOCS-45 — `deployment.md` part 2 (HU-10):**
- Replaced the "Network Guarantee" section with network isolation: the network limits exposure, but every service still validates the token itself.
- Documented:
  - One `.env` example per environment, and two credential pairs per domain.
  - The variables of every service.
  - The development identity, available only in `develop`.
- Added observability: JSON logs with the correlation ID, and metrics and traces over OpenTelemetry to Prometheus and Grafana.

## 3. Blockers and risks

- **`deployment.md` split into two PRs.** The full rewrite exceeded 400 changed lines, so part 1 (topology, instances, migrations) and part 2 (configuration, identity, observability) were merged separately; between them, part 1 carried a note that the environment section was pending.
- **Credential design corrected afterward.** The first version created the service user through a migration that received its password; HU-DOCS-46 corrected it so passwords only come from environment secrets.
- **The environment depends on every sibling repository.** The platform only starts with all repositories cloned side by side; a missing one fails visibly instead of being skipped.

## 4. Plan for next week

- HU-11:
  - HU-DOCS-56: align the `synkro-customers-api` contract (search by identity document, idempotent creation).
  - HU-DOCS-57: `synkro-products-api` catalog, categories and stock adjustments.
  - HU-DOCS-58: stock reservations and stock alerts.
- HU-12:
  - HU-DOCS-62: amend the DoD and the branch conventions.
  - HU-DOCS-64: fill the risk register and the technical backlog.
  - HU-DOCS-70: rewrite the service catalog.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to `main` — branches `docs/fix-pre-adr003-jwt-and-cors-model`, `docs/add-customers-openapi-contract`, `docs/add-products-openapi-contract`, `docs/rewrite-deployment-composition-and-migrations` and `docs/rewrite-deployment-configuration-and-observability`, merged via PR approved by `ariel5253`
- [x] Testable acceptance criteria
- [x] Tests added/updated — N/A, documentation-only HU
- [x] DDD / hexagonal boundaries respected — N/A, no code touched this week
- [x] No secrets; config via environment variables — `deployment.md` lists only variable names and placeholders; real values never reach a repository

## 6. Evidence links

- Deployment topology (HU-DOCS-44, HU-DOCS-45): [`deployment.md`](./docs/deployment.md)
- Customers contract (HU-DOCS-37): [`customers-service.yaml`](./docs/customers-service.yaml)
- Products contract (HU-DOCS-38): [`products-service.yaml`](./docs/products-service.yaml)
- Security policy (HU-DOCS-41): [`security-policy.md`](./docs/security-policy.md)
