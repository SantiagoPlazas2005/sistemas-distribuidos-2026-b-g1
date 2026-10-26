# CI/CD Pipeline

> Configured workflows and observed execution, reconciled in Week 12.
> HU-INF-01 established the common-file checks; subsequent deliveries added
> owner-specific validation. A configured workflow, a local pass and a green
> exact-revision remote run are distinct evidence. Required gates remain in force.

---

## Pipeline by repository type

This project has five kinds of repository, and each needs a different
pipeline — there is no one-size-fits-all "PR pipeline" (`testing-strategy.md`,
"CI pipeline per repository type").

### Domain services (`synkro-<domain>-api`, `synkro-workflow`, `synkro-worker`)

| Repository | Configured jobs / real scope |
|---|---|
| Auth API | Common-file check, Maven verify/architecture guard and real migrated-PostgreSQL integration, including initialization paths |
| Customers API | Common files, `tests/verify.mjs` and deployment checks; fixture configuration is required and missing database input fails |
| Products API | Go build/vet/unit/architecture guard and real PostgreSQL persistence job using owner migrations |
| Sales API | Go build/vet/unit/architecture, vulnerability scan and `deploy/ci/mongo-integration.sh` against its exact Sales DB pin |
| Workflow | Maven verify, coverage/deployment/architecture checks and separate real-process recovery suite; real joint participant acceptance is separately attributed |
| Worker | Go build/vet/`go test -race` and architecture guard; passes cover scheduler foundation, not the missing low-stock job |

Read each frozen owner's `.github/workflows/ci.yml` linked by the [release record](releases/2.0.0.md). Designed Pact/Schemathesis tiers and threshold expectations remain in the testing strategy; job names/file presence do not prove that every desired contract/coverage gate is implemented or has passed.

### Database repositories (`synkro-<domain>-db`)

| Repository | Configured jobs / real scope |
|---|---|
| Auth DB / Products DB | `ci.yml` common-file checks plus separate `db-ci.yml` owner migration/rebuild/regression workflow |
| Customers DB | Common files, Node contract checks and serial disposable integration/rebuild/permission checks |
| Sales DB | Common files plus `db-ci.yml` Mongo replica-set migration, preflight/postflight, rollback/rebuild and credential handling checks |

A rebuild after rollback must apply the removed versions again; only a repeated update on an already-current store applies zero. Do not use the former illustrative `flyway undo -target=0` as an executable command: official runner capabilities and manual U scripts differ. All destructive rebuilds use the owners' labeled disposable fixtures, never qa/main data. Applied migrations/checksums remain immutable.

### Gateway (`synkro-api-gateway`)

`check-common-files` and the containerized `smoke-test` build/start NGINX, check routing and health with test upstreams. These prove the gateway fixture, not real-domain acceptance, persistent environments or all platform authorization effects. The exact workflow and main run are linked in the release table.

### Frontend (`synkro-front`, each portal)

| Repository | Configured checks |
|---|---|
| Host | Common/root-deployment guard; npm lint, production build/check and coverage |
| Products Portal | Lint/build/compiled-boundary, unit coverage and container/NGINX validation |
| Sales Portal | npm lint/build/tests/coverage/compiled-boundary, deployment unit checks and prefixed/default container builds |
| Customers Portal | npm lint/typecheck/coverage, script tests, build/compiled custom-element boundary and Compose/container validation |
| Auth Portal | Common files in `ci.yml`; separate `auth-verification.yml` owns frontend verification |

Owner unit/DOM fixtures do not establish browser flows across the real Host/Gateway/domains. Infra adaptations and author browser runs retain exact roots, scope and failed/unexecuted cases; E2E-02 remains NOT EXECUTED in the inspected Sales browser evidence.

### Infrastructure (`synkro-infra-postgres` / `synkro-infra-mongo`)

The two owner repositories exist under their final names. PostgreSQL Infra main/qa have no workflow or root runtime; its develop/PR #13 candidate contains:

| Workflow | Scope / access boundary |
|---|---|
| `ci.yml` | Common-file and root unit/Git/static/protection checks; no claim of full private sibling integration |
| `private-integration.yml` | Explicit dispatch of real engine/owner/full-prerequisite suites using approved private read access; missing access fails, never skips to green |
| Mongo `ci.yml` | Configuration/lifecycle and Sales-owner suite against its recorded Sales DB revision, guarded cleanup |

The root `GITHUB_TOKEN` does not grant private sibling access. Organization/repository administrators must supply approved least-privilege read access and restore Actions execution; this documentation creates no secrets or permission changes. Author/local alternatives do not replace required CI or review. No infrastructure acceptance alternative was adopted by ARQ-28.

---

## When each pipeline runs

| Trigger | What runs |
|---------|-----------|
| Push to `develop`, `qa` or `main` | Owner-configured job set; the branch head must match the evidence |
| PR targeting `develop`, `qa` or `main` | Owner-configured job set. Additional named work-branch patterns vary by file; do not promise every arbitrary feature push triggers CI |
| Release-branch push/PR | Only where the actual workflow includes that pattern (Worker explicitly has `release/**`); verify owner triggers rather than infer from a branch name |
| `workflow_dispatch` | Only configured workflows; PostgreSQL Infra private integration requires explicit approved sibling read access |

Permanent-branch promotion uses reviewed destination child branches and `cherry-pick -x`. Docs uses `docs/*` to main. Code changes under main still require `ariel5253` approval, CODEOWNERS and conversations/check gates. A component tag or merge records delivered content and does not prove those process requirements were satisfied.

No complete hosted automatic deployment is evidenced by this snapshot. Building/test-running a container on CI is different from deploying a persistent academic environment. [Environment definitions](environments.md) retain Q-004 separately from the existence of qa branches.

---

## What blocks a merge, once the pipeline exists

The workflows now exist. Failed required jobs, unmet coverage/contract requirements, missing peer/main approval, unresolved conversations and oversized unreviewable changes remain blockers under the team conventions and Definition of Done.

The release record links actual main runs: several are failed; Infra candidates show `startup_failure`. Component tags/PRs also report owner-authorized local evidence and missing codeowner approval on some historical releases. Those observations are recorded without treating them as a new team exception or retroactive approval. No document update waives protection, validates failed CI or makes local testing equivalent to private integration.

For Infra, an organization administrator diagnoses/restores run startup, owners align the Sales DB pin, and a different teammate records exact-root reproduction. An Infra run with zero jobs has no job-level diagnosis; sibling billing messages do not establish its cause. Re-run applicable checks at the actual corrected head and record run URLs/results, dependency lock and review identity. Successful subsets cannot synthesize an all-green full run.

---

## What this pipeline intentionally does NOT have

| Absent or incomplete step | Why / remaining boundary |
|---|---|
| Uniform all-owner contract/coverage gate | Per-owner implementations differ; testing strategy requirements and missing Pact/peer evidence remain explicit |
| Fully passing private root integration | Approved access, run startup and cross-owner pin conflict remain unresolved; root-only checks cannot substitute |
| Measured persistent staging/production rollout | No complete root promotion/runtime acceptance established; Q-004 still needs provisioning decision |
| Canary/blue-green or registry release pipeline | No such delivery mechanism was adopted/evidenced |

Sales API configures a vulnerability-scan job; it is therefore incorrect to call all dependency scanning absent. Verify each owner's actual pre-release security evidence; missing or failed scans remain missing/failed.

---

## Correlations

- Full test tiers, tools and examples → `11-quality/testing-strategy.md`
- Environments this pipeline targets → `10-devops/environments.md`
- Branch and approval rules → `00-governance/git-conventions.md`
- Definition of Done and required execution/review evidence → `00-governance/definition-of-done.md`
- Vulnerability scanning rule → `00-governance/security-rules.md`, A06
- Migration rebuild check detail → `05-architecture/decisions/records/ADR-005-data-isolation-per-domain.md`, Decision 2
