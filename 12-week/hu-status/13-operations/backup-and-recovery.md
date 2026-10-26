# Backup and Recovery

> Week 12 operational record, 2026-10-25. These procedures target the inspected
> Infra PR #13 candidate, not scaffold main/qa. Delivered tests restore into
> disposable targets; persistent recovery/cutover remains a reviewed operation.

## Preconditions and ownership

SantiagoPlazas2005 coordinates operations; AsolanoT coordinates Infra acceptance and release selection; each store owner validates its data/permissions. Record explicit environment, root SHA, dependency lock/hash, owner revisions, operator, UTC timestamp and correlation/run identifier. Quiesce writers and active saga executors when taking a recovery-consistent cross-engine checkpoint. Individual engine dumps do not establish an atomic PostgreSQL/Mongo snapshot or safe saga cutover.

Backups live outside the entire sibling workspace, in operator-protected storage. They contain personal data and, for PostgreSQL globals, credential hashes/memberships; never attach them to issues or commit them. Check actual tool/image major versions, complete metadata, access protection and target compatibility. qa/main migrations require a successful affected-engine backup first. No measured RTO/RPO, scheduled frequency, encryption/retention automation or production restore rehearsal is established; owner/closing proof remain in the release gates and technical backlog.

## Backup commands

From the reviewed candidate with the corresponding engine prepared:

```bash
python scripts/synkro.py backup qa --mode postgres-migrations --destination /absolute/protected/backups
python scripts/synkro.py backup qa --mode mongo-migrations --engine mongo --destination /absolute/protected/backups
```

Select the matching explicit environment. Only completed bundles count. PostgreSQL format 2 includes `roles.sql`, custom-format `database.dump` and `complete.json` with project/bootstrap-role identity; incomplete outputs are not successful backups. The Mongo bundle records source project/engine metadata and Sales archive; it excludes users/grants. Protect outputs and keep the operation's safe completion reference, not contents/credentials, in the deployment record.

## PostgreSQL restoration and verification

```bash
python scripts/synkro.py restore qa --backup /absolute/protected/backups/SELECT_COMPLETED_PG_BUNDLE
```

The shipped candidate creates an exclusive labeled **new disposable target**, restores PostgreSQL 16 globals/data, preserves original bootstrap-superuser identity/grantors and omits only its duplicate CREATE ROLE. It does not overwrite qa or authorize replacing its volume. Guarded cleanup removes that newly owned target after verification. Do not interpret success as recovery of the persistent source.

Verify all owning schema histories/checksums, representative business data, restricted users/cross-schema denials, Auth append-only audit and relevant sale/saga reconciliation. The stock/Sales stores may have different checkpoint times; do not restart all RUNNING work or release reservations merely because one dump restored. Accepted ADR-015 requires evidence-sensitive recovery and retained reservations on ambiguous outcomes.

## MongoDB restoration and verification

Prepare a fresh independent disposable target through the Mongo owner's recorded lifecycle, bootstrap identities and apply the real Sales runner's ten changesets. Retain its exact manifest, project/test-run identity and runner image. Then use the root dispatch:

```bash
python scripts/synkro.py restore qa --engine mongo \
  --backup /absolute/protected/backups/SELECT_COMPLETED_MONGO_BUNDLE \
  --target-env-file /absolute/protected/disposable-restore.env \
  --manifest /absolute/protected/disposable-manifest.json
```

The owner `restore-sales-disposable` procedure verifies an owned empty disposable target and imports only `sales.sale` documents. Target users, roles/grants, validator/indexes and its independently created Liquibase history remain. Never import source authentication/history or infer recovery from a no-op update. Test allowed read/insert/update and denied deletion/cross-database access as target `sales_app`. Only owner-manifest cleanup is allowed; never remove shared `platform` or unrelated volumes. Published Mongo's exact Sales DB pin conflict remains a release blocker; use the recorded lock and retain failure rather than bypass its guard.

## Rollback and persistent recovery boundary

Prefer a reviewed forward fix preserving applied migrations. An application rollback requires proof that the prior binary accepts the current schema/data and that writes/participants can be stopped consistently; a Git checkout alone changes no database. U scripts/Liquibase inverses require a verified backup, explicit target versions, rehearsal on a copy, owner-reviewed data-loss/compatibility analysis and controlled execution. Disposable rollback/rebuild evidence is not permission to roll back persistent data.

Persistent cutover still needs a named operator/approver, consistent engine checkpoint and saga reconciliation, compatible previous build, verified restoration, access protection, validation sequence and measured recovery time/loss. Do not delete qa/main volumes, edit applied migrations, repair histories, reset saga rows or retry a sale with a new key to conceal an uncertain outcome. This document introduces no persistent restore implementation or administrator recovery path (TD-009).

## Evidence and remaining gates

The [release record](../10-devops/releases/2.0.0.md) identifies author A6 restoration results and their exact root/lock; independent reproduction remains pending. Close operational recovery follow-up only with a separately reviewed persistent procedure/rehearsal and real measured evidence. [Incident management](incident-management.md) owns escalation; [observability](observability.md) owns safe execution records; [models](../06-data/models.md) and owner runners own store structure and grants.
