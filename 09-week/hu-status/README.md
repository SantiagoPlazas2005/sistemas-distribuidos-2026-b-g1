<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       09-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 09

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group 10 - synkro-tech
- SPRINT_GOAL: Close HU-13 (contract completion and documentation readiness for the code phase), then deliver HU-14 in full — two database engines (ADR-010), the Angular Customers portal inside the React host (ADR-011), instance bootstrap and credentials (ADR-012), and the identity-as-cross-cutting-service proposal (ADR-013) — with every downstream document realigned, and open the code phase with HU-INF-01.
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
| Angel Gustavo Solano Trujillo      | https://github.com/AsolanoT               |

## 1. User stories worked this week

| HU ID      | Title                                                                 | Status | Evidence (PR or commit URL) |
|------------|------------------------------------------------------------------------|--------|------------------------------|
| HU-DOCS-71 | Rename DDL schema prefixes to `<domain>_schema` in `models.md` (HU-13) | done   | `models.md` |
| HU-DOCS-81 | Update deployment for two engines and per-environment files (HU-14)   | done   | https://github.com/code-corhuila/synkro-docs/pull/178 |
| HU-DOCS-88 | Update open questions, risks, dependencies and technical backlog (HU-14) | done | https://github.com/code-corhuila/synkro-docs/pull/180 |

## 2. My individual contribution

**HU-13 — closing the last schema-naming correction (HU-DOCS-71):**
- Renamed every remaining DDL schema prefix in `06-data/models.md` to the final `<domain>_schema` convention. Deferred from HU-DOCS-55 when it was first spotted mid-correction; closed here so HU-13 could close clean.

**HU-14 — deployment for two engines (HU-DOCS-81):**
- Rewrote `05-architecture/deployment.md`: both instances in the diagram and composition, §4 as "Database Instances" with the PostgreSQL/MongoDB tables and the privilege-check query, §5 migrations using administrator credentials with the new Sales Liquibase runner, §6's per-environment files and corrected credentials table, and a new §10 for `develop`-only simulated services. Updated `cross-cutting.md`'s health-check table, and `10-devops/environments.md`/`local-setup.md` for the per-environment-file convention.
- Addressed three automated-review findings: the PR description had listed myself as one of my own approvers (fixed — an author can't approve their own PR); `environments.md`'s intro still said "three database instances" without saying whether that meant three total or three per engine, which read as a leftover from before ADR-010 — now states explicitly "three per engine, six total"; and two citations of ADR-010 in `cross-cutting.md` were missing their decision number next to others that had theirs — both now cite the specific decision.

**HU-14 — closing the backlog's loose ends (HU-DOCS-88):**
- Updated `15-project-control/open-questions.md`, `risks.md`, `dependencies.md` and `technical-backlog.md` with everything HU-14 surfaced: the pending instructor actions (the rename, `synkro-infra-mongo`'s creation, ADR-013's answer), the Angular portal's unverified routing, and the Users/Service-tokens missing-list-endpoint gap. Also corrected two existing risks (R-001, R-005) that still described Sales as sharing the PostgreSQL instance — true before ADR-010, not after.

## 3. Blockers and risks

- **My own review comment on HU-DOCS-81 was a real process mistake**, not just a documentation gap — I'd listed myself as an approver on my own PR. Fixed, and noted for next time: confirm who the actual second reviewer is before writing the PR description, not after.
- **`synkro-infra-mongo` doesn't exist yet**, so the deployment document's MongoDB sections (§4, §9) describe a target that can't be run end-to-end locally until [#159](https://github.com/code-corhuila/synkro-docs/issues/159) resolves.
- **HU-DOCS-88 depends on every other HU-14 story's final state** to be accurate (which ADR is accepted, which risk is real) — had to wait for HU-ARQ-26, HU-DOCS-81's own review cycle, and HU-DOCS-86's screens to settle before writing the gap table, which pushed this story later in the week than planned.

## 4. Plan for next week

- HU-INF-02 (infrastructure skeleton and development identity) in `synkro-infra-postgres`, once the repository's real current name is confirmed.
- Revisit `deployment.md` §4/§9's MongoDB sections for accuracy once `synkro-infra-mongo` actually exists, rather than assuming the design holds unchanged.
- Pair with Jordan on resolving `open-questions.md` Q-006 (the Users/Service-tokens list-endpoint gap) once the Product Owner responds.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to `main` — branches `docs/rename-ddl-schema-prefixes`, `docs/update-deployment-for-two-engines-and-environments`, `docs/close-hu-14-loose-ends`, merged via PR approved by `ariel5253`
- [x] Testable acceptance criteria
- [x] Tests added/updated — N/A, documentation-only HU
- [x] DDD / hexagonal boundaries respected — N/A, no code touched this week
- [x] No secrets; config via environment variables — `deployment.md` §6's credential table names every variable but no value; `risks.md`/`dependencies.md` reference secret rotation without exposing any

## 6. Evidence links

- Deployment for two engines (HU-DOCS-81): [`deployment.md`](./docs/05-architecture/deployment.md)
- Project control updates (HU-DOCS-88): [`risks.md`](./docs/15-project-control/risks.md), [`open-questions.md`](./docs/15-project-control/open-questions.md)
- Issue tracking the pending instructor rename: https://github.com/code-corhuila/synkro-docs/issues/159
