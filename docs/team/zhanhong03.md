# Choy Zhan Hong - Project Portfolio Page

## Overview
**Equipment Master** is a desktop CLI (Command Line Interface) application engineered for University Laboratory Technicians. It replaces highly inefficient, paper-based inventory logbooks with a 100% accountable digital registry. The system allows technicians to manage high-volume equipment loans during peak academic weeks, instantly track assets by course modules, and proactively forecast procurement needs based on equipment aging and student enrollment sizes.

## Summary of Contributions

### Code Contributed
* https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=zhanhong03&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=false&filteredFileName=

### Enhancements Implemented
I implemented critical inventory safety mechanisms and UX refinements that ensure data integrity and proactive equipment management.

1. **Inventory Safety Threshold System (`setmin`, `add`, `delete`)**
    * **What it does:** Allows technicians to define a "Minimum Stock Level" for any asset. The system proactively triggers a high-visibility `!!! LOW STOCK ALERT` whenever stock is added or deleted such that the available quantity falls to or below the safety buffer.
    * **Justification & Depth:** In a high-stakes laboratory environment, discovering a stockout during a lab session is a critical failure. I engineered a proactive notification layer within the execute() methods of AddCommand and DeleteCommand. This ensures the system acts as an active monitor rather than a passive database. Additionally, extended SetMinCommand to support index-based targeting (e.g., setmin 1 min/10), consistent with the dual-targeting pattern used across other commands such as delete and setstatus.

2. **Context-Aware Semester Enrollment Warnings (`setsem`)**
    * **What it does:** Detects academic semester shifts and issues a "Smart Reminder" to the technician to update student enrollment data (pax) for the new term.
    * **Justification & Depth:** Procurement reports are only as accurate as their input data. I implemented a State-Change Observer logic that only triggers the warning if the semester actually changes and existing modules are detected, preventing "alert fatigue" from redundant notifications.

3. **UI Resilience & Table Optimization (`UiTableRow`)**
    * **What it does:** Integrated the `Min`: threshold display into the main inventory list and refactored the row-rendering logic.
    * **Justification & Depth:** I identified and resolved a critical Column Count Mismatch bug. In the original architecture, mixing equipment with module tags and those without caused the UiTable to crash. I refactored the constructor to handle dynamic column lengths, ensuring a stable and responsive ASCII display regardless of data complexity.

4. **Defensive Storage Architecture & Failure Testing (`TagCommand`, `DelModCommand`)**
    * **What it does:** Hardened the TagCommand and DelModCommand against "Ghost References" and simulated disk failures using advanced mocking techniques.
    * **Justification & Depth:** I implemented a Double Ghost Reference Check to maintain database integrity between the Module and Equipment lists. To verify this, I designed a test suite using Anonymous Class Mocking to inject simulated EquipmentMasterException triggers during the storage save process. This allowed me to achieve 100% Branch Coverage and prove that the application can recover gracefully from file-system errors.

### Contributions to the User Guide (UG)
* **Authored the "Inventory Safety & Management" section:** Documented the usage for setmin and the advanced delete command, including clear explanations of how the status-based (`s/available` or `s/loaned`) logic interacts with stock alerts.
* **Authored FAQ entries regarding Stock Alerts:** Provided clear troubleshooting for common user queries, such as why alerts trigger immediately upon adding equipment and how safety thresholds influence procurement decisions.

### Contributions to the Developer Guide (DG)
* **Authored Feature Implementation Sections:** Detailed the architecture and execution flow for the Proactive Alert System and the Semester State Change logic.
* **Contributed UML Diagrams:** Sequence Diagram: Illustrating the logic flow for DeleteCommand, showing the internal check between the new quantity state and the `minQuantity` threshold. 
* Activity Diagram: Mapping the decision-making process in SetSemCommand regarding when to fire the enrollment update warning.