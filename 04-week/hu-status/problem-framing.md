# Problem Framing — Problem Definition

> **Why this document exists:** Before designing solutions, the team must be
> aligned on the problem it solves. This document captures that alignment.
> A well-defined problem is already halfway to a solution.

---

## 1. The problem in one sentence

**SynkroTech SAS's sales and inventory staff** who **run the company's day-to-day commercial operations** struggle with **disconnected, manual sales and inventory tracking** because **stock levels, customer records, and sales transactions live in separate spreadsheets, physical notebooks, and isolated tools with no shared source of truth**, resulting in **stock checks that require physical inspection or outdated spreadsheets, sales recorded with no link to inventory, and no consolidated way to know how much was sold in a period or review a customer's purchase history** (see `01-context/overview.md`, "What problem does it solve").

---

## 2. Affected users

| Segment | Description | Estimated size | Priority |
|---------|-------------|-----------------|----------|
| ADMIN | Business administrator — full access: users, customers, products, sales, and reports | N/A — academic project, no real headcount | High |
| SALESPERSON | Sales staff — registers sales, checks stock, manages customers | N/A — academic project, no real headcount | High |
| INVENTORY | Inventory staff — manages products, categories, and stock | N/A — academic project, no real headcount | High |

*(Roles and their needs are already fixed in `01-context/overview.md` → "Main Users" and `01-context/glossary.md`; not redefined here.)*

### Jobs-to-be-done (JTBD)

**When** a customer wants to buy one or more products,
**I want** to check real stock, register the sale, and have inventory update automatically,
**so that** I never oversell an out-of-stock item and always know exactly what was sold.

---

## 3. Evidence of the problem

> **Academic-project note:** SynkroTech SAS is a fictional company built for this course. There are no real user interviews, support tickets, or field observations to cite. The evidence below is limited to the professor's original business brief and the team's own documented problem framing — nothing here is a fabricated data point.

| Evidence type | Source | Date | Key finding |
|----------------|--------|------|--------------|
| Business brief (course PDR) | `pdr/01_PDR_negocio_v1.md` | Week 1 of the course | Establishes the manual/disconnected sales and inventory process as the founding business problem |
| Team's own domain framing | `01-context/overview.md`, "What Problem Does It Solve" | Weeks 1–3 | Documents the "before" process: stock checked by physical inspection or outdated spreadsheets; sales recorded in notebooks disconnected from inventory; no consolidated sales reporting |

---

## 4. Current user solution (and its problems)

| Current solution | Limitations | Cost/Friction |
|--------------------|----------------|-----------------|
| Manual stock checks (physical inspection or outdated spreadsheets) | Doesn't reflect changes in real time; requires walking the floor or trusting a stale file | Delays every sale confirmation |
| Sales recorded in notebooks or isolated files | Not connected to inventory; no traceability | Stock discrepancies; no reliable customer purchase history |
| No consolidated reporting | Can't determine sales for a period, or which products sell best, without manual tallying | Hours of manual aggregation, error-prone |

---

## 5. Solution hypothesis

**We believe that** a centralized sales management system with real-time stock validation and role-based access for SynkroTech SAS's internal staff (ADMIN, SALESPERSON, INVENTORY)
**for** SynkroTech SAS's sales and inventory operations,
**will achieve** accurate, traceable sales and inventory records with no manual reconciliation.
**We will know we succeeded when** the average time to register a sale drops measurably compared to the manual process, and recorded stock stays consistent with actual sales without manual correction.

---

## 6. Success metrics (North Star)

| Metric | Current baseline | 6-month target | How to measure it |
|--------|--------------------|-------------------|------------------------|
| Average time to register a sale | Unmeasured — the manual process was never timed (no real system to instrument) | To be defined once the MVP is instrumented and the team times a simulated manual process for comparison | Timestamp from "start new sale" to "sale confirmed" inside the system |
| Stock discrepancy incidents (secondary) | Unmeasured — no current tracking mechanism exists | 0 for sales made through the system | Compare system-recorded stock against a physical count |

**North Star Metric:** Average time to register a sale.

---

## 7. Hypothesis risks

| Risk | Probability | Impact | Experiment to validate |
|------|--------------|--------|---------------------------|
| The improvement can't be measured against a real historical baseline (fictional company, no real "before" system running) | High | Medium | Have the team time a simulated manual process once, to create a reference baseline for comparison |
| Reviewers may not find the role-based flows realistic enough to demonstrate business understanding | Medium | High | Walk the MVP demo through the exact "With the system" narrative in `01-context/overview.md`, scenario by scenario, before the sprint review |

---

## 8. Out of scope (we do not solve)

- **Customer self-service portal** — the system is for internal SynkroTech SAS staff, not end customers (see `01-context/scope.md`).
- **Electronic invoicing for tax authorities** — outside the academic and business scope of the MVP (see `01-context/scope.md`).

---

## Correlations

- Product vision → `03-product/vision.md`
- HUs that implement this solution → `04-requirements/user-stories.md`
- Detailed KPIs → `13-operations/README.md`
