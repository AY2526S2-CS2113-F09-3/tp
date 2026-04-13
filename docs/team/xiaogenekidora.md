# Ge Wenqing - Project Portfolio Page

## Overview
**Equipment Master** is a desktop CLI (Command Line Interface) application engineered for University Laboratory Technicians. It replaces highly inefficient, paper-based inventory logbooks with a 100% accountable digital registry. The system allows technicians to manage high-volume equipment loans during peak academic weeks, instantly track assets by course modules, and proactively forecast procurement needs based on equipment aging and student enrollment sizes.

## Summary of Contributions

### Code Contributed
* https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=xiaogenekidora&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=

### Enhancements Implemented
I contributed significantly to the foundational architecture and user interface of the application, focusing on maintainability, presentation, and core functionality.

1. **Universal UiTable Utility Class**
    * **What it does:** Provides a robust, reusable utility for formatting text-based data into cleanly aligned, responsive ASCII tables in the CLI.
    * **Justification & Depth:** A core requirement of generating reports and listing inventory in a CLI is readability. Without a standardized table formatter, output would be misaligned and difficult to parse. I engineered `UiTable` to dynamically adjust column widths based on content, handle text wrapping gracefully, and provide a consistent visual language across all commands.

2. **Parser Refactoring (Command Factory Pattern)**
    * **What it does:** Completely overhauled the `Parser` component to utilize the Command Factory style, moving away from monolithic switch-case statements.
    * **Justification & Depth:** The initial parser was tightly coupled and violated the Open-Closed Principle. By implementing a Command Factory, I decoupled command parsing from execution. This architectural change allowed team members to add new commands simply by registering them in the factory, drastically reducing merge conflicts and improving scalability.

3. **Core Application Commands (`report procurement`, `help`, `list`)**
    * **What it does:** Implemented essential features for viewing and managing standard inventory. `list` displays current items, `help` provides an interactive command manual, and `report procurement` calculates the total number of items needed for the upcoming semester based on module enrollment and current stock levels.
    * **Justification & Depth:** The `report procurement` command required complex integration between module enrollment figures and existing equipment counts to accurately predict shortfall. This feature directly tackles the primary value proposition of the app: proactively forecasting procurement needs.

4. **Testing**
   * **What it does:** Developed comprehensive unit tests for not only own features but also other components like DeleteCommand etc. in order to increase the overall code coverage and ensure robustness.
   * **Justification & Depth:** Given the critical nature of these components in ensuring the application's reliability and maintainability, I prioritized testing to catch edge cases.

### Contributions to the User Guide (UG)
* **Authored the "Core Commands" section:** Documented the usage formats and parameters for `list` and `help`, complete with expected terminal outputs formatted using the new `UiTable`.
* **Authored the "Procurement Report" section:** Explained the rationale and usage of the `report procurement` command, providing practical examples of how lab technicians can use this feature to justify budget requests.

### Contributions to the Developer Guide (DG)
* **Authored Feature Implementation Sections:** Detailed the architecture, execution flow, and design considerations for the **UiTable Utility**, **Parser Refactoring (Command Factory)**, and the **Procurement Report**.
* **Contributed UML Diagrams:** Authored PlantUML diagrams mapping out my specific architectural contributions, including:
    * Class Diagram detailing the new `Parser` Command Factory logic.
    * Visual representation of the `UiTable` dynamic width calculation process (`uiTable.png`).

### Team-Based Tasks & Project Management
* **Consistency & Code Quality:** Unified the definition of "low quantity/stock" across various commands and reports, ensuring the entire team used a consistent, standardized metric throughout the codebase, User Guide, and Developer Guide.
