# Deployment Environments

> Defines the system's environments, their purposes, access, and key
> configurations. This project has 4 rows in the table below, but 
> only 3 real environment definitions, matching the three permanent Git branches
> (`00-governance/git-conventions.md`). `deployment.md` §4 defines one PostgreSQL
> instance and one MongoDB instance for each of the three persistent environment
> definitions (`develop`, `qa`, `main`), when provisioned. These definitions do not
> prove six running engines.
> Disposable tests use separate owned instances and are not another persistent
> environment. **Local and Development are the same
> environment definition** (the `develop` branch, `env/dev.env.example`),
> run at two different moments: Local is a developer running it on their
> own machine before pushing; Development is the same configuration
> validated after a merge, by CI or by whoever is checking integration.
> Neither has a centrally hosted, always-running server — each is a
> separate physical run of `synkro-infra-postgres` (and `synkro-infra-mongo`),
> with its own `synkro-db` and `synkro-mongo` containers, never shared with
> anyone else's run.
>
> Both infrastructure repositories now exist under their final names. The
> root candidate and the scaffold main/qa revisions are distinguished in
> [the release record](releases/2.0.0.md); branch existence is not a running environment.

---

## Project environments

| Environment | Branch | Purpose | Access | Database | Status |
|-------------|--------|---------|--------|----------|--------|
| **Local** | Work branch from `develop` | Developer validation | Assigned developer, approved repository access | Separate local PostgreSQL/Mongo pair, test data | Candidate procedures available; exact root/lock required |
| **Development** | `develop` or explicit review candidate | Integration after owner delivery | Team/reviewer | Per-run engine pair, isolated volumes | Author disposable integration evidence exists; HU-INF-02 acceptance remains open |
| **Staging (qa)** | `qa` | Validate promotion before main | Team + PO | Dedicated pair and project if provisioned | Permanent branch/examples exist; persistent provision, operator and schedule not established (Q-004). Infra qa is scaffold |
| **Production (main)** | `main` | Academic delivered versions | Team + PO/operator | Dedicated pair and project if provisioned | Component main/tag deliveries exist; full-root production deployment/acceptance is not established. Infra main is scaffold |

No public environment URL, deployed vault, Kubernetes cluster or hosted always-on server is established by this snapshot. The operations run on the selected local/CI Docker daemon. Local is a use of the development definition, not a fourth persistent environment. A run on a disposable test project is evidence for that run, not evidence that `synkro-qa` or `synkro-main` was provisioned.

---

## Environment variables per environment

`synkro-infra-postgres/env/` holds one file per environment, each with its
own `COMPOSE_PROJECT_NAME` to isolate container/volume ownership:

| Environment | Real file (never committed) | Committed example | `COMPOSE_PROJECT_NAME` |
|---|---|---|---|
| develop | `env/dev.env` | `env/dev.env.example` | `synkro-dev` |
| qa | `env/qa.env` | `env/qa.env.example` | `synkro-qa` |
| main | `env/main.env` | `env/main.env.example` | `synkro-main` |

The real file is created from the matching example and is **never
committed** (`05-architecture/deployment.md` §6, ADR-012 Decision 3).

### Naming convention

Variable names follow the credentials fixed by ADR-012, not a generic
`APP_[SERVICE]_[VARIABLE]` scheme:

```
DB_ADMIN_USER / DB_ADMIN_PASSWORD_PATH          — PostgreSQL administrator and protected host file
<DOMAIN>_APP_PASSWORD_PATH                      — distinct protected PostgreSQL application password files
MONGO_ADMIN_USER / MONGO_ADMIN_PASSWORD         — protected Mongo owner configuration; runner administration
SALES_APP_PASSWORD / SALES_APP_MONGODB_URI       — sales_app, authentication in admin, database sales
JWT_PUBLIC_KEY_PATH / JWT_PUBLIC_KEY            — owner-specific public-key file/inline wiring
WORKFLOW_SERVICE_TOKEN / WORKER_SERVICE_TOKEN   — distinct Auth-issued SERVICE credentials
```

`WORKFLOW_SERVICE_TOKEN` and `WORKER_SERVICE_TOKEN` are distinct input names used by the owners' Compose files. Each maps its own value to `SERVICE_TOKEN` inside the corresponding application container; the two services do not share one credential. Workflow additionally supports `SERVICE_TOKEN_FILE` at runtime under its own precedence rule.

See the full variable table per service: `05-architecture/deployment.md` §6.

### Where secrets live, per environment

| Environment | Where the real values come from |
|-------------|----------------------------------|
| Local | Owner-only files outside the sibling workspace and ignored `env/dev.env`; explicit development identity tooling only when needed |
| Development | Same supported configuration contract; authorize and record each run/lock separately |
| Staging (qa) | Environment-managed protected files/input required; actual provisioning and storage owner remain Q-004 |
| Production (main) | Auth-issued identity and environment-managed protected files required; no deployed vault or complete root runtime is asserted |

On shared `platform`, run persistent environments sequentially: Compose project names do not isolate DNS aliases. The root checks conflicting database aliases; never bypass it or remove another project's resources. File precedence is component-specific in [deployment section 6](../05-architecture/deployment.md#6-configuration), not a universal rule. No secret values belong in command arguments, logs or expanded Compose output.

---

## CI/CD Pipeline

Workflows exist in all 18 code repositories, with PostgreSQL Infra's executable workflow on develop/candidate rather than scaffold main/qa. HU-INF-01 established common-file checking; later deliveries added repository-specific jobs. [CI/CD](ci-cd.md) records configured jobs, triggers and actual limitations. The [release record](releases/2.0.0.md) links observed runs: several fail, and Infra reports `startup_failure`. Manual/local evidence does not turn those into green CI or waive the required review/private integration.

---

## Correlations

- Full deployment topology and composition → `05-architecture/deployment.md`
- How to start the Local environment → `10-devops/local-setup.md`
- CI/CD pipeline design → `10-devops/ci-cd.md`
- Branch strategy these environments mirror → `00-governance/git-conventions.md`
- The `qa` environment decision → `15-project-control/open-questions.md` Q-004
- Instance bootstrap and per-environment files → ADR-012
- Sales domain on MongoDB and repository naming → ADR-010
- Secret management rules → `00-governance/security-policy.md`
