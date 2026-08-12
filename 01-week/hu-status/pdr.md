Aporte Individual al PDR

*project_key:* PRJ-GESTION-VENTAS-V1
member: Fredman Santiago Plazas Artunduaga
assigned points: 8, 9, 10 y 11 — Risks, Work Plan, Acceptance Criteria and Next Steps
Responsibility: Identify the project risks, propose a phased work plan, define when the preliminary design is considered accepted, and establish the specific next steps.



---

## 8. Identified Risks and Mitigation

| Risk | Probability | Impact | Mitigation |
| ---- | ----------- | ------ | ---------- |
| Unresolved business questions (editing/cancellation of transactions, cumulative balance vs. balance by period) may change the model halfway through development | High | High | Resolve these decisions with the instructor/client **before** starting the implementation of the Transactions service |
| Ambiguity in the initial income and expense categories | Medium | Medium | Define a minimum base catalog (aggregated sales, payments, merchandise purchases, operating expenses) validated by the business before coding |
| Human or calculation error in the net balance (income − expenses) | Low | High | Perform unit tests on the calculation logic; validate with manual test cases before integrating with the frontend |
| Reports becoming misaligned with actual transactions if a separate summary table is used | Medium | Medium | In the initial phase, the Reports service should query transactions directly through the API instead of maintaining a replica |
| Tendency to over-design the system by adding inventory, invoicing, or POS functionality outside the defined scope | Medium | High | Reinforce during each review that the scope is aggregated financial control, not an ERP or point-of-sale system |
| Lack of definition regarding users/roles (open question: one or multiple administrators?) | Medium | Low | Design authentication to support a single administrator role in the MVP, with the possibility of extending it later |

---

## 9. Preliminary Work Plan

| Phase | Activity | Deliverable |
| ---- | -------- | ----------- |
| 1 | Resolve critical open questions (initial categories, transaction editing/cancellation, cumulative balance) and define the hexagonal architecture template in Go | Decision document + base project template |
| 2 | Development of the Categories service | Functional microservice + categories database |
| 3 | Development of the Transactions service (income and expense registration, balance calculation) | Functional microservice + transactions database |
| 4 | Development of the Reports service (daily, weekly, and monthly summaries) | Functional microservice |
| 5 | Development of the Angular frontend, integrating the services mentioned above | Functional interface |
| 6 | End-to-end integration testing (register transaction → query summary) | Test report |
| 7 | Final documentation and preparation of the presentation/demo | Technical document + demo |

---

## 10. PDR Acceptance Criteria

The preliminary design is considered accepted if:

- The team validates that the scope is limited to aggregated financial control (without detailed inventory, electronic invoicing, or point-of-sale functionality).
- The proposed initial income and expense categories cover the cases described in the business context.
- The preliminary data model allows the registration of a transaction (date, category, amount, note) and the calculation of income, expenses, and net balance by period (day, week, month).
- The most impactful open questions in the design (transaction editing/cancellation and cumulative balance) have a decision or, at minimum, a clear plan to resolve them before Phase 2.
- The identified risks have a mitigation strategy accepted by the team.
- There are no evident technical blockers that would prevent implementation from starting.

---

## 11. Next Steps

1. Validate this PDR with the instructor/client of the project.
2. Resolve the priority open questions with the business (or with the instructor, acting as the client): initial categories, whether transaction editing/cancellation is allowed, and whether the balance should be displayed only by period or also cumulatively.
3. Define the Go hexagonal architecture folder structure and configure the container environment (Docker) with MySQL.
4. Configure the necessary repositories (backend for each service, frontend, and database) and development environments.
5. Start Phase 1 of the work plan.
