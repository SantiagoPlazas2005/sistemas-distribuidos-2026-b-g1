# Initial Administrator Initialization

> ADR-014 operational procedure, reconciled in Week 12 (2026-10-25).
> HU-AUTH-14 delivered InitialAdminCli in Auth API v2.0.0; owning source and
> author evidence are linked below. Initialization is separate from recovery.

## Authorization and preconditions

- The deployment operator assigned to the target environment records their deployment-session identity and an execution correlation identifier.
- The environment is in its first initialization and is closed to normal user traffic. If normal operation has begun, stop: this runbook does not authorize recovery.
- Auth migrations, including user_creation_audit and its grants, have completed. Verify auth_app has access only to auth_schema and cannot update/delete/truncate audit records.
- Use the release's documented initialization CLI and auth_app connection configuration on the platform network. Neither PostgreSQL administrator credentials nor a new HTTP endpoint is used.
- Verify the target environment, database and release before entering secrets. The operator must not rely on development identity scripts.

## Inputs and command contract

Use Auth API `e3ea374fbfb428e2583b5a776295e60b0e229696` and the reviewed Auth DB migration revision for the selected environment. The [owning README](https://github.com/code-corhuila/synkro-auth-api/blob/e3ea374fbfb428e2583b5a776295e60b0e229696/README.md#initial-administrator) and [InitialAdminCli source](https://github.com/code-corhuila/synkro-auth-api/blob/e3ea374fbfb428e2583b5a776295e60b0e229696/auth-app/src/main/java/co/edu/corhuila/synkro/auth/app/InitialAdminCli.java) establish the actual executable. The jar is `/app/auth-app.jar` in the service image. Build that pinned owner's `deploy/Dockerfile`; no registry artifact is assumed.

| Input | Protected source / rule |
|---|---|
| Database | `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME=auth_app`; exactly one of `SPRING_DATASOURCE_PASSWORD_FILE` or `SPRING_DATASOURCE_PASSWORD`. Use the file form below. No administrator credential. |
| Name/email | `SYNKRO_INIT_ADMIN_NAME` / `SYNKRO_INIT_ADMIN_EMAIL` or `--name`/`--email`. Ordinary registration rules; normalized email, name at most 150 characters. Keep actual personal details out of shared execution reports. |
| Initial password | `SYNKRO_INIT_ADMIN_PASSWORD_FILE` or `--password-file`; otherwise read twice without echo from a real console. No console and no file fails. Bcrypt input 8–72 bytes. Trailing CR/LF is stripped; do not rely on significant trailing newlines. |
| Operator | Required `SYNKRO_INIT_OPERATOR` / `--operator`, 1–200 characters without controls. Attribution from the controlled deployment session; the CLI cannot independently authenticate that string. |
| Correlation | `SYNKRO_INIT_CORRELATION_ID` / `--correlation-id`, at most 128 characters; generated UUID if omitted. Preserve it with the outcome and audit. |

Provision two absolute, access-restricted files outside repositories; mount them read-only and ensure the image's non-root `synkro` user can read them. Supply nonsecret target/name/email/operator metadata through a protected deployment environment file, with no inline passwords or authenticated URIs. That file must set the datasource file path inside the container and must not set an inline datasource password simultaneously.

```bash
# Build from the pinned Auth owner checkout (Bash example).
docker build -f deploy/Dockerfile -t synkro-auth-api:initial-admin-e3ea374 .
# Metadata and file paths are configured in the protected deployment file.
docker run --rm --network platform \
  --env-file /absolute/protected/initial-admin.env \
  --mount type=bind,source=/absolute/protected/auth-app-password,target=/run/secrets/auth_app_password,readonly \
  --mount type=bind,source=/absolute/protected/initial-admin-password,target=/run/secrets/initial_admin_password,readonly \
  --entrypoint java synkro-auth-api:initial-admin-e3ea374 \
  -cp /app/auth-app.jar \
  -Dloader.main=co.edu.corhuila.synkro.auth.app.InitialAdminCli \
  org.springframework.boot.loader.launch.PropertiesLauncher \
  --password-file /run/secrets/initial_admin_password
```

The deployment file supplies `SPRING_DATASOURCE_URL` for the selected Auth store, `SPRING_DATASOURCE_USERNAME=auth_app`, `SPRING_DATASOURCE_PASSWORD_FILE=/run/secrets/auth_app_password`, administrator name/email and trusted operator/correlation metadata. Use the actual absolute path syntax of the host; the example is Bash, not a PowerShell continuation example. For a locally packaged jar use the same `-cp`/`-Dloader.main`/`PropertiesLauncher` arguments with its real jar path. No JWT key, HTTP route or application ADMIN caller is involved. No `Idempotency-Key` is needed: retries use the locked existence check.

### Outcomes and exit codes

| Exit | Output / meaning | Next action |
|---|---|---|
| 0 | `result=created` with user ID/correlation; user and audit committed | Verify audit/login; clean temporary input |
| 0 | `result=already_initialized`; any ADMIN exists, including inactive; no changes | Verify intended target; never reset password/role |
| 1 | Unexpected failure, safe exception type | Preserve correlation, investigate; no blind success/rollback inference |
| 2 | Invalid arguments/input/configuration or email already used by another non-ADMIN | Correct input; nothing written |
| 3 | 10-second advisory lock wait expired | Inspect lock holder before an explicit retry; nothing written |
| 4 | Database failure before commit | Repair connection/store and retry explicitly; nothing retained |
| 5 | Commit sent, confirmation lost; result unknown | Reconcile/retry same target/settings: committed ADMIN gives no-op; do not assert rollback or create via SQL |

Errors expose safe categories/SQLSTATE, never passwords, hashes, connection URLs or raw driver messages. Outcomes log `operation=INITIAL_ADMIN_CREATE`, outcome, operator, safe reason, UTC time and correlation. The owner reports container created/no-op executions against real migrations; this documentation update runs none and does not establish persistent environment readiness.

## Transaction protocol

1. Connect to the target database as auth_app and begin explicit READ COMMITTED.
2. Apply a finite lock-wait timeout and acquire the single-bigint pg_advisory_xact_lock key reserved in 06-data/models.md.
3. After the lock statement completes, issue a separate query for any system_user with role ADMIN, without filtering active.
4. If found, commit without changing users, passwords or creation audit records; report `already initialized` and log the no-op with the execution identity.
5. Otherwise, insert one ADMIN and its INITIAL_ADMIN_CREATED audit record in the same transaction, with the trusted operator identity.
6. Commit and report `created` with user ID and correlation identifier, never the password. Release the lock automatically at transaction end.

All steps use one connection and transaction. The single-bigint lock is `1398361682`; its 10-second timeout bounds lock waiting, not the entire command. The CLI uses a separate connection with connect/socket timeouts 10/60 seconds, not the HTTP service pool. Precommit failure rolls back; lost commit confirmation returns exit 5, whose outcome remains uncertain. It never falls back to an unlocked insert. An authorized retry resolves the existing-state result.

## Verification and cleanup

- Confirm the reported user is ADMIN and active and has exactly one corresponding initialization audit record. A no-op creates no new record.
- Verify login through the normal gateway with the operator-supplied initial credentials. Keep all tokens and passwords out of the execution report.
- Preserve the execution identity, target environment, release, outcome and correlation identifier in the deployment record. Investigate audit discrepancies before enabling normal traffic.
- Remove temporary secret inputs and restrict routine access to the initialization CLI. Subsequent administrators are created by an authenticated ADMIN through register.

## Failure and recovery boundary

Audit insertion failure rolls back user insertion. Invalid input fails without a
user or success record; log the reason without exposing inputs. Do not loop
indefinitely on lock failures. Confirm the target and inspect the active lock
holder before an authorized retry.

An inactive ADMIN still causes a no-op. If every ADMIN is later deactivated,
reassigned or exceptionally removed, do not use this command as recovery.
The check cannot detect former roles and the lock is cooperative, not a durable
initialization marker. Follow the separate recovery decision when approved;
the outstanding gap is recorded in 15-project-control/technical-backlog.md.

## References

- [Architecture decision](../05-architecture/decisions/records/ADR-014-admin-only-registration-and-initial-administrator.md)
- [Transaction and lock-key specification](../06-data/models.md)
- [Audit meanings](../06-data/data-dictionary.md)
- [Deployment sequence](../05-architecture/deployment.md)
- [Evidence handling](observability.md)
