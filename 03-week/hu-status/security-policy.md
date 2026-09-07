# Security Policy

> Security is not a feature — it is a property of the system built from day one.
> This document defines the mandatory practices.
> Any deviation must be explicitly approved by the team.

---

## Security Principles

1. **Defense in Depth:** multiple layers of security. If one fails, the others contain the damage.
2. **Least Privilege:** each component has only the minimum permissions required (see the database-user-per-schema mechanism in ADR-001).
3. **Fail Securely:** in the event of an error, the system denies access rather than allowing it.
4. **Security by Design:** security controls are designed from the beginning, not added at the end.
5. **Zero Trust:** always verify, never implicitly trust, even within the internal network (each microservice validates the JWT locally, without exception).

---

## Authentication

### JWT (JSON Web Tokens)

| Property | Required Value |
|----------|---------------|
| Signature Algorithm | RS256 (asymmetric) — defined in ADR-001 |
| Access Token Expiration | 1 hour (`exp`) |
| Refresh Token Expiration | 7 days |
| Required Claims | `sub` (user_id), `roles`, `permissions`, `iat`, `exp`, `jti` (unique token ID) |
| Client Storage | `httpOnly cookie` (web) |

**Prohibited in the Payload:**

- Passwords
- Card data
- Full PII (only the user ID)

### Refresh Token

- Stored in the `auth` schema database (with bcrypt hash)
- Mandatory rotation on every use (one refresh token = one-time use)
- Invalidated on logout and password change
- ALL active tokens are invalidated if the use of a revoked token is detected

---

## Authorization

### RBAC (Role-Based Access Control)

| Role | Description | Permissions |
|------|-------------|------------|
| `ADMIN` | Business administrator | Full access: users/roles, customers, products, sales, and reports |
| `SALESPERSON` | Sales staff | Manages customers, creates sales, checks stock, views reports of their own sales |
| `INVENTORY` | Inventory staff | Manages products, categories, and stock; no access to customers, sales, or reports |

**Permission Model:**

```text
Permission: [action]

Examples applied to the project:
  customers:create
  customers:read
  customers:update
  customers:delete
  products:read
  products:write
  sales:create
  sales:read
  reports:read
  users:manage
```

**Validation:**

- Each microservice validates the JWT locally (signature and expiration) using Auth's public key.
- Each service validates role permissions for the specific operation on its own resources.
- Roles are included in the JWT as the claim `roles: ["SALESPERSON"]`.

---

## Secure Communication

### Transmission

- **HTTPS is mandatory** in all environments except local
- Minimum TLS 1.2; TLS 1.3 recommended
- HSTS enabled in production

### Internal Communication Between Services

- Bearer token (JWT) for synchronous communication between the 4 microservices (Sales → Customers, Sales → Products)

---

## Secret Management

```text
✗ NEVER in source code
✗ NEVER in a committed .env file
✗ NEVER in logs
✗ NEVER in client-facing error messages
✓ Environment variables
✓ Per-service database credentials (auth_user, customers_user, products_user, sales_user) injected through environment variables
```

**Secret Rotation:**

- Database passwords: every 6 months or immediately if compromise is suspected

---

## Input Validation and Sanitization

### General Rules

1. **Never trust user input.** Validate at the edge (controller) before processing.
2. **Whitelist, not blacklist.** Define what is allowed, not only what is forbidden.
3. **Fail fast.** If input is invalid, return 400 and stop processing.

### SQL Injection Prevention

When using separate schemas per service (see ADR-001), each service MUST use parameterized queries exclusively on its own schema — never build dynamic SQL using schema or table names from user input.

---

## OWASP Top 10 — Review Checklist

| Vulnerability | Implemented Control |
|---------------|-------------------|
| A01: Broken Access Control | RBAC + permission validation in every service |
| A02: Cryptographic Failures | TLS 1.2+, bcrypt for passwords, RS256 for JWT |
| A03: Injection | Prepared SQL statements, schema validation |
| A05: Security Misconfiguration | Review of default values before every release |
| A07: Authentication Failures | JWT with rotation, minimum schema permissions per service |
| A09: Logging Failures | Logs without PII, with security events recorded |

---

## Security Auditing and Logging

### Events That Are ALWAYS Logged

```text
auth.login.success
auth.login.failure
auth.token.revoked
auth.unauthorized_access_attempt
admin.role.changed
```

**Required Fields in Security Logs:**

- `userId` (or `ANONYMOUS` if not authenticated)
- `action`
- `resource`
- `result` (SUCCESS / FAILURE)
- `timestamp`

---

## References

- Security Non-Functional Requirements → `04-requirements/non-functional.md`
- Authentication ADR → `05-architecture/decisions/records/ADR-001-architecture.md`
- RBAC implemented in → `09-microservices/services/01-auth/`