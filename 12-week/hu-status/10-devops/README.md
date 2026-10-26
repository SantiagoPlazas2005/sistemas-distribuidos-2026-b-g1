# 10 — DevOps

> **What is this?** How code goes from the developer's computer to production,
> and how it is maintained in each environment. CI/CD, environments, and local configuration.

## Why this section exists

Without documented DevOps:
- Each developer has their own deploy process (and fails differently)
- "It works on my machine" is the most expensive answer in software
- Releases are high-risk events instead of routine

---

## What is here and how to fill it in

### `local-setup.md` ⭐ (Most urgent)

[Local setup](local-setup.md) selects the reviewed Infra candidate/lock, protected configuration, both engines, explicit owning migrations and real Auth initialization. Plain scaffold main is not a full startup. Preserve existing checkouts, volumes and the shared network; respect separate per-environment ownership and sequential shared-DNS operation.

### `environments.md` ⭐

[Environment definitions](environments.md) distinguish local/development use, qa/main branches/examples and actual runtime provision. No hosted server or persistent qa acceptance is inferred. Q-004 retains its owner and Sprint 12 checkpoint.

### `ci-cd.md` ⭐

[Configured CI/CD](ci-cd.md) describes actual owner jobs/triggers and the private integration boundary. HU-INF-01 common checks exist; failed/startup_failure runs and missing gates remain visible. [Release 2.0.0](releases/2.0.0.md) records eighteen repositories, sixteen annotated tags, promotion references, exact roots/hashes and open acceptance gates. No automatic persistent deployment or green whole-platform release is claimed.

---

## Correlations with other sections

| This section is fed by... | And feeds into... |
|---------------------------|-------------------|
| `05-architecture/deployment.md` → how it is deployed | Pipeline implementation |
| `11-quality/testing-strategy.md` → which tests to run | Pipeline steps |
| `09-microservices/` → each service to deploy | Which images the pipeline builds |
| `13-operations/` → post-deploy monitoring | What the pipeline verifies |

---

## Questions this section must answer

- Hexagonal layer structure these pipelines test → `05-architecture/hexagonal-architecture.md`
- Test tiers, tools and coverage thresholds → `11-quality/testing-strategy.md`
- Full deployment topology → `05-architecture/deployment.md`
- Branch strategy → `00-governance/git-conventions.md`
- Definition of Done → `00-governance/definition-of-done.md`
