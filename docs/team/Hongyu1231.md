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
* **Authored the "Introduction" and "Quick Start" sections:** Established the application's core value proposition for University Laboratory Technicians and provided clear, step-by-step onboarding instructions to ensure a frictionless setup process for new users.
* **Authored the "Module Tracking System" section:** Detailed the usage formats, parameter constraints, and provided practical examples for the `addmod`, `updatemod`, `delmod`, and `listmod` commands, guiding users on how to establish a baseline for automated equipment mapping.
* **Authored the "Enhanced Find Feature" section:** Documented the upgraded search capabilities, explaining how users can perform cross-relational searches to locate equipment via their associated module codes (e.g., finding all items needed for `CG2111A`).
* **Authored the "Aging Equipment Report" section:** Documented the `report aging` and `setsem` commands, providing clear examples of how technicians can set semantic academic timelines to simulate future semesters and proactively audit inventory lifespans.

### Contributions to the Developer Guide (DG)
* **Authored Feature Implementation Sections:** Detailed the architecture, execution flow, and design considerations (e.g., justifying the Normalized Entity Structure) for the **Module Tracking System**, **Enhanced Find Feature**, and **Aging Equipment Report**.
* **Contributed UML Diagrams:** Authored multiple PlantUML diagrams specifically for my features, including:
    * Class Diagram for the Module System (`module_class.png`).
    * Sequence Diagrams for `UpdateModCommand` (`updatemod.png`) and `ReportCommand` (`reportAging.png`).
    * Activity Diagram illustrating the early-return algorithm in `FindCommand` (`find_activity.png`).
* **Authored "Product Scope & User Stories" sections:** Defined the **Target User Profile** (e.g., Senior Lab Technicians) and drafted comprehensive **User Stories** to firmly establish the application's domain context, Value Proposition, and expected operational scenarios.
* **Defined "Non-Functional Requirements" (NFRs):** Established the system's strict constraints regarding performance (response times), environment (Java 17 CLI), and data integrity to guide architectural decisions.
* **Authored the "Glossary":** Standardized domain-specific terminology (e.g., *Pax*, *Academic Semester*, *Safe Dereferencing*) to ensure clear communication among current and future developers.
* **Authored "Instructions for Manual Testing":** Designed and documented clear, step-by-step manual testing procedures for the Module Tracking, Enhanced Find, and Aging Report features, ensuring peer testers and evaluators can accurately verify system behaviors and edge cases.
* **Authored "Acknowledgements":** Properly attributed the foundational AB3 architecture and necessary third-party libraries, adhering to strict academic and software engineering integrity standards.

### Contributions to Team-Based Tasks
* **Project Management & Issue Tracking:** Orchestrated the project's issue tracker on GitHub. I actively created, prioritized, and distributed issues among team members to ensure a balanced workload and clear feature ownership.
* **Milestone & Release Management:** Established and enforced WEEKLY milestones to maintain a strict agile development cadence. Handled the end-to-end Release Management process, ensuring stable builds (JAR files) were properly packaged, tested, and tagged for each iteration.
* **General Code Enhancements & Debugging:** Acted as a primary troubleshooter for the codebase. Led general debugging efforts to resolve overarching integration bugs, runtime exceptions, and technical debts that spanned across multiple individual features rather than being confined to my own code.
* **Deliverable Maintenance:** Maintained the project's data storage formats to ensure backward compatibility during major refactoring. Managed the formatting and compilation of the final PDF deliverables for the UG and DG, ensuring proper pagination and functional PlantUML rendering.

### Review / Mentoring Contributions
* **Release Gatekeeping & Code Reviews:** As the person managing WEEKLY releases, I critically reviewed overarching PRs. I focused on ensuring new features did not break existing storage formats and rigidly enforced **SLAP** and **Checkstyle** standards across the codebase.
* **Architectural Mentorship:** Guided the team through a major mid-project refactoring phase. I demonstrated how to implement the **Context Object Pattern** to cleanly inject dependencies (ModuleList, EquipmentList, Storage) into commands, significantly improving the testability of my teammates' code.
* **Proactive Debugging & Unblocking:** Frequently stepped outside my assigned features to assist teammates with complex integration bugs. Mentored peers on writing robust JUnit assertions and utilizing IDE debuggers effectively to trace silent logic failures.