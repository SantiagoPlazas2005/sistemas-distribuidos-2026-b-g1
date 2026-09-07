### Frontend: React vs. Vue.js

React is the frontend technology already fixed by the team's decision, recorded in ADR-001 and in the Technology Stack table above. This section does not evaluate an open decision — it investigates and confirms, before implementation begins, that React is the right choice against an equally mature alternative: Vue.js.

React has a larger ecosystem that translates into more battle-tested libraries, more tutorials and answers to common problems, and a higher chance that any team member (or whoever picks up the project later) already has experience with the tool.

Vue was also considered a valid alternative for developing the user interface. However, it was not selected because it did not provide significant advantages over React for the objectives defined in the project. Both technologies are capable of building modern web applications and consuming REST services effectively, meaning the distinction is not based on functional capabilities. In this context, priority was given to a technology widely adopted in both professional and academic environments, allowing the team to focus its efforts on implementing business functionality and the distributed architecture aspects that represent the primary goal of the project.

**Conclusion:** React was selected as the frontend technology because it aligns well with the goals and scope of the project. Both React and Vue are capable of meeting the system requirements; however, React provides a solid foundation for building a maintainable and scalable user interface. This decision allows the team to focus on delivering business functionality and validating the distributed architecture rather than investing effort in technology comparisons.
