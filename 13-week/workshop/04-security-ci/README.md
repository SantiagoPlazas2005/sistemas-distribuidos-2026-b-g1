# CP4 — Security CI evidence

**Status:** executed successfully on 2026-10-30; the linked run is the evidence.

## Planned checks

- Secret scanning (Gitleaks) on pushes and pull requests.
- Dependency audit appropriate to the actual source repository/language; record the tool and result.
- Negative authorization test from Story 1: a caller without `sales:register` receives 403 and creates no sale.
- A failing security gate keeps the PR open and the linked story unresolved.

## Execution evidence

- Workflow file: [`../../../.github/workflows/w13-security.yml`](../../../.github/workflows/w13-security.yml)
- Run URL: [Week 13 security checks — run #2](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/actions/runs/41028704389)
- Result: Gitleaks passed; `TestSalesHTTPRegistrationAndReplay` passed; `govulncheck ./...` passed using Go 1.26.10.

The first run detected reachable standard-library vulnerability GO-2026-6809 in Go 1.26.9. The audit was updated to use patched Go 1.26.10, and the successful run above confirms the checks pass with that toolchain. The first run remains visible in GitHub Actions history.

The authorization test is run against the pinned Synkro Sales API source revision documented by the team repository. `TestSalesHTTPRegistrationAndReplay` verifies that callers without the required service permission receive `403` and do not reach persistence. The workflow also runs Gitleaks on this class repository and `govulncheck` on the Sales Go module.

Do not paste secrets, tokens, private library records, or screenshots containing credentials here.
