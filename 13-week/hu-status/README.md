<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       13-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 13

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group 1
- SPRINT_GOAL: Complete the Week 13 threat-modeling deliverables for the Workflow-to-Sales boundary and document verified authorization-test evidence.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| #284 | Restrict sale registration to the Workflow service | todo | [Issue #284](https://github.com/code-corhuila/synkro-docs/issues/284); [STRIDE PR #275](https://github.com/code-corhuila/synkro-docs/pull/275) |
| #285 | Preserve sale authorship and idempotency across retries | todo | [Issue #285](https://github.com/code-corhuila/synkro-docs/issues/285); [STRIDE PR #275](https://github.com/code-corhuila/synkro-docs/pull/275) |
| #286 | Keep credentials and sale details out of logs and errors | todo | [Issue #286](https://github.com/code-corhuila/synkro-docs/issues/286); [STRIDE PR #275](https://github.com/code-corhuila/synkro-docs/pull/275) |

## 2. My individual contribution
- Claimed and mapped the `synkro-workflow` → `synkro-sales-api` trust boundary, then authored the STRIDE analysis in PR #275 and incorporated the review concern about transitive trust in `createdBy`.
- Created three security backlog stories (#284–#286), refined their acceptance criteria and risk metadata, and linked them to the STRIDE PR.
- Ran `TestSalesHTTPRegistrationAndReplay` locally and recorded its console output; validated `.github/workflows/w13-security.yml` with actionlint.

## 3. Blockers and risks
- The three security stories remain open in Backlog; implementation and ownership are still for the team to plan.
- Production secret access, deployed permission grants, log redaction, and the 30-day service-token exposure window have not been verified by the local test. The test uses the service HTTP router with in-memory persistence.

## 4. Plan for next week
- Coordinate with the team to prioritize the open security stories, confirm deployment configuration, and implement and verify the P1 authorization and sale-integrity controls before treating them as done.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- STRIDE PR: https://github.com/code-corhuila/synkro-docs/pull/275
- Stories: https://github.com/code-corhuila/synkro-docs/issues/284, https://github.com/code-corhuila/synkro-docs/issues/285, https://github.com/code-corhuila/synkro-docs/issues/286
- Successful security workflow run: https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/actions/runs/41028704389
- Latest workshop commit before CP9: https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/9946acc
