<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       04-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 05

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group - synkro-tech
- SPRINT_GOAL: Close the professor's S00/S06/S12 rubric feedback, formally answer the professor's HU-01 (Technology Stack Selection) and HU-02 (Project Discovery) by auditing existing documentation before writing anything new, and open the Corte 1 MVP build in the dedicated `synkro-tech` repository.
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
| HU-DOCS-14 | Governance refinement (S00): DoD, DoR, documentation rules | done | `00-governance/definition-of-done.md`, `definition-of-ready.md`, `documentation-rules.md` |
| HU-DOCS-18 | Discovery: wireframes (Figma) and per-role user flows | doing | `12-ux-ui/wireframes.md`, `navigation-map.md` |
| HU-DOCS-19 | `overview.md` refinement: Frontend alternatives | done | `01-context/overview.md`, "Alternatives Considered" |

## 2. My individual contribution

**Context:** the professor's rubric marked `00-governance` as 🟡 (three files
partial, not adapted from the framework template), and separately gave the
team HU-01 (Stack Selection) and HU-02 (Discovery), which turned out to
require an audit of the existing `docs` content before writing anything new
— most of both HUs' tasks were already answered somewhere in the repo.

**HU-DOCS-14 — Governance refinement:**
- I rewrote `definition-of-done.md`, `definition-of-ready.md`, and
  `documentation-rules.md`, removing every generic-template criterion the
  rubric flagged.
- I found and fixed three real inconsistencies instead of just rewording:
  the old DoD required a `qa` staging smoke test as mandatory, but
  `overview.md` explicitly lists Staging as "Planned — pending decision," so
  I made that step conditional on the environment actually existing; the DoD
  referenced a per-service `data-model.md` path the team never adopted
  (we centralize in `06-data/models.md`) and an `event-catalog.md` for
  async messaging the team hasn't built (our real event catalog today is
  `02-domain/domain-events.md`); `documentation-rules.md` assumed "no fixed
  Tech Lead" and an "already-assigned service owner" — neither is true for
  this team.
- I split both DoR and DoD into two separate checklists, one for code
  stories and one for documentation-only stories, since most of this
  sprint's HUs are documentation, not code.

**HU-DOCS-19 — Frontend alternatives (React vs. Vue.js):**
- I compared React against Vue.js on ecosystem size, learning curve, and
  runtime performance, and evaluated both against NFR-01 (browser
  accessibility) and NFR-07 (REST interoperability) from the business PDR.
- I wrote this as a standalone subsection, ready to paste into
  `overview.md`'s new "Alternatives Considered" section — it does not
  reopen ADR-001, which already fixed React as the frontend technology; it
  documents why that choice holds up against a real alternative.

**HU-DOCS-18 — Wireframes and per-role flows (in progress):**
- I corrected the target folder for this HU: it originally pointed to
  `03-product/`, but the professor's own `12-ux-ui/README.md` explicitly
  defines `wireframes.md` for this, and `00-sdd-guide.md` groups wireframes
  with `navigation-map.md`, not with `problem-framing.md`.
- The team chose Figma as the wireframing tool. I defined the rule that a
  Figma link alone isn't enough for `wireframes.md` — since a link can
  expire or go private, each screen also needs an exported image embedded
  directly in the file.
- I enriched `navigation-map.md`'s existing "Main user flows" section with
  three new flows (Flow 3/4/5: a typical ADMIN, SALESPERSON, and INVENTORY
  session), since the two flows already there were business-process flows
  (register a sale, authenticate), not role journeys — which is specifically
  what HU-02 asks for.
- Still pending: the actual Figma screens and `wireframes.md` itself, for
  the 5 main screens (login, dashboard, customers, products/stock, sales).

## 3. Blockers and risks

- There's a paste error already in the uploaded `overview.md`: the Frontend
  subsection of "Alternatives Considered" (my HU-DOCS-19 content) shows up
  as a stray table row instead of the real text. This needs a fix before
  the professor reviews it — tracked under Angel's HU-DOCS-22.
- HU-DOCS-18's actual Figma work hasn't started yet; only the supporting
  pieces (folder correction, tool decision, flow enrichment) are done.
- No real market/user-interview data exists for this academic project, so
  HU-DOCS-19's conclusion leans on documented team/course constraints
  (team size, course length) rather than measured usage data — flagged
  here for transparency, not treated as a gap to fix.

## 4. Plan for next week

- Finish the Figma designs for the 5 main screens and publish
  `12-ux-ui/wireframes.md` with an embedded capture per screen.
- Confirm the `overview.md` Frontend subsection is corrected once Angel
  closes HU-DOCS-22.
- Support Sergio on HU-ARQ-07 if the backend configuration needs frontend
  contract input before HU-FE-02 starts.

## 5. Compliance self-check
- [ ] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to that environment — not applicable to `docs` repo (no branches, direct commit to `main` per `documentation-rules.md`)
- [x] Testable acceptance criteria
- [ ] Tests added/updated (unit / integration) — not applicable, documentation-only HUs
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) — not applicable, documentation-only HUs
- [x] No secrets; config via environment variables

## 6. Evidence links
- Governance refinement: [`definition-of-done.md`](./docs/00-governance/definition-of-done.md), [`definition-of-ready.md`](./docs/00-governance/definition-of-ready.md), [`documentation-rules.md`](./docs/00-governance/documentation-rules.md)
- Frontend alternatives: [`overview.md`](./docs/01-context/overview.md), "Alternatives Considered" → Frontend subsection
- Wireframes and flows (in progress): [`navigation-map.md`](./docs/12-ux-ui/navigation-map.md) (Flow 3/4/5 already added), `wireframes.md` (pending)
