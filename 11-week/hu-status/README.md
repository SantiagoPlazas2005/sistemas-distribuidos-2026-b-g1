<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       11-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 11

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Fredman Santiago Plazas Artunduaga
- GITHUB_USER: SantiagoPlazas2005
- TEAM: Group - synkro-tech
- SPRINT_GOAL: Make the register-a-sale journey of SynkroTech observable end to end and operable: trace map, log contract, instrumentation code with a test, metric catalog (delivery 1), then SLOs with error budget, burn-rate alerts, a runbook and an incident drill with a blameless postmortem (delivery 2).
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| W11-CP1 | Critical journey and trace map (register a sale) | done | [505ef22](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/505ef22) |
| W11-CP2 | Structured log contract | done | [753acb9](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/753acb9) |
| W11-CP3 | Instrumentation code and test in synkro-workflow | done | [bf3c354](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/bf3c354), [8bacfff](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/8bacfff) |
| W11-CP4 | Golden signals and metric catalog | done | [b1bbf38](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/b1bbf38) |
| W11-CP6 | SLIs, SLOs and error budget | done | [b5d0a7a](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/b5d0a7a), [0b6691f](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/0b6691f) |
| W11-CP7 | Burn-rate alerts | done | [](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/) |
| W11-CP8 | Runbook for the fast-burn alert | done | [eec41c9](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/eec41c9) |
| W11-CP9 | Incident drill and blameless postmortem | done | [](https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/commit/) |

## 2. My individual contribution
- **Delivery 1 (CP0 to CP4):** journey and trace map, log contract, a minimal instrumented `synkro-workflow` module with a passing test, and the metric catalog (details in the previous status).
- **CP6:** defined three SLOs for `POST /sagas/register-sale` (availability 99.5 %, latency 95 % under 0.25 s, saga integrity 99.9 %), a traffic estimate with its assumptions, the error budget in requests and minutes and the budget policy at 50, 75 and 100 % (`05-slo.md`). A second commit added the PromQL queries that were missing from the first push.
- **CP7:** two multi-window burn-rate alerts (14.4 over 1 h confirmed over 5 min, and 6 over 6 h confirmed over 30 min) in `06-alerts.yml`, validated with `promtool`, and `06-alert-policy.md` with the budget each alert means and two cause-based alerts not created.
- **CP8:** a runbook for the fast-burn alert (`07-runbook.md`) that lists only the mitigations our architecture supports.
- **CP9:** an incident drill (a participant answers in 3 s for 25 % of the requests during 40 minutes) with the trace, four log lines, the SLI values, the timeline and three action items (`08-incident.md`).

## 3. Blockers and risks
- The drill showed that no current alert detects slowness without errors: the two alerts count only `outcome="error"`, and a burn rate of 5 is below the 6 of the slow alert.
- The documents describe no rollback procedure, and there is no feature flag; the MVP has one instance of each service, so scaling out is not an option.
- Traffic and SLO targets are assumptions: there is no Production environment and no real traffic estimate (`NFR-001`, `NFR-002`).
- With about 20 sales per working hour, two failed sales in one hour fire the fast alert, so it can be noisy.
- The CP3 module is a minimal slice and ADR-007 does not say whether the correlation id is stored in `saga_instance`.
- Some commits made from the GitHub web editor kept the default message (`Create ...`).

## 4. Plan for next week
- Add the latency alert, the slow-call log line and the documented rollback (the three action items of `08-incident.md`).
- Decide with the team the request timeout of the saga and whether `trace_id` is saved with the saga state.
- Bring the same trace context and log format to `synkro-products-api` and `synkro-sales-api`.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` (except the web-editor commits noted in section 3)
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) - not applicable: workshop work was pushed to `main` of my fork
- [ ] Testable acceptance criteria - the action items of `08-incident.md` have them; the workshop checkpoints are not user stories
- [x] Tests added/updated (unit / integration) - `TracePropagationTest`; the alert rules were checked with `promtool` unit tests
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) - not evaluated: the module is an instrumentation slice without domain code
- [x] No secrets; config via environment variables - the downstream URL is the property `customers.base-url`

## 6. Evidence links
- Workshop folder: https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/tree/main/11-week/workshop
- SLOs: https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/blob/main/11-week/workshop/05-slo.md
- Alerts: https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/blob/main/11-week/workshop/06-alerts.yml
- Runbook: https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/blob/main/11-week/workshop/07-runbook.md
- Incident and postmortem: https://github.com/SantiagoPlazas2005/sistemas-distribuidos-2026-b-g1/blob/main/11-week/workshop/08-incident.md
