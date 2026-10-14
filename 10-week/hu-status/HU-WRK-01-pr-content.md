# HU-WRK-01 — PR content (open by hand)

**Base:** `develop` ← **Compare:** `feat/base-structure-and-scheduler`

**Title:**
```
feat(worker): base structure and scheduler loop
```

**Body:**
```markdown
## Description

### Summary

Adds the hexagonal folder structure and a working scheduler loop to
`synkro-worker` — the first service in this project with no HTTP endpoint
at all. Ticks on a configurable interval, bounds each run with its own
timeout, and shuts down cleanly on SIGTERM. No persistence adapter, no
HTTP client, no real low-stock logic — all future work.

### Changes

- Go module and hexagonal folder structure, following `synkro-products-api`'s conventions (config pattern, Dockerfile shape, the fixed `.Standard`-based architecture check).
- `internal/config` — fails fast if `SERVICE_TOKEN` is absent; sane defaults for everything else; a non-positive `LOW_STOCK_EVERY`/`RUN_TIMEOUT` falls back to its default instead of crashing `time.NewTicker`.
- `internal/adapter/in/scheduler` — the scheduler loop: ticks on an interval, bounds each job call with its own timeout, stops cleanly when its context is cancelled. Covered by three tests run with `-race` (no data races), including 200 consecutive native runs and 100 `-race` iterations in a Linux container.
- `cmd/worker/main.go` — composition root with `signal.NotifyContext` for graceful `SIGTERM`/`SIGINT` handling, wired to a placeholder job (the real low-stock check is future work, pending `synkro-products-api`'s stock-alerts endpoints).
- `deploy/Dockerfile`, `deploy/compose.yml` — deliberately no `ports` or `expose`, since this service accepts no connections.
- CI: build, vet, `go test -race`, and the architecture check.

### Verified manually, not only inferred from unit tests

- `SIGTERM` sent to the real compiled binary (via a Linux container, since
  Windows can't deliver a real SIGTERM to a native Go process): the worker
  finished its loop and exited cleanly (code 0).
- Missing `SERVICE_TOKEN`: the binary exits immediately (code 1) with a
  clear log message, no job ever attempted.
- `docker stop` (SIGTERM to PID 1) also exits the container cleanly.

### Definition of Done

- [x] Every behavior's test was written and confirmed failing before its implementation
- [x] The scheduler tests pass under `-race`, not only natively
- [x] Graceful shutdown and fail-fast-without-token both verified against the real compiled binary
- [x] `go vet` and the architecture check pass in CI
- [x] No persistence adapter, HTTP client, or real job logic added — all explicitly out of scope per this story
- [ ] Reviewed and approved by someone other than the author

Closes code-corhuila/synkro-docs#189
```
