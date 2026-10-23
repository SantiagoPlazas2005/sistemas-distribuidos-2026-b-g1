# CP7 - Alert policy

Rules: `06-alerts.yml`. SLO: availability of `POST /sagas/register-sale`, 99.5 % over 30 days (`05-slo.md`). Budget: 0.5 %, that is 24 failed requests of 4,800 (216 minutes of full outage).

We alert on how fast the budget is spent, not on CPU or memory. Each rule needs two conditions at the same time: a long window (the budget is really burning) and a short window (it is still burning now, so the alert stops when the problem stops).

## What each alert means

| Alert | Burn rate | Windows | Error ratio that fires it | Severity | Team |
|---|---|---|---|---|---|
| RegisterSaleAvailabilityFastBurn | 14.4 | 1 h and 5 min | 14.4 x 0.5 % = 7.2 % | page | synkro-tech |
| RegisterSaleAvailabilitySlowBurn | 6 | 6 h and 30 min | 6 x 0.5 % = 3 % | ticket | synkro-tech |

Budget spent before each alert fires, in theory (steady traffic over the whole month, 720 hours):

| Alert | Calculation | Budget spent | In requests (budget of 24) | In minutes of full outage (budget of 216) |
|---|---|---|---|---|
| Fast | 14.4 x 1 h / 720 h | 2 % | 0.48 | 4.3 min (7.2 % of one hour) |
| Slow | 6 x 6 h / 720 h | 5 % | 1.2 | 10.8 min (3 % of six hours) |

What this means in our product. The salespeople sell about 8 hours a day, 160 sales a day, so about 20 sales per working hour (`05-slo.md`). With so few requests, the alert needs whole failed requests:

- **Fast:** 7.2 % of 20 requests is 1.44, so 2 failed sales in one working hour fire it. That is 2 of 24, or 8.3 % of the monthly budget, more than the 2 % of the theory.
- **Slow:** 3 % of the 120 requests of six working hours is 3.6, so 4 failed sales fire it. That is 4 of 24, or 16.7 % of the budget.
- With a total outage during working hours, the fast alert fires after about 4 minutes, and the 5-minute window confirms it at once.
- With no traffic (nights, weekends) the ratio has no value and no alert fires. That is correct: nobody is selling.

The honest conclusion is that with 4,800 requests a month a single failure moves the ratio a lot, so these alerts can be noisy. If that happens, a possible improvement is to require a minimum number of requests in the window, or to alert on the count of failed sales.

## Two cause-based alerts we will NOT create

| Alert we will not create | Why |
|---|---|
| CPU of `synkro-workflow` above 80 % | High CPU does not mean that a salesperson fails to sell. A busy service can answer well, and a failing one can be idle. If CPU really hurts users, it shows up as errors or slowness and the SLO alerts fire. |
| Any single 5xx or timeout from `synkro-customers-api`, `synkro-products-api` or `synkro-sales-api` | The workflow retries each call up to 3 times with a 5 s timeout and compensates the saga when a step fails (`cross-cutting.md` §8, ADR-007). One failed call is usually absorbed and the salesperson does not notice it. Paging a person for each one would wake the team for problems that fix themselves. |

## Relation with the error budget policy

The fast alert pages a person because it can spend 2 % to 8 % of the budget in one hour. The slow alert opens a ticket for the next working day. The thresholds of the policy in `05-slo.md` (50 %, 75 %, 100 % of the budget consumed) are reviewed by the team independently of these alerts.
