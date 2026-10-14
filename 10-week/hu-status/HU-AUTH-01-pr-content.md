# HU-AUTH-01 — PR content (open by hand)

**Base:** `develop` ← **Compare:** `feat/base-structure-and-health-check`

**Title:**
```
feat(auth-api): base structure and health check
```

**Body:**
```markdown
## Description

### Summary

Adds the 3-module Maven hexagonal structure, a working `/health` endpoint, and a default-deny security filter to `synkro-auth-api`. No persistence adapter and no real JWT validation — both are out of scope for this story.

### Changes

- 3-module Maven structure: `auth-core` (domain + application, zero Spring dependency), `auth-adapters` (persistence — stays empty this story), `auth-app` (Spring Boot application).
- `GET /health` — public, returns 200 with the service's health shape.
- Default-deny security filter (`MinimalBearerCheckFilter` + `SecurityConfig`) — every other route answers 401 with the standard error envelope and a trace ID; a present-but-invalid token is forwarded, never rejected by the filter itself.
- `deploy/Dockerfile` (multi-stage Maven build), `deploy/compose.yml`.
- CI extended with `mvn verify` and an architecture check confirming `auth-core` has no Spring dependency.
- README updated with real run and test instructions.

### Definition of Done

- [x] Every behavior's test was written and confirmed failing before its implementation
- [x] `/health` (200) and the default-deny 401 are both verified by automated HTTP tests, not only manually
- [x] `mvn verify` and the architecture check pass in CI
- [x] No persistence adapter and no real JWT signature validation added — both explicitly out of scope per this story's technical notes
- [ ] Reviewed and approved by someone other than the author

Closes code-corhuila/synkro-docs#184
```
