# Hongyu Chen - Project Portfolio Page

## Overview
**Equipment Master** is a desktop CLI (Command Line Interface) application engineered for University Laboratory Technicians. It replaces highly inefficient, paper-based inventory logbooks with a 100% accountable digital registry. The system allows technicians to manage high-volume equipment loans during peak academic weeks, instantly track assets by course modules, and proactively forecast procurement needs based on equipment aging and student enrollment sizes.

## Summary of Contributions

### Code Contributed
* https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=hongyu1231&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=

### Enhancements Implemented
I architected and implemented the core analytical and structural features of the application, transforming it from a simple data-entry tool into a dynamic lab management system.

1. **Module Tracking System (`addmod`, `delmod`, `updatemod`, `listmod`)**
    * **What it does:** Allows the technician to register academic courses (Modules) as distinct entities and track their student enrollment size (pax).
    * **Justification & Depth:** Lab demands fluctuate strictly based on module enrollment. Without this baseline, automated forecasting is impossible. I engineered a normalized entity structure with a centralized `ModuleList`, utilizing the **Context Object Pattern** for clean dependency injection. Furthermore, I implemented **Safe Dereferencing** in the `delmod` command to prevent data corruption when a module is deleted, ensuring orphaned equipment records remain intact.

2. **Semantic Aging Equipment Report (`report aging`, `setsem`)**
    * **What it does:** Dynamically calculates the age of all inventory against a baseline semester, flagging items that have exceeded their expected lifespan.
    * **Justification & Depth:** Enables proactive budgeting, as technicians need to justify procurement requests *before* the semester starts. Instead of using standard date libraries (`LocalDate`), I designed a custom `AcademicSemester` class to parse and perform mathematical time-difference calculations on semantic university timelines (e.g., `AY24/25 Sem1`). This aligns the software's architecture perfectly with the user's real-world domain context.

3. **Enhanced Find Feature (`find`)**
    * **What it does:** Upgraded the search algorithm to cross-reference both the equipment's name and its associated relational modules.
    * **Justification & Depth:** Heavily refactored the iteration logic to adhere to the **Single Level of Abstraction Principle (SLAP)**. I successfully eliminated deeply nested loops (the "Arrow Anti-Pattern") by extracting helper methods and utilizing $O(1)$ early returns, significantly improving codebase testability and search performance.

### Contributions to the User Guide (UG)
* **Authored the "Module Tracking System" section:** Detailed the usage formats and provided practical examples for the `addmod`, `updatemod`, `delmod`, and `listmod` commands, establishing the baseline for automated equipment mapping.
* **Authored the "Enhanced Find Feature" section:** Explained how users can perform cross-relational searches by finding equipment via module names.
* **Authored the "Aging Equipment Report" section:** Documented the `report aging` and `setsem` commands, explaining how technicians can simulate future academic semesters to proactively audit inventory lifespans.
* **Authored "Product Scope & User Stories" sections:** Defined the target user profile (e.g., Senior Lab Technicians) and realistic operational scenarios (e.g., the Week 4 rush) to firmly establish the application's domain context and Value Proposition.

### Contributions to the Developer Guide (DG)
* **Authored the "Module Tracking System" Implementation section:** Detailed the architecture of the normalized entity structure (`ModuleList`) and explicitly documented the implementation of the Context Object Pattern used for dependency injection across all module-related command classes.
* **Authored the "Enhanced Find Feature" Implementation section:** Explained the refactoring logic behind the search algorithm, specifically detailing the adherence to the Single Level of Abstraction Principle (SLAP) and the use of $O(1)$ early returns to eliminate nested loops.
* **Authored the "Aging Equipment Report" Implementation section:** Documented the semantic timekeeping approach using the custom `AcademicSemester` class instead of standard date libraries.
* **Contributed UML Diagrams:** Authored multiple PlantUML diagrams specifically for my features, including:
    * Class Diagram for the Module System (`module_class.png`).
    * Sequence Diagrams for `UpdateModCommand` (`updatemod.png`) and `ReportCommand` (`reportAging.png`).
    * Activity Diagram illustrating the early-return algorithm in `FindCommand` (`find_activity.png`).
* **Authored "Design Considerations" for all three features:** Wrote deep-dive analyses on why specific architectural choices were made, such as explicitly justifying a Normalized Entity Structure for modules to avoid data redundancy.

### Contributions to Team-Based Tasks
* **Project Management & Issue Tracking:** Orchestrated the project's issue tracker on GitHub. I actively created, prioritized, and distributed issues among team members to ensure a balanced workload and clear feature ownership.
* **Milestone & Release Management:** Established and enforced WEEKLY milestones to maintain a strict agile development cadence. Handled the end-to-end Release Management process, ensuring stable builds (JAR files) were properly packaged, tested, and tagged for each iteration.
* **General Code Enhancements & Debugging:** Acted as a primary troubleshooter for the codebase. Led general debugging efforts to resolve overarching integration bugs, runtime exceptions, and technical debts that spanned across multiple individual features rather than being confined to my own code.
* **Deliverable Maintenance:** Maintained the project's data storage formats to ensure backward compatibility during major refactoring. Managed the formatting and compilation of the final PDF deliverables for the UG and DG, ensuring proper pagination and functional PlantUML rendering.

### Review / Mentoring Contributions
* **Technical Mentorship:** Frequently assisted teammates in diagnosing and resolving complex technical and IDE-related issues, helping to unblock their development progress and keep the team on schedule.
* **Architecture Guidance:** Discussed and guided the team in adopting the Context Object Pattern to cleanly decouple the Logic and Model components during our mid-project architectural refactoring phase.
* **Code Reviews:** *[Optional: Insert Link to a PR you reviewed here, e.g., Reviewed PR #42 related to storage bug fixes and ensured they met our team's Checkstyle standards.]*

---

## Contributions to the Developer Guide (Extracts)
*The following is a snippet of my contribution to the Developer Guide detailing the Design Considerations for the Module Tracking System:*

> **Alternative 1 (Current Implementation): Normalized Entity Structure**
> * **How it works:** Modules and their capacities are stored in a standalone `ModuleList`. Equipment objects store references to the modules they are used in, keeping the entities strictly separated.
> * **Why it was chosen:** This follows the database normalization principle. If a module's enrollment size changes (via `updatemod`), we only need to update it in one place (`ModuleList`). It avoids traversing and updating the entire `EquipmentList`.
>
> **Alternative 2: Embedding Module Details within Equipment**
> * **How it works:** Each `Equipment` object contains a list of fully instantiated `Module` objects (including name and pax).
> * **Why it was rejected:** This creates massive data redundancy. If `CG2111A` uses 10 different types of equipment, the enrollment number `150` would be saved 10 times. Updating the enrollment would require an $O(N)$ search through all equipment, increasing the risk of inconsistent data state.

## Contributions to the User Guide (Extracts)
*The following is a snippet of my contribution to the User Guide explaining the Aging Equipment Report:*

> ### Aging Equipment Report
> Proactively audit your inventory to find equipment that has exceeded its expected lifespan based on the semantic university timeline.
>
> #### Generating the Aging Report: `report aging`
> Scans the inventory and generates a report of all equipment whose age (calculated from their purchase semester to the current semester) meets or exceeds their defined lifespan.
> * **Format:** `report aging [AY[YYYY]/[YY] Sem[1/2]]`
    >   * *(Note: If the optional semester argument is omitted, it defaults to the system's current semester set via `setsem`.)*
> * **Example:** `report aging`
> * **Example:** `report aging AY2026/27 Sem1` (Simulates an audit for a future semester)