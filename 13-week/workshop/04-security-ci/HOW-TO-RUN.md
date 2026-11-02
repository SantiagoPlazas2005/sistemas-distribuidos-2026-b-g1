# CP8 — Local negative authorization test

Run this from the root of the `synkro-sales-api` repository. It exercises the service's HTTP router and registration authorization checks with the test's in-memory persistence adapter; it does not start MongoDB or the deployed service.

```cmd
go test ./internal/adapter/in/httpapi -run TestSalesHTTPRegistrationAndReplay -v
```

Console output captured on Santiago's Windows machine with Go 1.27.3 (`windows/amd64`):

```text
C:\Users\Personal\Documents\clonado Repos\synkro-sales-api>go test ./internal/adapter/in/httpapi -run TestSalesHTTPRegistrationAndReplay -v
go: downloading github.com/go-playground/validator/v10 v10.22.1
go: downloading github.com/google/uuid v1.6.0
go: downloading github.com/gabriel-vasile/mimetype v1.4.3
go: downloading github.com/go-playground/universal-translator v0.18.1
go: downloading github.com/leodido/go-urn v1.4.0
go: downloading golang.org/x/crypto v0.57.0
go: downloading golang.org/x/text v0.42.0
go: downloading github.com/go-playground/locales v0.14.1
go: downloading golang.org/x/sys v0.48.0
go: downloading golang.org/x/net v0.60.0
=== RUN   TestSalesHTTPRegistrationAndReplay
--- PASS: TestSalesHTTPRegistrationAndReplay (0.11s)
PASS
ok      github.com/code-corhuila/synkro-sales-api/internal/adapter/in/httpapi   3.522s
```

The test verifies that the authorized Workflow service can register once, while callers lacking the required role/permission or using an unrelated service subject are rejected without reaching persistence. The output above is from the local run; it is not the GitHub Actions log.

## Workflow validation

Validated the checked-in workflow `.github/workflows/w13-security.yml` with actionlint v1.7.12.

```cmd
go run github.com/rhysd/actionlint/cmd/actionlint@latest .github/workflows/w13-security.yml
echo %ERRORLEVEL%
0
```

Result: passed with no actionlint diagnostics (exit code `0`). The command downloaded actionlint and its Go dependencies on the first run. This repository names the workflow `w13-security.yml`; it was validated in place and not renamed to match the generic `security.yml` wording in the workshop guide.
