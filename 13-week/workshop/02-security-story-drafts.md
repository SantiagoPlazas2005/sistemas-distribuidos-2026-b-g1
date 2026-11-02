# CP3 — Draft security stories for the team board

Create these as three separate issues in the team's project. Add labels `security` and the shown priority, then link each issue to the STRIDE PR. Story 1 was created as [issue #284](https://github.com/code-corhuila/synkro-docs/issues/284), Story 2 as [issue #285](https://github.com/code-corhuila/synkro-docs/issues/285), and Story 3 as [issue #286](https://github.com/code-corhuila/synkro-docs/issues/286).

## Story 1 — Restrict sale registration to the Workflow service

**Labels:** `security`, `P1`  
**Threat:** STRIDE E — a caller without the narrowly scoped service permission registers a sale.  
**Risk:** 6 (likelihood 2 × impact 3); **Priority:** P1.  
**Related STRIDE PR:** [#275](https://github.com/code-corhuila/synkro-docs/pull/275).
**Status:** Created as [issue #284](https://github.com/code-corhuila/synkro-docs/issues/284).

**As** the SynkroTech system owner, **I want** `synkro-sales-api` to accept `POST /api/v1/sales` only from a valid service token carrying `sales:register`, **so that** users and unrelated services cannot create sales directly.

**Acceptance criteria**

- A missing, invalid, expired, or wrongly signed JWT is rejected with 401.
- A valid token without `sales:register` is rejected with 403 and creates no sale.
- A valid `synkro-workflow` service token with `sales:register` receives `201 Created` and exactly one sale is persisted.
- A valid token with `sales:register` but a subject other than `synkro-workflow` receives 403; caller-supplied `X-User-*` headers do not grant access.
- A negative authorization test runs in CI and fails the check if an unauthorized request creates a sale.

## Story 2 — Preserve sale authorship and idempotency across retries

**Labels:** `security`, `P1`  
**Threat:** STRIDE T — a modified request or retry changes the author/contents or creates a duplicate sale.  
**Risk:** 6 (likelihood 2 × impact 3); **Priority:** P1.  
**Related STRIDE PR:** [#275](https://github.com/code-corhuila/synkro-docs/pull/275).
**Status:** Created as [issue #285](https://github.com/code-corhuila/synkro-docs/issues/285).

**As** a salesperson, **I want** Sales to accept `createdBy` only from the authorized workflow and to atomically honor the saga-step idempotency key, **so that** sale attribution is trustworthy and retries cannot duplicate a sale.

**Acceptance criteria**

- The workflow uses the `sub` captured from the validated initiating-person token as `createdBy`.
- Sales rejects malformed or unauthorized requests and calculates the sale totals itself.
- The same authorized idempotency key and same validated input return the original sale without a second write.
- A conflicting reuse of the key is rejected without exposing stored input or causing participant effects.
- An automated test verifies that `createdBy` equals the validated initiating token's `sub` and is not accepted from caller-controlled request data.
- An automated replay test verifies that the same authorized idempotency key creates one sale and returns the original sale; a conflicting reuse returns the documented generic rejection without a second write or stored-data disclosure.

## Story 3 — Keep credentials and sale details out of logs and errors

**Labels:** `security`, `P2`  
**Threat:** STRIDE I/R — disclosure of secrets or insufficient evidence to investigate who registered a sale.  
**Risk:** I = 3 (likelihood 1 × impact 3); R = 4 (likelihood 2 × impact 2); **Priority:** P2 (highest linked risk: 4).  
**Related STRIDE PR:** [#275](https://github.com/code-corhuila/synkro-docs/pull/275).
**Status:** Created as [issue #286](https://github.com/code-corhuila/synkro-docs/issues/286).

**As** a security operator, **I want** the Workflow-to-Sales call to propagate a correlation ID and record a safe actor/action/result trail, **so that** I can investigate a sale without exposing credentials or unnecessary personal data.

**Acceptance criteria**

- Structured events include the correlation ID, saga/step identifier, initiating user ID where appropriate, action, result, and timestamp.
- `Authorization`, JWT contents, passwords, and unnecessary PII are excluded from application logs.
- Client errors use the common generic error envelope and do not include stack traces, MongoDB details, or stored request bodies.
- A test or CI check fails if a known token marker or prohibited field appears in captured logs/error responses.

**Book [B]:** Costas Santos, Jesús. _Seguridad informática_ (1st ed.). Bogotá: Ediciones de la U, 2011, ch. 1, p. 15. Library call no.: `005.8 C839s Ej. 1`. ISBN 978-958-8675-70-1. Santiago confirms that he read this page after locating the book again. Chapter 1 introduces information-security principles, including confidentiality, which supports this story's focus. Do not publish private library-record details in the issue.
