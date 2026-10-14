## Summary

Populate `08-uml/` with real SynkroTech diagrams: 8 BPMN process
diagrams and 2 C4 diagrams, all in draw.io for visual quality. Fill
`diagram-index.md`'s registry accordingly.

## Changes

- **8 BPMN diagrams** (`08-uml/diagrams/source/bpmn-*.drawio`): auth
  login, customers register/update/deactivate, products register/
  update/deactivate, sales registration
- **Sale registration diagram rebuilt**: the earlier version showed
  `sales-service` calling `customers-service`/`products-service`
  directly (pre-ADR-003 design). Now shows `synkro-workflow` as
  orchestrator, matching ADR-003 §4's sequence exactly — including the
  reordered steps (stock reserved before the sale is registered)
- **2 C4 diagrams, new** (`08-uml/diagrams/source/c4-01-context.drawio`,
  `c4-02-containers.drawio`): same content as the Mermaid versions in
  `05-architecture/overview.md`, converted to draw.io for this folder
- **`diagram-index.md`** fully filled: registry rows for all 10
  diagrams, tool decision documented, and an explicit note that the C4
  diagrams now live in two places (Mermaid in `overview.md`, draw.io
  here) and must be kept in sync — a deliberate exception to the
  single-source rule the rest of the index follows

## What's NOT included yet

The 10 `.svg` exports for `08-uml/diagrams/exports/` are not part of
this PR — draw.io's SVG export is a manual, per-file UI action (File >
Export as > SVG) that couldn't be automated. Until those exist, the
diagrams are only viewable by opening each `.drawio` file in the draw.io
editor, not natively on GitHub. See the tracking note in the closing
comment.

## Definition of Done

- [x] All 8 BPMN diagrams committed as draw.io sources
- [x] Sale registration diagram reflects ADR-003 (synkro-workflow orchestrator)
- [x] C4 diagrams added to 08-uml/, with sync responsibility documented
- [x] diagram-index.md registry fully filled
- [ ] SVG exports present in `08-uml/diagrams/exports/`, so all diagrams render natively on GitHub
- [ ] Reviewed and approved by at least one other team member

Part of HU-DOCS-30.