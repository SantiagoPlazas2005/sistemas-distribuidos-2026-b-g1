# Domain Events

> Catalog of relevant business facts, expressed in the past tense. Level of detail:
> essential events per entity (Created/Updated/Deactivated). The MVP uses synchronous
> REST communication without an active event bus. These events are documented as a reference
> and will be activated when (and if) the project adopts the optional asynchronous phase (see ADR-001).
>
> **Future Extension Note:** if the asynchronous phase is enabled, more granular and
> business-specific events may be added (e.g., `StockDepleted`, `SaleRejectedDueToInsufficientStock`)
> without breaking the events already documented here. New rows are simply added to this table.

## Auth

| Event | Triggered by | Data | Consumers | Bounded Context |
|-------|--------------|------|-----------|------------------|
| UserRegistered | Successful registration of a new system user | user_id, email, role, registration_date | None currently (synchronous MVP) | Auth |
| UserUpdated | Role change or update of an existing user's information | user_id, changes | None currently (synchronous MVP) | Auth |
| UserDeactivated | Deactivation of a user (by an ADMIN) | user_id, date | None currently (synchronous MVP) | Auth |

## Customers

| Event | Triggered by | Data | Consumers | Bounded Context |
|-------|--------------|------|-----------|------------------|
| CustomerRegistered | Successful registration of a new customer | customer_id, identity_document, registration_date | None currently (synchronous MVP) | Customers |
| CustomerUpdated | Update of a customer's contact information | customer_id, changes | None currently (synchronous MVP) | Customers |
| CustomerDeactivated | Deactivation of a customer | customer_id, date | None currently (synchronous MVP) | Customers |

## Products

| Event | Triggered by | Data | Consumers | Bounded Context |
|-------|--------------|------|-----------|------------------|
| ProductRegistered | Registration of a new product in the catalog | product_id, name, price, category_id | None currently (synchronous MVP) | Products |
| ProductUpdated | Change in price, stock, or product information | product_id, changes | None currently (synchronous MVP) | Products |
| ProductDeactivated | Deactivation of a product | product_id, date | None currently (synchronous MVP) | Products |
| CategoryRegistered | Registration of a new category | category_id, name | None currently (synchronous MVP) | Products |
| CategoryDeactivated | Deactivation of a category | category_id, date | None currently (synchronous MVP) | Products |

## Sales

| Event | Triggered by | Data | Consumers | Bounded Context |
|-------|--------------|------|-----------|------------------|
| SaleRegistered | Successful confirmation of a sale | sale_id, customer_id, total, date, detail | None currently (synchronous MVP); a natural candidate to be consumed by Products if the asynchronous phase is enabled | Sales |
| SaleCanceled | Cancellation of a previously registered sale | sale_id, date, reason | None currently (synchronous MVP) | Sales |

---

## References

- Context Map → `02-domain/domain-map.md`
- Entities and Business Rules → `02-domain/entities-and-rules.md`
- Infrastructure-Level Event Catalog (when implemented) → `09-microservices/event-catalog.md`
- Asynchronous Event Contracts (if that phase is enabled) → `07-api/contracts/`