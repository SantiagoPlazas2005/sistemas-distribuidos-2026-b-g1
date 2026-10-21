# CP4 - Golden signals and metric catalog

Journey: register a sale (`POST /api/v1/sagas/register-sale`).

## Conventions

- Names in snake_case, prefixed with the service, unit suffix (`_seconds`) and `_total` for counters.
- `route` is always the route template (`/customers/{id}`), never the raw URL. A path that matches no route is recorded as `unmatched`.
- `outcome` has a closed set of values: `success` (2xx and 3xx), `client_error` (4xx, includes business rejections like 422), `error` (5xx or an exception) and `rate_limited` (gateway only, 429).
- Only `error` counts against availability. A 422 is an answer, not a failure (`07-api/guidelines.md`).
- The `workflow_*` metrics of `/sagas/register-sale` already exist in the CP3 module (`03-instrumentation/synkro-workflow`). Names marked "proposed" do not exist yet in any code.
- Route counts below were counted from the OpenAPI contracts in `07-api/contracts/openapi/`.

## synkro-workflow (Java, Spring Boot)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | workflow_request_duration_seconds | histogram | route, method | `histogram_quantile(0.95, sum by (le) (rate(workflow_request_duration_seconds_bucket{route="/sagas/register-sale"}[5m])))` |
| Traffic | workflow_requests_total | counter | route, method, outcome | `sum(rate(workflow_requests_total{route="/sagas/register-sale"}[5m]))` |
| Errors | workflow_requests_total | counter | route, method, outcome | `sum(rate(workflow_requests_total{outcome="error"}[5m])) / sum(rate(workflow_requests_total[5m]))` |
| Saturation | hikaricp_connections_active, hikaricp_connections_max (proposed for this service) | gauge | pool | `hikaricp_connections_active / hikaricp_connections_max` |

The CP3 module has no database, so it has no pool metrics yet. In the real service the pool is HikariCP with a maximum of 10 connections (`spring.datasource.hikari.maximum-pool-size`, `cross-cutting.md` §8), so `hikaricp_connections_max` is 10 and the saturation query is a fraction of that.

Extra metrics of the saga (proposed):

| Metric | Type | Labels | Use |
|---|---|---|---|
| workflow_saga_finished_total | counter | status (COMPLETED, COMPENSATED, FAILED) | Sales that ended well, compensated or flagged for a person |
| workflow_step_duration_seconds | histogram | step (validate-customer, reserve-stock, register-sale) | Which step is slow |
| workflow_downstream_calls_total | counter | target (customers, products, sales), outcome | Calls to participants, including timeouts and retries |

## synkro-customers-api (Java, Spring Boot)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | customers_request_duration_seconds | histogram | route, method | `histogram_quantile(0.95, sum by (le) (rate(customers_request_duration_seconds_bucket{route="/customers/{id}"}[5m])))` |
| Traffic | customers_requests_total | counter | route, method, outcome | `sum(rate(customers_requests_total[5m]))` |
| Errors | customers_requests_total | counter | route, method, outcome | `sum(rate(customers_requests_total{outcome="error"}[5m])) / sum(rate(customers_requests_total[5m]))` |
| Saturation | hikaricp_connections_active, hikaricp_connections_max | gauge | pool | `hikaricp_connections_active / hikaricp_connections_max` |

## synkro-products-api (Go)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | products_request_duration_seconds | histogram | route, method | `histogram_quantile(0.95, sum by (le) (rate(products_request_duration_seconds_bucket{route="/stock-reservations"}[5m])))` |
| Traffic | products_requests_total | counter | route, method, outcome | `sum(rate(products_requests_total[5m]))` |
| Errors | products_requests_total | counter | route, method, outcome | `sum(rate(products_requests_total{outcome="error"}[5m])) / sum(rate(products_requests_total[5m]))` |
| Saturation | products_db_pool_connections_in_use (proposed) | gauge | none | `products_db_pool_connections_in_use / 10` |

The pool size is 10 (`db.SetMaxOpenConns`, `cross-cutting.md` §8). In Go the value would come from `db.Stats().InUse`.

## synkro-sales-api (Go, MongoDB)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | sales_request_duration_seconds | histogram | route, method | `histogram_quantile(0.95, sum by (le) (rate(sales_request_duration_seconds_bucket{route="/sales"}[5m])))` |
| Traffic | sales_requests_total | counter | route, method, outcome | `sum(rate(sales_requests_total[5m]))` |
| Errors | sales_requests_total | counter | route, method, outcome | `sum(rate(sales_requests_total{outcome="error"}[5m])) / sum(rate(sales_requests_total[5m]))` |
| Saturation | sales_mongo_pool_connections_in_use (proposed) | gauge | none | `sales_mongo_pool_connections_in_use / <MONGODB_MAX_POOL_SIZE>` |

## synkro-api-gateway (NGINX)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | gateway_request_duration_seconds (proposed) | histogram | route | `histogram_quantile(0.95, sum by (le) (rate(gateway_request_duration_seconds_bucket{route="/api/v1/sagas"}[5m])))` |
| Traffic | gateway_requests_total (proposed) | counter | route, outcome | `sum(rate(gateway_requests_total[5m]))` |
| Errors | gateway_requests_total (proposed) | counter | route, outcome | `sum(rate(gateway_requests_total{outcome="error"}[5m])) / sum(rate(gateway_requests_total[5m]))` |
| Saturation | gateway_requests_total (proposed) | counter | route, outcome | `sum(rate(gateway_requests_total{outcome="rate_limited"}[5m]))` |

At the gateway, saturation is read as requests rejected by rate limiting (429).

## Label cardinality

Real routes and operations per contract (each contract also has `GET /health`):

| Service | Routes (paths) | Operations (route + method) | Counter series, worst case (operations x 3 outcomes) | Histogram series (operations x about 12) |
|---|---|---|---|---|
| synkro-workflow | 3 | 3 | 9 | about 36 |
| synkro-customers-api | 3 | 6 | 18 | about 72 |
| synkro-products-api | 11 | 17 | 51 | about 204 |
| synkro-sales-api | 6 | 7 | 21 | about 84 |

Each histogram operation has 10 buckets (9 limits and `+Inf`) plus `_sum` and `_count`. Unknown paths add a few more series under `route="unmatched"`. These numbers are small, so the catalog is safe as it is. The labels that could break this are listed next.

| Label | Possible values |
|---|---|
| route | The templates of the contracts, plus `unmatched` |
| method | GET, POST, PUT, DELETE |
| outcome | success, client_error, error (and rate_limited at the gateway) |
| step, target, status | 3 values each |

## Forbidden labels

| Label | Why | What replaces it |
|---|---|---|
| customer_id, product_id, sale_id, saga_id, reservation_id | One value per record: unbounded series | The same IDs go in the logs (`02-log-contract.md`), searched by `trace_id` |
| Raw URL path (`/customers/2b7d...`) | One value per resource | The route template `/customers/{id}`; unknown paths become `unmatched` |
| email, identityDocument, phone | Unbounded and personal data | Nothing; never in metrics |
| trace_id, correlation_id | One value per request | Logs and traces, not metrics |
| error message text | Unbounded | `outcome` and, in logs, `error_code` |

## Latency histogram buckets

`0.025, 0.05, 0.1, 0.25, 0.5, 1, 2.5, 5, 10` seconds (already configured in the CP3 module).

What the documents fix:
- Every outgoing call of the workflow has a 5 s timeout and at most 3 attempts (`cross-cutting.md` §8). A bucket exactly at 5 shows how many calls hit the limit.
- A saga that needs one retry after a timeout takes at least 5 s plus the retry, so the 10 s bucket catches it. Anything above 10 s goes to `+Inf`.

What is still an assumption of the team, to validate by measuring in QA:
- A salesperson registers the sale while the customer waits at the counter, so we assume that under 0.5 s feels instant, under 1 s is comfortable and 2.5 s is the limit.
- The small buckets (0.025 to 0.25) separate fast reads, such as `GET /customers/{id}`, from the transactional writes, such as `POST /stock-reservations`.

Open point: the documents do not give the request timeout of `POST /sagas/register-sale` (ADR-007 says only that the `POST` answers `RUNNING` when the steps do not finish in time). The 10 s upper bucket should be reviewed once that value is decided.

## How to read the two main queries

Error ratio, `sum(rate(workflow_requests_total{outcome="error"}[5m])) / sum(rate(workflow_requests_total[5m]))`:
- `rate(...[5m])` turns a counter, which only grows, into requests per second averaged over the last 5 minutes.
- `sum(...)` adds all the series (every route and method) into one number.
- Errors per second divided by all requests per second gives the fraction of failed requests. With no traffic it has no value (division by zero).
- In the CP3 run (`HOW-TO-RUN.md`) there were 2 requests, 1 `client_error` and 1 `error`, so the ratio of errors was 1 / 2 = 0.5.

Latency p95, `histogram_quantile(0.95, sum by (le) (rate(workflow_request_duration_seconds_bucket{route="/sagas/register-sale"}[5m])))`:
- Each `_bucket` series counts the requests that took up to its limit `le`. `rate` gives the per-second increase of each bucket.
- `sum by (le)` merges methods and instances but keeps one value per bucket.
- `histogram_quantile(0.95, ...)` estimates the time under which 95 % of requests finish, interpolating inside the bucket where that 95 % falls.
- In the CP3 run, 1 request finished under 0.1 s and 2 under 0.25 s. The 95 % point falls in the 0.1 to 0.25 bucket and is estimated at 0.235 s, although the slowest request was 0.124 s. Coarse buckets give an approximate answer.
