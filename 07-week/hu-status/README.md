<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       07-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 07

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group - synkro-tech
- SPRINT_GOAL: Unify FR/NFR identifiers in user-stories.md, correct documentation-rules.md to match the actual docs repo workflow, and populate 08-uml/ with real SynkroTech diagrams in draw.io.
<!-- CONFIG-END -->

## Docs Repository

| Board Name             | URL                                              |
|------------------------|--------------------------------------------------|
| synkro-docs Repository | https://github.com/code-corhuila/synkro-docs.git |

## Team Members

| Full Name                          | GitHub User                               |
|------------------------------------|-------------------------------------------|
| Angel Gustavo Solano Trujillo      | https://github.com/AsolanoT               |
| Sergio Andres Ordoñez Diaz         | https://github.com/SergioAndres17         |
| Jordan Ramirez Gallego             | https://github.com/JordanRG420            |

## 1. User stories worked this week

| HU ID      | Title                                             | Status | Evidence (PR or commit URL)                                                              |
|------------|---------------------------------------------------|--------|------------------------------------------------------------------------------------------|
| HU-DOCS-25 | Unify FR/NFR identifiers and disconnect PDR       | done   | [`user-stories.md`](https://github.com/code-corhuila/synkro-docs/commit/596435f5f2b840938faf65804634808fa5fe281e#diff-e4b6f51bda0c87fc7722c721041d0af407cb68911e0b258580212630500f3ced) |
| HU-DOCS-28 | Align git-conventions.md with branching-policy.md | done   | `00-governance/documentation-rules.md`                                                   |
| HU-DOCS-30 | Add BPMN and C4 diagrams as draw.io sources       | done   | `08-uml/diagram-index.md`, `08-uml/diagrams/source/`                                    |

## 2. My individual contribution

**HU-DOCS-25 — Unify FR/NFR identifiers and disconnect PDR:**
- Updated `00-governance/user-stories.md` to replace all `RF-`/`RNF-`
  prefixes with the unified `FR-`/`NFR-` three-digit format and remove
  every remaining reference to the external PDR document, ensuring all
  HU-to-requirement traceability links point to files that exist in the
  repository.

**HU-DOCS-28 — documentation-rules.md correction:**
- Fixed `00-governance/documentation-rules.md`'s "Update process" section,
  which still described working from a fork and said "no PR is possible"
  for the `docs` repository — both wrong under the current policy. Updated
  it to describe the actual `docs/*` branch + PR + `ariel5253` approval
  flow. Delivered as its own commit (separate from Sergio's
  `git-conventions.md` fix) to preserve traceability of the additional
  finding discovered during review.

**HU-DOCS-30 — UML diagrams:**
- Populated `08-uml/diagrams/source/` with 10 draw.io source files: 8
  BPMN process diagrams (auth login; customers register, update,
  deactivate; products register, update, deactivate; sales registration)
  and 2 C4 diagrams (System Context, Container).
- The sale registration diagram (BPMN-08) was rebuilt to reflect
  `synkro-workflow` as the orchestrator (ADR-003 §4), replacing the
  pre-ADR-003 version that showed `sales-service` calling
  `customers-service`/`products-service` directly. The flow now includes
  the reordered steps (stock reserved before the sale is registered)
  and the compensation path (release stock on failure).
- The C4 diagrams in `08-uml/` are draw.io versions of the same content
  that already lives as Mermaid in `05-architecture/overview.md` — added
  for visual quality and team readability. Both locations must be updated
  together; this is documented explicitly in `diagram-index.md`.
- Filled `08-uml/diagram-index.md`: completed the registry with all 10
  diagrams, their source paths, and the tool decision (draw.io for BPMN
  and C4; ER diagrams stay as Mermaid inside `06-data/models.md` since
  moving them would separate the diagram from the SQL it describes).
- Note: SVG exports for `08-uml/diagrams/exports/` are a manual step
  (File → Export as → SVG per diagram in draw.io) pending completion by
  the team — documented in the PR and the closing comment.

## 3. Blockers and risks

- The 10 SVG exports for `08-uml/diagrams/exports/` are not yet committed.
  Until they exist, the diagrams are only viewable by opening each
  `.drawio` file in the draw.io editor — they do not render natively on
  GitHub. This is the one open item for HU-DOCS-30's DoD.
- The C4 diagrams now live in two places (Mermaid in `overview.md`,
  draw.io in `08-uml/`). Any future architecture change must update both
  in the same PR — a coordination risk flagged in `diagram-index.md`.

## 4. Plan for next week

- Complete the SVG export step for all 10 diagrams and commit them to
  `08-uml/diagrams/exports/` to fully close HU-DOCS-30's DoD.
- Follow up on professor's feedback on the merged PRs and address any
  requested corrections.
- Coordinate with the team on the coding sprint kickoff strategy.

## 5. Compliance self-check

- [x] Conventional Commits - `type(scope): summary`
- [x] Per-environment HU branch + PR to `main` — branches `docs/unify-fr-nfr-and-disconnect-pdr`, `docs/align-git-conventions`, and `docs/add-uml-diagrams`, all merged via PR approved by `ariel5253`
- [x] Testable acceptance criteria
- [x] Tests added/updated — N/A, documentation-only HU
- [x] DDD / hexagonal boundaries respected — N/A, no code touched this week
- [x] No secrets; config via environment variables — N/A

## 6. Evidence links

- User stories (HU-DOCS-25): [`user-stories.md`](https://github.com/code-corhuila/synkro-docs/blob/main/00-governance/user-stories.md)
- Documentation rules (HU-DOCS-28): [`documentation-rules.md`](https://github.com/code-corhuila/synkro-docs/blob/main/00-governance/documentation-rules.md)
- Diagram index (HU-DOCS-30): [`diagram-index.md`](https://github.com/code-corhuila/synkro-docs/blob/main/08-uml/diagram-index.md)
- UML source diagrams (HU-DOCS-30): [`08-uml/diagrams/source/`](https://github.com/code-corhuila/synkro-docs/tree/main/08-uml/diagrams/source)
