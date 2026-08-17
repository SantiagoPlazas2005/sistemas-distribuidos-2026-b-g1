Weekly status - Week 01
<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
FULL_NAME: Fredman Santiago Plazas Artunduaga
GITHUB_USER: SantiagoPlazas2005
TEAM: Group - PRJ-GESTION-VENTAS-V1
SPRINT_GOAL: Convert the hardware store (ferretería) product brief into a preliminary design review: risk assessment, work plan, acceptance criteria, and next steps for a financial control system.
<!-- CONFIG-END -->
## Team Members

| Full Name                          | GitHub User                                                 |
| ----------------------------       | ------------------------------------------                  |
| Sergio Andres Ordoñez Diaz         | https://github.com/SergioAndres17                           |
| Fredman Santiago Plazas Artunduaga | https://github.com/SantiagoPlazas2005                       |
| Jordan Ramirez Gallego             | https://github.com/JordanRG420                              |
| Angel Gustavo Solano Trujillo      |  https://github.com/AsolanoT                                |

1. User stories worked on this week
HU ID	Title	Status (todo/doing/done)	Evidence (PR or commit URL)
HU-PDR-002	Define risks, work plan, acceptance criteria, and next steps for the financial control PDR	done	[commit or PR URL]
2. My individual contribution
I identified the project risks (open business questions left unresolved, low team experience with Go, ambiguous income/expense categories, calculation errors in the net balance, misaligned reports, and scope creep toward a full ERP/POS) and proposed a mitigation for each.
I defined a preliminary, phased work plan: closing open business questions first, then building the Categories, Movements, and Reports services in order of increasing complexity, followed by the Angular frontend and end-to-end testing.
I wrote the PDR acceptance criteria: the scope must stay limited to aggregated financial control, the initial income/expense categories must be validated, the data model must support net balance by period, and the highest-impact open questions must be resolved before Phase 2.
I outlined the next steps: validating the PDR with the instructor, resolving the priority open questions with the business, setting up the hexagonal folder template in Go, and configuring repositories and the Docker/MySQL environment.
3. Blockers and risks
Key open business questions (whether movements can be edited/annulled, and whether the balance should show only per period or also accumulated) are still unresolved and directly affect the data model I used as the basis for the risks and work plan.
The architecture and service names (Categories, Movements, Reports) used in my sections are a proposal based on the course pattern; they still need to be confirmed against what the team defined in points 5-7 of the PDR.
4. Plan for next week
Follow up with the instructor/client to close the pending open questions before Phase 2 of the work plan starts.
Align the risks and work plan with the final architecture once points 5-7 are confirmed by the team.
Support the setup of the hexagonal folder template and repository configuration described in the next steps.
5. Compliance self-check
 Conventional Commits - type(scope): summary
 HU branch by environment + PR to that environment (hu-xxx-dev -> develop, ...)
 Verifiable acceptance criteria
 Tests added/updated (unit / integration)
 DDD / hexagonal boundaries respected (the domain has no I/O)
 No secrets; configuration via environment variables
6. Evidence links
Contribution to Risks, Work Plan, Acceptance Criteria and Next Steps: pdr.md
