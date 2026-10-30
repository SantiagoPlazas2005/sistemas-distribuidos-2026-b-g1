# CP3 — Draft security stories for the team board

Create these as three separate issues in the team's project. Add labels `security` and the shown priority, then link each issue to the STRIDE PR. Story 1 was created as [issue #284](https://github.com/code-corhuila/synkro-docs/issues/284), Story 2 as [issue #285](https://github.com/code-corhuila/synkro-docs/issues/285), and Story 3 as [issue #286](https://github.com/code-corhuila/synkro-docs/issues/286).

## Story 1 — Restrict sale registration to the Workflow service

**Labels:** `security`, `P1`  
**Threat:** STRIDE E — a caller without the narrowly scoped service permission registers a sale.
**Status:** Created as [issue #284](https://github.com/code-corhuila/synkro-docs/issues/284).

**As** the SynkroTech system owner, **I want** `synkro-sales-api` to accept `POST /api/v1/sales` only from a valid service token carrying `sales:register`, **so that** users and unrelated services cannot create sales directly.

**Acceptance criteria**

- A missing, invalid, expired, or wrongly signed JWT is rejected with 401.
- A valid token without `sales:register` is rejected with 403 and creates no sale.
- The workflow service token with `sales:register` can register a valid sale.
- Authorization is checked in the Sales application/use-case layer, not inferred from network location or caller-supplied identity headers.
- A negative authorization test runs in CI and fails the check if an unauthorized request creates a sale.

## Story 2 — Preserve sale authorship and idempotency across retries

**Labels:** `security`, `P1`  
**Threat:** STRIDE T — a modified request or retry changes the author/contents or creates a duplicate sale.
**Status:** Created as [issue #285](https://github.com/code-corhuila/synkro-docs/issues/285).

**As** a salesperson, **I want** Sales to accept `createdBy` only from the authorized workflow and to atomically honor the saga-step idempotency key, **so that** sale attribution is trustworthy and retries cannot duplicate a sale.

**Acceptance criteria**

- The workflow uses the `sub` captured from the validated initiating-person token as `createdBy`.
- Sales rejects malformed or unauthorized requests and calculates the sale totals itself.
- The same authorized idempotency key and same validated input return the original sale without a second write.
- A conflicting reuse of the key is rejected without exposing stored input or causing participant effects.
- Tests verify attribution, duplicate prevention, and rejection behavior.

## Story 3 — Keep credentials and sale details out of logs and errors

**Labels:** `security`, `P2`  
**Threat:** STRIDE I/R — disclosure of secrets or insufficient evidence to investigate who registered a sale.
**Status:** Created as [issue #286](https://github.com/code-corhuila/synkro-docs/issues/286).

**As** a security operator, **I want** the Workflow-to-Sales call to propagate a correlation ID and record a safe actor/action/result trail, **so that** I can investigate a sale without exposing credentials or unnecessary personal data.

**Acceptance criteria**

- Structured events include the correlation ID, saga/step identifier, initiating user ID where appropriate, action, result, and timestamp.
- `Authorization`, JWT contents, passwords, and unnecessary PII are excluded from application logs.
- Client errors use the common generic error envelope and do not include stack traces, MongoDB details, or stored request bodies.
- A test or CI check fails if a known token marker or prohibited field appears in captured logs/error responses.

**Book [B]:** Costas Santos, Jesús. _Seguridad informática_ (1st ed.). Bogotá: Ediciones de la U, 2011, ch. 1, p. 15. ISBN 978-958-8675-70-1. Santiago confirms that he read this page after locating the book again. Chapter 1 introduces information-security principles, including confidentiality, which supports this story's focus. Do not publish private library-record details in the issue.
