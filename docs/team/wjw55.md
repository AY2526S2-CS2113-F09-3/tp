# Wang Jiawei - Project Portfolio Page

## Overview
**Equipment Master** is a desktop CLI application for University Laboratory Technicians, replacing inefficient paper logbooks with a 100% accountable digital registry. It streamlines high-volume equipment loans, tracks assets by course modules, and proactively forecasts procurement needs based on equipment aging and student enrollment sizes.
## Summary of Contributions

### Code Contributed
* https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=wjw55&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=

### Enhancements Implemented

-   **Foundation Architecture (OOP Setup):** Designed the core project skeleton, establishing a clean separation of concerns to eliminate bottlenecks and enable parallel, conflict-free team development.

-   **Core Inventory Ingestion (`add`):**

    -   Built a flexible, collision-free parser for detailed, single-command equipment registration.

    -   **Highlight:** Engineered **Strict Batch Validation** to reject restocks with conflicting metadata, preventing "silent data loss" and protecting Aging Report accuracy.

-   **Academic Dependency Mapping (`tag` / `untag`):**

    -   Developed the core relational logic to dynamically link equipment to modules using requirement ratios for procurement forecasting.

    -   **Highlight:** Implemented defensive **"Double Ghost Reference" checks** to verify entity existence before mapping, entirely preventing orphaned data and fatal crashes.
### Contributions to the User Guide (UG)

-   Authored the **Equipment Inventory Management** section (`add`, `tag`, `untag`), prioritizing readable formats and practical daily-use examples.

-   Detailed the mathematical impact of the `req/FRACTION` parameter so technicians easily understand how to represent shared lab equipment constraints.

-   Added specific FAQ and Error Handling documentation explaining the system's strict batch-tracking rules to clarify why conflicting restocking attempts are rejected.


### Contributions to the Developer Guide (DG)

-   **Authored Feature Implementation Sections:** Detailed the architecture, execution flow, and design considerations for **Core Inventory Ingestion** and **Academic Dependency Mapping**. Strongly emphasized defensive programming mechanisms (Double Ghost Reference Check and Anti-Data Loss Batch Validation).

-   **Contributed UML Diagrams:** Authored PlantUML diagrams mapping my architectural contributions, including:

    -   Class and Sequence diagrams detailing parsing and execution flow (`AddCommand.png`).

    -   Sequence diagram illustrating strict two-way validation (`TagCommand.png`).

    -   Sequence diagram detailing safe dereferencing and dependency removal (`UntagCommand.png`).

### Review / Mentoring Contributions

-   Actively reviewed teammate Pull Requests (PRs) to ensure architectural consistency and proper exception handling.
### Contributions Beyond the Project Team

-   **Inter-team Quality Assurance:** Participated heavily in the peer-testing phase (PE dry-run) for other groups. Conducted rigorous stress tests on competing CLI parsers using boundary values and malformed inputs.
