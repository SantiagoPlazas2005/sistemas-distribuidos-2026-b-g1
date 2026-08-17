# Weekly status - Week 01
<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group - PRJ-GESTION-VENTAS-V1
- SPRINT_GOAL: Document the Consequences section of the architecture ADR for the sales management system, capturing the trade-offs of the service split and communication strategy.
<!-- CONFIG-END -->

## Team Members
| Full Name                          | GitHub User                           |
| ----------------------------------- | -------------------------------------- |
| Sergio Andres Ordoñez Diaz         | https://github.com/SergioAndres17      |
| Fredman Santiago Plazas Artunduaga | https://github.com/SantiagoPlazas2005  |
| Jordan Ramirez Gallego             | https://github.com/JordanRG420         |
| Angel Gustavo Solano Trujillo      | https://github.com/AsolanoT            |

## 1. User stories worked on this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-pdr-04 | Risks, work plan, acceptance criteria and glossary | done | [commit or PR URL](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/blob/main/01-week/hu-status/pdr.md)] |

## 2. My individual contribution
- I identified the project risks for the sales management system: coupling from service-to-service communication, data inconsistency between services (sold vs. available stock), the added complexity of asynchronous messaging as an optional later phase, human/calculation errors in the net balance logic, and the learning-curve risk of adopting Go, hexagonal architecture and JWT at the same time. For each one I proposed a concrete mitigation (strict use of ports/interfaces with timeouts, validating stock at sale time, treating async messaging as optional, unit tests on the calculation logic, and a phased schedule).
- I flagged a risk specific to authentication: Auth being the single issuer of JWTs is a critical point — if it goes down, no service can validate new sessions. The mitigation is that each service validates the JWT locally with the public key instead of calling Auth on every request, so only login and token refresh actually depend on Auth's availability.
- I defined the phased work plan (8 phases): training in Go and hexagonal architecture, then building the Auth service (registration, login, JWT, roles) before Clients, Products and Sales (which now also integrates reporting), followed by the React frontend, end-to-end tests (login → register a transaction → check a summary), and final documentation.
- I wrote the PDR acceptance criteria: the team must validate the proposed hexagonal architecture for each service, the functional and non-functional requirements must be considered complete and correct, the preliminary data model must be enough to start detailed design, the identified risks must have an accepted mitigation strategy, and there must be no evident technical blockers to start implementation.
- I outlined the next steps: validating the PDR with the instructor/client, defining the hexagonal folder template for Java and Go, configuring the repositories (backend, frontend/config, databases) and the Docker environment, and starting Phase 1.

## 3. Blockers and risks
- The mitigation for the Auth single-point-of-failure risk (local JWT validation via public key) depends on every service actually implementing signature verification correctly from day one; if any service skips it and calls Auth directly instead, the risk comes back.
- The work plan assumes Auth is built before Clients/Products/Sales; if the team's actual implementation order differs, the risk probabilities and mitigations I wrote may need to be re-sequenced.

## 4. Plan for next week
- Confirm with the team that every service (Clients, Products, Sales) implements local JWT validation as designed, not a call back to Auth per request.
- Follow up on Phase 1 (hexagonal folder templates for Java and Go) so the work plan's sequencing stays accurate.
- Support repository and Docker environment setup as described in the next steps.

## 5. Compliance self-check
- [ ] Conventional Commits - `type(scope): summary`
- [ ] HU branch by environment + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Verifiable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (the domain has no I/O)
- [x] No secrets; configuration via environment variables

## 6. Evidence links
- Contribution to Risks, Work Plan, Acceptance Criteria and Next Steps: [`pdr.md`](./pdr.md)
