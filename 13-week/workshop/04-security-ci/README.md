# CP4 — Security CI evidence

**Status:** not run yet. This README must contain a URL to a real GitHub Actions run before it is submitted; do not claim a pass without execution evidence.

## Planned checks

- Secret scanning (Gitleaks) on pushes and pull requests.
- Dependency audit appropriate to the actual source repository/language; record the tool and result.
- Negative authorization test from Story 1: a caller without `sales:register` receives 403 and creates no sale.
- A failing security gate keeps the PR open and the linked story unresolved.

## Execution evidence

- Workflow file: [`../../../.github/workflows/w13-security.yml`](../../../.github/workflows/w13-security.yml)
- Run URL: **pending real run**
- Result and relevant failed/passed checks: **pending**

The authorization test is run against the pinned Synkro Sales API source revision documented by the team repository. `TestSalesHTTPRegistrationAndReplay` verifies that callers without the required service permission receive `403` and do not reach persistence. The workflow also runs Gitleaks on this class repository and `govulncheck` on the Sales Go module.

Do not paste secrets, tokens, private library records, or screenshots containing credentials here.
