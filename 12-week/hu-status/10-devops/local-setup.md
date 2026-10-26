# Local Project Setup

> Starts the whole system on a new computer, from scratch. If any step here
> does not match reality, fix it in the same PR that found the gap — an
> onboarding guide that lies is worse than none.
>
> This document is the "type this literally" version of
> `05-architecture/deployment.md` §9, aimed at a new developer's first run.
> If the two ever disagree, `deployment.md` is the source of truth.

---

## Prerequisites

| Tool | Minimum version | Verify with |
|------|-----------------|-------------|
| Docker | 24.0+ | `docker --version` |
| Docker Compose | 2.24.4+ for the candidate include/override model | `docker compose version` |
| Git | 2.40+ | `git --version` |
| Python | 3.11+ for root operations | `python --version` |
| Node.js | 22+ for Mongo owner tooling; frontend uses its pinned package runtime | `node --version` |
| JDK | 21 | `java --version` |
| Maven | 3.9+ | `mvn --version` |
| Go | the release pinned in `go.mod` | `go version` |

You need the JDK/Maven pair only if you are working on a Java service
(`synkro-auth-api`, `synkro-customers-api`, `synkro-workflow`); you need Go
only for a Go service (`synkro-products-api`, `synkro-sales-api`,
`synkro-worker`). Docker is required either way, to run the shared
PostgreSQL instance, the MongoDB instance, and the services you are not
actively developing.

**Recommended, not required:** an editor with Java and Go support (IntelliJ
IDEA, VS Code with the Go extension), and a REST client (Postman, Insomnia,
or `curl`) for testing endpoints manually.

---

## Local ports in use

| Port | Service | Published to the host? |
|------|---------|------------------------|
| 8000 | `synkro-api-gateway` | Yes — the only API entry point |
| 5173 | `synkro-front` | Yes |
| 3000 | Grafana | Yes |
| 9090 | Prometheus | Yes |
| 8080 | Four APIs and Workflow | No — internal `platform` |
| 80 | Portal asset containers in selected root | No — served through Host; Products tag publishing 5175 is excluded from this lock |
| 5432 | `synkro-db` (shared PostgreSQL instance) | No |
| 27017 | `synkro-mongo` (MongoDB instance, holds the `sales` database) | No |

`synkro-worker` listens on no port. Full detail:
`05-architecture/deployment.md` §2.

**Check a port is free before starting:**
```bash
# macOS / Linux
lsof -i :8000

# Windows
netstat -ano | findstr ":8000"
```

---

## Step-by-step installation

### Step 1 — Clone every repository as a sibling of `synkro-infra-postgres`

Obtain approved read access to both private infrastructure repositories and the owners. Clone all 18 code repositories listed in the [release manifest](releases/2.0.0.md), plus `synkro-docs`, as siblings. No optional sibling or stub is silently substituted in the full candidate. Use a new review workspace so existing user work is preserved.

```bash
mkdir synkrotech-review
cd synkrotech-review
git clone https://github.com/code-corhuila/synkro-infra-postgres.git
git -C synkro-infra-postgres fetch origin feat/delivered-composition-lock
git -C synkro-infra-postgres switch --detach 8139a43e786f3ab7adf6367065a6165d8d83c15e
cd synkro-infra-postgres
python scripts/fetch_dependencies.py
python scripts/verify_lock_tags.py
```

These commands select the **open PR #13 candidate**, not a promoted root release. The fetcher uses `dependencies.lock.json` and approved access, retrieves absent siblings into owned staging directories, checks exact HEAD/clean state and refuses conflicting existing checkouts. Do not force-reset or delete a teammate's checkout. For PR #14/browser reproduction use its distinct head and record that lock. Published component tags alone cannot substitute for the candidate Products Portal/Worker or Sales DB pin.

### Step 2 — Environment variables

```bash
cp env/dev.env.example env/dev.env
```

Provision the protected absolute files and metadata in [deployment section 6](../05-architecture/deployment.md#6-configuration): five distinct PostgreSQL password files, Auth private/public keys, Grafana password readable by UID 472, and owner-only Mongo environment configuration. Keep secrets outside every repository/root mount. Set `DATABASE_URL` only for `products_app` and `SALES_APP_MONGODB_URI` only for `sales_app` with `authSource=admin&replicaSet=rs0`. Root derives separate administrator runner credentials in memory. Inline Products public PEM must match its public-key file. Do not print expanded configuration.

`python scripts/synkro.py dev-identity dev` is the actual optional development-only key/SERVICE-token generator; it refuses existing outputs and qa/main. It creates no human ADMIN. Root examples keep `VITE_DEV_SIGN_IN=false`; normal login uses Auth. Use the delivered initialization CLI after migrations for the first ADMIN; no local seed or simulated token replaces it.

### Step 3 — Start the platform

Prepare the engines first, with no normal application traffic. From the candidate root:

```bash
python scripts/synkro.py up dev --mode postgres-migrations
python scripts/synkro.py status dev --mode postgres-migrations
```

This preparation mode starts PostgreSQL; owning runners are explicit tooling and do not run from `up`. The operations wrapper creates `platform` only if absent, validates credentials/exposure and refuses conflicting persistent environment aliases. Stop/operate environments sequentially on that shared network. Full application startup follows migrations and administrator initialization in Steps 4–5, rather than treating pre-migration errors as normal readiness.

### Step 4 — Migrate each store

```bash
python scripts/synkro.py migrate dev --mode postgres-migrations --domain auth
python scripts/synkro.py migrate dev --mode postgres-migrations --domain customers
python scripts/synkro.py migrate dev --mode postgres-migrations --domain products
python scripts/synkro.py migrate dev --mode postgres-migrations --domain workflow
python scripts/synkro.py privileges dev --mode postgres-migrations
python scripts/synkro.py down dev --mode postgres-migrations
python scripts/synkro.py up dev --mode mongo-migrations
python scripts/synkro.py migrate dev --mode mongo-migrations --domain sales
python scripts/synkro.py down dev --mode mongo-migrations
```

Flyway history belongs to each PostgreSQL schema; Sales Liquibase history belongs to Sales. Repetition applies zero new changes. A legacy shared Flyway history/checksum mismatch fails for owner reconciliation, never repair. Restart PostgreSQL with `python scripts/synkro.py up dev --mode postgres-migrations`, then follow [Initial administrator initialization](../13-operations/initial-administrator-initialization.md) while the target remains closed to normal traffic. The CLI's exit 5 means unknown commit outcome, not rollback. Existing ADMIN returns no-op; initialization is not TD-009 recovery.

For qa/main select its explicit environment; the supported root migration wrapper requires a successful backup first. Preserve volumes and immutable applied migrations. [Backup and recovery](../13-operations/backup-and-recovery.md) covers compatible restoration and manual rollback prerequisites.

### Step 5 — Verify

```bash
python scripts/synkro.py up dev
python scripts/synkro.py status dev
curl --fail http://localhost:8000/health
```

Open `http://localhost:5173` and authenticate through Auth/Gateway. Check authorized Customer/Product reads, Sales history and the explicitly selected registration/detail scope. Verify durable business effects and ownership against the selected evidence checklist; a gateway response alone does not establish all stores or saga behavior. Products health remains TD-017; Worker only logs placeholder execution. Grafana/Prometheus at loopback 3000/9090 show tools, not proven end-to-end service telemetry.

Independent full reproduction uses the versioned [reviewer package](https://github.com/code-corhuila/synkro-infra-postgres/blob/8139a43e786f3ab7adf6367065a6165d8d83c15e/docs/reviewer-package-2026-10-22.md); its record is still pending. Do not report the documented commands as executed by this documentation update. The known Sales DB pin conflict prevents a fully green owner-gate result with the current lock.

---

## Working on a single service

Use the owner README, exact runtime and a migrated disposable/local owning store; other domain state is accessed only through its API. The database is internal on `platform`. A host-run service cannot use `synkro-db` DNS or localhost:5432 unless an explicit isolated development arrangement is provided; do not publish the persistent database to make the old host-run example work. Prefer the owner's container on the existing network, or its disposable test fixture.

Use the Java/Go/frontend stack guides for build commands. Root preparation mode is a subset, not complete integration acceptance. Missing siblings/credentials fail; do not comment out root includes and then describe that result as full startup.

---

## Daily workflow

```bash
git fetch origin
git switch -c feat/short-description origin/develop
# Review and apply any new owner migrations through the explicit environment CLI.
# Follow the HU criteria and TDD for production-code changes.
git add <specific-files>
git commit -m "feat(scope): description"
git push -u origin feat/short-description
```

For documentation use `docs/<short-kebab-description>` from docs main and a `docs(scope): description` commit with the scope matching the change. Promotion of code is `cherry-pick -x` through destination child branches and reviewed PRs; no permanent-branch merge. A new owner revision needs a reviewed lock update and affected reruns, not an unrecorded pull. Before stopping select the same environment/mode; the wrapper preserves its volumes.

---

## Common issues

### "Port already in use"

Identify the listener with `lsof -i :8000` or Windows `netstat -ano | findstr :8000`. Coordinate with its owner; stop only the selected project through its supported command. Do not kill arbitrary processes or prune shared Docker resources.

### "Docker containers not starting"

Check Docker/Compose versions, exact candidate lock, protected input readability, include paths and the sanitized operation error. Use `python scripts/synkro.py status dev` for the selected engine and inspect only safe, access-restricted diagnostics. `python scripts/synkro.py down dev` preserves data. Volume deletion is not a documented repair.

### "`synkro-db` or `synkro-mongo` healthy but a service answers 500 on every request"

Check that the correct owner migrations/history/grants exist, that application credentials match bootstrap, and that the service points to its own store. Engine health does not prove schema readiness. Wrong credentials, absent tables and real implementation faults remain failures; do not classify every 500 as expected migration work.

### "401 on every request, even with a token"

Check the Auth-issued credential's expiry, allowed algorithm, audience/use and consistent public key; renew through normal login/Host rules. Development key regeneration invalidates old credentials and must not be used to repair qa/main. Issued JWT revocation limits remain ADR-017.

### "A service I need isn't cloned"

Use approved access and `fetch_dependencies.py` against the recorded lock in a clean review workspace. Private source read access is separate from a successful private-integration workflow. Do not broaden GitHub permissions, silently substitute stubs or patch owner source from Infra.

---

## Correlations

- Full deployment topology, ports and the composition rule → `05-architecture/deployment.md`
- Environment variables per service → `05-architecture/deployment.md` §6
- The 3 project environments → `10-devops/environments.md`
- Configured CI/CD jobs and their real execution limits → `10-devops/ci-cd.md`
- Per-stack commands and folder layout → `_stacks/go.md`, `_stacks/java-spring.md`
- Candidate/persistent operation troubleshooting → `13-operations/incident-management.md`
