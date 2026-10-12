<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       10-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 10

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group 10 - synkro-tech
- SPRINT_GOAL: Stand up the Auth and worker base structures, then take `synkro-auth-api` from a placeholder filter to real RS256 validation, PostgreSQL-backed persistence, registration, logout with reuse detection, and service tokens — and update the traceability matrix for the newly real Products portal.
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
| HU-AUTH-01 | Base structure of `synkro-auth-api` (3 Maven modules + `/health` + default filter) | done | https://github.com/code-corhuila/synkro-docs/issues/184 |
| HU-WRK-01  | Worker scheduler loop, no HTTP endpoint, graceful shutdown | done | https://github.com/code-corhuila/synkro-docs/issues/189 |
| HU-DOCS-89 | Update the traceability matrix with HU-PRO-11 and HU-PRO-12; link FR-002, FR-003, FR-004 to their tests; clarify that Products now uses real services while Sales stays blocked | done | branch: `docs/add-products-portal-stories` |
| HU-AUTH-07 | Replace `MinimalBearerCheckFilter` with real RS256 validation (RS256 only, `exp`, `sub`, 30s tolerance, ignores `X-User-*`), 401/403 with the common envelope, remove `PlaceholderController` | done | https://github.com/code-corhuila/synkro-docs/issues/211 |
| HU-AUTH-08 | JDBC adapters (`JdbcTemplate`) over `system_user` and `refresh_token`, connected as `auth_app`; seeded users removed; integration tests against `TEST_DATABASE_URL` and CI with the real `synkro-auth-db` schema | done | https://github.com/code-corhuila/synkro-docs/issues/212 |
| HU-AUTH-09 | `POST /auth/register` with `Idempotency-Key` (201+Location, 200 on repeat, 422 on duplicate email, `SERVICE` role rejected, key+user in one transaction) and `GET /auth/users/{id}` for ADMIN only, hash never exposed | done | https://github.com/code-corhuila/synkro-docs/issues/214 |
| HU-AUTH-10 | Idempotent `POST /auth/logout` (rejects a service token), reuse detection that invalidates every active token for the user, 7-day expiry enforced | done | https://github.com/code-corhuila/synkro-docs/issues/216 |
| HU-AUTH-11 | `POST /auth/service-tokens`, ADMIN only, fixed ADR-006 permission table, `sub` equals the service, `roles: ["SERVICE"]`, 30-day expiry, `jti` equals `token_id`, shown once (repeat with the same key returns 200 without the token); `GET /auth/service-tokens/{id}` never returns the token | done | https://github.com/code-corhuila/synkro-docs/issues/217 |

> HU-DOCS-89's branch is recorded; verify the merged PR URL before upload.

## 2. My individual contribution

**Base structures (HU-AUTH-01, HU-WRK-01):**
- HU-AUTH-01: stood up `synkro-auth-api`'s 3-module Maven skeleton, `/health` and the default (placeholder) filter — the starting point every later HU-AUTH-0X HU replaced piece by piece.
- HU-WRK-01: built the worker's scheduler loop with no HTTP surface at all and a graceful shutdown path, matching the worker's role as a background-jobs service with no inbound traffic.

**Traceability update (HU-DOCS-89):**
- Brought the traceability matrix current with HU-PRO-11 and HU-PRO-12, linked FR-002/FR-003/FR-004 to their actual tests, and recorded the real state of the system accurately: Products now calls real services end to end, while Sales is still blocked on its own API chain.

**`synkro-auth-api` hardening, end to end (HU-AUTH-07 through HU-AUTH-11):**
- HU-AUTH-07: replaced the placeholder bearer-check filter with real RS256 validation — RS256 only, checks `exp` and `sub`, a 30-second clock-skew tolerance, and it now ignores any `X-User-*` header a caller might try to forge. Removed `PlaceholderController` along with it, since it depended on the filter being fake.
- HU-AUTH-08: swapped the seeded in-memory users for real JDBC adapters over `system_user` and `refresh_token`, connected through the `auth_app` role, with integration tests running against `TEST_DATABASE_URL` and CI validated against `synkro-auth-db`'s real schema rather than a mock.
- HU-AUTH-09: implemented `POST /auth/register` with idempotency-key support (201 with `Location` on first call, 200 on repeat, 422 on a duplicate email, `SERVICE` role rejected outright) and `GET /auth/users/{id}` restricted to ADMIN, with the password hash never serialized in any response.
- HU-AUTH-10: made `POST /auth/logout` idempotent, rejected outright for a service token, added refresh-token reuse detection that invalidates every active token for that user the moment reuse is spotted, and enforced the 7-day expiry window.
- HU-AUTH-11: implemented `POST /auth/service-tokens` for ADMIN only, against the fixed per-service permission table from ADR-006 — `sub` set to the service name, `roles: ["SERVICE"]`, 30-day expiry, `jti` equal to the stored `token_id`, and the raw token shown exactly once (a repeat call with the same idempotency key returns 200 with the metadata but not the token again); `GET /auth/service-tokens/{id}` never returns the token value.

## 3. Blockers and risks

- **HU-AUTH-07 through HU-AUTH-11 depend on Angel's HU-AUTH-02/03** (schema skeleton and real token issuance) and Sergio's HU-AUTH-04/05/06 (the actual tables) — this week's API work only became end-to-end testable once those landed.
- **HU-AUTH-09's idempotent registration is being replaced by HU-AUTH-13** (Jordan, ADMIN-only registration with audit) — need to confirm the public `/auth/register` route this HU built gets removed cleanly in the handoff, not left as a second, unprotected path.
- **HU-AUTH-11's service-token issuance is a direct dependency for Workflow and Worker's service tokens** — any change to the ADR-006 permission table has to be re-verified against both consumers.
- **HU-DOCS-89's branch has no confirmed merged-PR URL in the source table** — needs checking before upload.

## 4. Plan for next week

- Confirm with Jordan that `/auth/register`'s public route (HU-AUTH-09) is fully retired once HU-AUTH-13's ADMIN-only flow is live, with no overlapping endpoint left active.
- Add integration coverage for HU-AUTH-11's service-token issuance against the gateway's new public/protected split (HU-GTW-02, Angel).
- Pick up the Auth portal's login screen once `synkro-auth-portal`'s session-ownership documentation (HU-DOCS-92, Angel) is finalized.
- Verify HU-WRK-01's scheduler loop against the first real scheduled job, once one exists.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to `main`, approved by `ariel5253`
- [x] Testable acceptance criteria
- [x] Tests added/updated — 401/403 tests (HU-AUTH-07), integration tests against `TEST_DATABASE_URL` (HU-AUTH-08), idempotency/duplicate-email tests (HU-AUTH-09), reuse-detection tests (HU-AUTH-10), service-token issuance tests (HU-AUTH-11)
- [x] DDD / hexagonal boundaries respected — RS256 validation and JDBC adapters sit in the infrastructure layer; the auth use cases don't import JDBC or servlet types directly
- [x] No secrets; config via environment variables — `TEST_DATABASE_URL` and the RS256 public key read from environment; service tokens shown once and never logged

## 6. Evidence links

- Auth API base structure: `synkro-auth-api` (branch `feat/base-structure-and-health-check`)
- Worker base structure and scheduler: `synkro-worker` (branch `feat/base-structure-and-scheduler`)
- RS256 validation and placeholder removal: `synkro-auth-api`
- JDBC persistence adapters: `synkro-auth-api`
- Registration and user lookup: `synkro-auth-api`
- Logout and reuse detection: `synkro-auth-api`
- Service tokens: `synkro-auth-api`
- Traceability matrix update: `synkro-docs` (branch `docs/add-products-portal-stories`)
