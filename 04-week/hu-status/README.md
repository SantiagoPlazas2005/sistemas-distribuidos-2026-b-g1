<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       04-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 04

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group - synkro-tech
- SPRINT_GOAL: Formalize the domain-to-repository service catalog, fill the product-definition gap (problem framing + vision), and build a panoramic MVP monolith (Spring Boot + React) to validate business understanding, as requested by the instructor for Week 4.
<!-- CONFIG-END -->

## Docs Repository

| Board Name          | URL                                              |
| -------------------- | ------------------------------------------------ |
| synkro-docs Repository | https://github.com/code-corhuila/synkro-docs.git |

## Team Members

| Full Name                          | GitHub User                                                 |
| ----------------------------       | ------------------------------------------                  |
| Sergio Andres Ordoñez Diaz         | https://github.com/SergioAndres17                           |
| Fredman Santiago Plazas Artunduaga | https://github.com/SantiagoPlazas2005                       |
| Jordan Ramirez Gallego             | https://github.com/JordanRG420                              |
| Angel Gustavo Solano Trujillo      |  https://github.com/AsolanoT                                |


## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-DOCS-12 | Fill In Problem Framing and Product Vision | done | https:g |

## 2. My individual contribution

**Context:** the instructor asked for 3 explicit deliverables this week: the domain catalog, continuing the `docs` repo, and a panoramic MVP mockup. My HU covers deliverable #2, closing a gap the team's own tracking had flagged: `03-product` (problem framing + vision) was supposed to come before `04-requirements`/`05-architecture` per `00-sdd-guide.md`, but got skipped while the team was busy fixing the ADR and context-map in Week 3.

- I filled in `problem-framing.md`, naming the three internal roles already defined in RBAC (ADMIN, SALESPERSON, INVENTORY) as the affected user segments, describing the current pain each one has, and defining at least one North Star success metric.
- I filled in `vision.md` using the Geoffrey Moore vision-statement format, keeping it strictly consistent with what's already fixed in `01-context/scope.md` — no new feature was introduced beyond the documented MVP scope.
- I deliberately did **not** redefine bounded contexts in `problem-framing.md`, per the correlation rule in `03-product/README.md` — that modeling work already lives in `02-domain/domain-map.md` and stays untouched.
- Since this is an academic project with no real user interviews or support tickets, I filled the "Evidence of the problem" section using the professor's original business brief / PDR as the closest available stand-in, and labeled it explicitly as such instead of fabricating fake interviews or metrics.
- I converted this HU into the Gherkin-scenario format required by `04-requirements/user-stories.md` and added it there, resolving the "Pendiente inmediato" note that was tracked since last week.

## 3. Blockers and risks
- Because there's no real user research behind this (by design, given the academic context), the North Star metric and the pain points are inferred from the PDR rather than validated with actual users — if the instructor wants real validation, that would need a separate research exercise.
- Now that `03-product` is closed, the next gap to watch is whether `04-requirements` (functional/non-functional requirements) stays consistent with both the vision statement and the existing PDR requirements, since they were written at different times.

## 4. Plan for next week
- Cross-check `04-requirements/functional.md` and `non-functional.md` against this week's `vision.md` for consistency once that section is picked up.
- Support continuing toward `05-architecture`, now that both `03-product` and the service catalog (HU-ARQ-01) are closed.

## 5. Compliance self-check
- [ ] Conventional Commits - `type(scope): summary`
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O)
- [x] No secrets; config via environment variables

## 6. Evidence links
- Problem framing: [`problem-framing.md`](./docs/03-product/problem-framing.md)
- Product vision: [`vision.md`](./docs/03-product/vision.md)
- User stories backlog (HU-DOCS-12 entry): [`user-stories.md`](./docs/04-requirements/user-stories.md)
