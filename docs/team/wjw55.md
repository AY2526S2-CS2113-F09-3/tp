# Wang Jiawei - Project Portfolio Page

## Overview
**Equipment Master** is a desktop CLI (Command Line Interface) application engineered for University Laboratory Technicians. It replaces highly inefficient, paper-based inventory logbooks with a 100% accountable digital registry. The system allows technicians to manage high-volume equipment loans during peak academic weeks, instantly track assets by course modules, and proactively forecast procurement needs based on equipment aging and student enrollment sizes.

## Summary of Contributions

### Code Contributed
* https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=wjw55&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=

### Enhancements Implemented
-   **Foundation Architecture (Initial OOP Setup):**

    -   _What it does:_ Designed and implemented the core Object-Oriented Programming (OOP) class structures and project skeleton at the project's inception.

    -   _Justification:_ Establishing a solid architectural baseline early on was critical for team velocity. It cleanly separated the system components, allowing the rest of the team to immediately work in parallel on different classes and features without architectural bottlenecks or constant merge conflicts.
  
- **Core Inventory Ingestion (`add` command):**
    -   _What it does:_ Allows technicians to register physical equipment into the system. It supports basic additions as well as highly detailed records (including inline module tagging, expected lifespans, purchase semesters, and low-stock thresholds) in a single command.

    -   _Justification:_ This forms the foundational database of the entire application. Without a robust way to ingest and structure the physical stock, downstream forecasting and module mapping features would not function.

    -   _Highlights:_ Built a highly flexible parser capable of safely extracting multiple optional and repeating flags without collision.

-   **Academic Dependency Mapping (`tag` and `untag` commands):**

    -   _What it does:_ Allows technicians to dynamically link (or unlink) specific equipment to academic modules using a configurable `requirementRatio` (e.g., 0.5 means 1 piece of equipment is shared between 2 students).

    -   _Justification:_ This is the core logic that enables the application's most powerful feature: the Procurement Report. By allowing variable requirement ratios, the system can accurately calculate exactly how much equipment is needed based on real-world lab sharing constraints.

    -   _Highlights:_ Engineered defensive "Ghost Reference" checks. During `TagCommand#execute` and `UntagCommand#execute`, the system strictly verifies the existence of _both_ the module and the equipment before allowing a link to be created or destroyed, preventing orphaned data and fatal errors during forecasting calculations.

### Contributions to the User Guide (UG)
-   Authored the **Equipment Inventory Management** section of the User Guide (documenting `add`, `delete`, `setstatus`, and `setmin`).

-   Structured the documentation to prioritize user readability, providing clear formats and practical daily-use examples. I specifically focused on explaining the mathematical impact of the `req/FRACTION` parameter in the `tag` command so technicians understand how to represent shared lab equipment.
### Contributions to the Developer Guide (DG)
-   **Authored Feature Implementation Sections:** Detailed the architecture, execution flow, and design considerations for the **Core Inventory Ingestion** (`AddCommand`) and the **Academic Dependency Mapping System** (`TagCommand` & `UntagCommand`), with a strong focus on defensive programming mechanisms like the Double Ghost Reference Check.

-   **Contributed UML Diagrams:** Authored PlantUML diagrams mapping out my specific architectural contributions, including:

    -   Class and Sequence Diagrams detailing the parsing and execution flow of the Core Inventory Ingestion (AddCommand.png).

    -   Sequence Diagram illustrating the strict two-way validation and execution flow of the Academic Dependency Mapping process (`TagCommand.png`).

    -   Sequence Diagram detailing the safe dereferencing and removal of module dependencies (`UntagCommand.png`).
