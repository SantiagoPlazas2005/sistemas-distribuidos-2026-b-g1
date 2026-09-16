<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       04-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 06

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group - synkro-tech
- SPRINT_GOAL: Close the architecture documentation gap in `05-architecture/` (HU-04, done) and unify FR/NFR requirement identifiers across the repository, disconnecting it from the external PDR (HU-05, in progress).
<!-- CONFIG-END -->

## Docs Repository

| Board Name          | URL                                              |
| -------------------- | ------------------------------------------------ |
| synkro-docs Repository | https://github.com/code-corhuila/synkro-docs.git |

## Team Members

| Full Name                          | GitHub User                                                 |
| ----------------------------       | ------------------------------------------                  |
| Angel Gustavo Solano Trujillo      | https://github.com/AsolanoT                                 |
| Sergio Andres Ordoñez Diaz         | https://github.com/SergioAndres17                           |
| Jordan Ramirez Gallego             | https://github.com/JordanRG420                              |

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| HU-DOCS-25 | Rename RF/RNF prefixes to FR/NFR across the repository | doing | Partial, mechanical `NFR-04`→`NFR-004` digit padding in several files (see below) |
| HU-DOCS-26 | Disconnect the repository from the external PDR | todo | — (blocked by HU-DOCS-25) |

## 2. My individual contribution

**Context:** HU-DOCS-25 touches 5 files by its original scope
(`scope.md`, `navigation-map.md`, `models.md`, `non-functional.md`,
`overview.md`), plus 2 more discovered during review
(`service-catalog.md`, and a literal leftover `RNF-04` in `models.md`
itself). A digit-padding pass ran across the repo this week
(`NFR-04` → `NFR-004`), but it only fixed the *format*, not the
*category* — and it didn't touch the actual `RF-` → `FR-` prefix rename
at all in most files.

**What happened this week:**
- A repo-wide pass changed literal `NFR-04` to `NFR-004` wherever that
  exact string appeared (confirmed in ADR-002, `security-threat-model.md`,
  `05-architecture/overview.md`, `data-dictionary.md`'s `active` row, and
  a historical Gherkin scenario in `user-stories.md`).
- This only catches the exact string `NFR-04`. It did **not** touch
  `NFR-03`, `NFR-07`, `NFR-05`, `NFR-02`, or any `RF-0X` — because those
  are different substrings, not the same citation padded.
- Net result: some citations are now consistently formatted but still
  point to the **wrong NFR category** (e.g., traceability content still
  cites `NFR-004`, which is Security, instead of `NFR-009`, which is
  Data Integrity & Traceability).

**What's NOT done — the actual scope of HU-DOCS-25 remains largely open:**

- [ ] `01-context/scope.md`, "MVP Scope (In Scope)" table — still has
      `RF-10`, `RF-01`, `RF-02, RF-03`, `RF-04`, `RF-05, RF-06`, `RF-07`,
      `RF-08, RF-09` unrenamed, and row 8's `(NFR-004)` needs to become
      `(NFR-009)` (category, not digit)
- [ ] `12-ux-ui/navigation-map.md` — **3 occurrences** of `RF-08/RF-09`,
      none renamed (a third one, in "Flow 3 — A typical ADMIN session",
      was found this week and wasn't in the original scope)
- [ ] `01-context/overview.md`, "Technology Stack" table — `NFR-07` and
      `NFR-03` citations unchanged; both are old PDR-numbering citations
      that need to become `NFR-004` and `NFR-006` respectively
- [ ] `00-governance/definition-of-ready.md` — `NFR-05` and `NFR-02`
      unchanged in the checklist, **plus a third occurrence** found this
      week ("use the concrete value from NFR-05" in the Acceptance
      Criteria section)
- [ ] `09-microservices/service-catalog.md` — newly found: cites
      `NFR-07` in the JWT validation note; should be `NFR-004`
- [ ] `06-data/models.md` §2 "Data modeling principles" — newly found:
      still says the literal old prefix `RNF-04` (never even renamed to
      NFR); should go straight to `NFR-009`
- [ ] `05-architecture/overview.md` and `06-data/data-dictionary.md`
      (`active` row) — both padded to `NFR-004` but wrong category;
      need `NFR-009`
- [ ] ADR-002 (SQL section), `06-data/models.md` (`sales_summary` note),
      `02-domain/entities-and-rules.md` ("Note on Reports"), and
      `data-dictionary.md` (`stock` row) — all still have `FR-08`,
      `FR-09`, or `FR-07` in 2-digit form; none were touched by the
      NFR-only padding pass

## 3. Blockers and risks

- The digit-only padding pass created a false sense of progress: several
  files now "look" 3-digit but still cite the wrong requirement. This is
  worse than leaving them 2-digit, because a category error is harder to
  spot on review than an obviously-outdated prefix.
- HU-DOCS-26 cannot start cleanly until `navigation-map.md`'s prefix
  rename is done, since both HUs touch the same lines (the prefix
  rename here, the "in the PDR" phrase removal there).
- 14 distinct edits remain across 8 files. None are individually hard,
  but doing them one at a time risks missing one, as already happened
  twice this week (the third `navigation-map.md` occurrence, and the two
  newly-found files outside the original scope).

## 4. Plan for next week

- Apply all pending edits in one pass, file by file, using the
  consolidated checklist (`hu-docs-24-25-final-checklist.md`) instead of
  ad-hoc find-replace, to avoid a third round of partial fixes.
- Explicitly separate "rename `RF-` → `FR-`" from "fix which NFR a
  citation points to" as two different checks per file, since this
  week's partial pass conflated them.
- Re-run a full repo search for `RF-0`, `RNF-0`, and `NFR-0` after
  applying the fixes, to confirm zero 2-digit leftovers and zero
  wrong-category citations remain.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to that environment — N/A: direct commit to `main` for `docs` per `documentation-rules.md` (no branches in this repo)
- [ ] Testable acceptance criteria — pending: HU-DOCS-25's Scenario 1 ("no file still uses the RF prefix") is not yet met
- [x] Tests added/updated (unit / integration) — N/A, documentation-only HU
- [x] DDD / hexagonal boundaries respected (domain has no I/O) — N/A, no code touched this week
- [x] No secrets; config via environment variables

## 6. Evidence links
- Partial digit-padding evidence: `security-threat-model.md`, `ADR-002-sale-authorship-traceability.md` (both already consistent at `NFR-004` for their own internal citations, confirmed correct in those two specific files)
- Pending work: see `hu-docs-24-25-final-checklist.md` for the complete 14-item list across 8 files
