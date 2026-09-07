# Wireframes

> Low/medium-fidelity designs of the main screens, built in Figma. The
> focus is structure and flow — not the final design system (that lives in
> `design-system.md`, Crimson Circuit palette).
>
> **Important note on color:** the screenshots in this document and in the
> attached PDF use the **pre-Crimson-Circuit** blue/copper palette (the
> same one currently in the real code, `tokens.css`, before HU-FE-02
> replaces it). Ignore color when reviewing this — what matters here is
> what information each screen carries and how navigation flows between
> them.

**Figma (editable, live version):**
https://www.figma.com/design/zjOfbYmvVakzjIAyh7rdPA/SynkroTech-%E2%80%94-MVP-UI-Draft

**Exported PDF (stable version, uploaded in this same folder):**
`12-ux-ui/SynkroTech-MVP-UI-Draft.pdf` — so this file stays reviewable even
if the Figma link's permissions change or it expires.

---

## Reference design system (PDF page 1)

The PDF includes a "Style guide — draft v0.1" page with the earlier
palette (Ink `#1B2430`, Canvas `#F5F7FA`, Primary/Steel `#2F5D8A`,
Accent/Copper `#C97C3D`, Success `#2E8B67`, Error `#C1443C`) and base
components (buttons, stock badges). This page is kept as a historical
reference for what the MVP looked like when these screens were designed —
the current design system is `12-ux-ui/design-system.md`.

---

## Screen: Login (user picker)

**Target route:** `/login` · **Current MVP route:** none (shown
conditionally, no route of its own) · **Access:** public

**Structure:**
- Logo + "Synkro Tech" centered, subtitle "Pick the user you want to work as"
- "Available users" card with a "Refresh" button
- List of available users, each with an avatar (initials), name, and a role badge (Administrator / Salesperson / Inventory)
- Bottom action button, whose label changes with state:
  - No selection: "Select a user to continue" (disabled)
  - With a selection: "Continue as [Name]" (enabled)

**Behavior:**
- Clicking a user row highlights it (border + background) and enables the button
- Login is simulated — there is no password or token yet (see `navigation-map.md`, Flow 2, MVP note)
- Confirming redirects to the selected role's default module

---

## Screen: Dashboard (Summary)

**Target route:** `/dashboard` (shared by role) · **Current MVP route:** `/summary`, ADMIN only · **Target access:** ADMIN, SALESPERSON, INVENTORY (different content per role, see `navigation-map.md`)

**Structure (ADMIN view, the only one implemented today):**
- 3 stat tiles on top: Active customers, Active products, Out of stock
- 2 stat tiles below: Sales registered, Total revenue
- "Recent sales" table: date, customer, lines, total
- "Lowest stock" table: product, category, price, stock (with a colored badge)
- Footnote clarifying the sample figures are illustrative

**Behavior:**
- Read-only view, no actions — it's a snapshot, not a form
- The SALESPERSON and INVENTORY views (own sales / stock alerts) are still pending design in Figma — this PDF screen only covers the ADMIN view

---

## Screen: Customers

**Route:** `/customers` · **Access:** ADMIN, SALESPERSON

**Structure:**
- Header "Customers" + subtitle "Deactivating never deletes — past sales keep resolving"
- "New customer" button (top right)
- Table: Name (+ address as subtext), Tax ID, Email, Phone, Status (Active/Inactive badge), Actions (Edit, Deactivate)

**Behavior:**
- "Deactivate" does not delete the record — consistent with the soft-delete rule (`active` flag) already fixed in `models.md`
- The subtitle communicates that business rule explicitly to the user, not just to the developer
- Identical view for ADMIN and SALESPERSON — the role difference is which other modules appear in the sidebar, not this screen

---

## Screen: Products

**Route:** `/products` · **Access:** ADMIN, INVENTORY

**Structure:**
- 3 stat tiles: Active products, Out of stock, Active categories
- "Catalogue" table: Product, Category (badge), Price, Stock (colored badge), Status, Actions (Edit, Deactivate)
- "Categories" section below: chips for existing categories + "New category" button
- "New product" button top right

**Behavior:**
- Same soft-delete pattern as Customers
- Stock badges use color to reinforce state (green = in stock), but the number is always visible next to the badge — it never depends on color alone

---

## Screen: Stock lookup

**Route:** `/stock` · **Access:** SALESPERSON only

**Structure:**
- 3 stat tiles: Sellable products, In stock, Out of stock
- "Search by name or category..." field
- Read-only table: Product, Category, Price, Stock — **no Actions column**

**Behavior:**
- Unlike Products, this screen has no "Edit" or "Deactivate" — intentional (see `navigation-map.md`: "a salesperson can check what is available to sell without being able to edit the catalogue")
- Confirms in the real design what was already documented in the navigation map

---

## Screen: Sales

**Route:** `/sales` · **Access:** ADMIN, SALESPERSON

**Structure:**
- "New sale" section: customer selector, product lines (product + quantity + calculated subtotal + remove-line button), "+ Add line" button
- Highlighted "Estimated total", with "Clear" and "Register sale" buttons
- "History" section below: date, customer, lines, total, "View" action

**Behavior:**
- The total is a **client-side estimate** while filling the form — matches what's already documented in `navigation-map.md`, Flow 1: the real total and stock validation happen on the backend at submit
- The low-fidelity reference sketch made earlier in this chat (409 error state) still applies here; the Figma PDF doesn't show that state explicitly, but the behavior is already described in `navigation-map.md`

---

## Correlations

- Route map, roles, and flows → `12-ux-ui/navigation-map.md`
- Final color tokens and components (Crimson Circuit) → `12-ux-ui/design-system.md`
- Business rules behind these screens (soft delete, stock validation) → `02-domain/entities-and-rules.md`
- Real implementation → `synkro-tech` repo, `src/features/`
