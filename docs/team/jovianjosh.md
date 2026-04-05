# Jovian Josh - Project Portfolio Page

## Overview
**Equipment Master** is a desktop CLI (Command Line Interface) application engineered for University Laboratory Technicians. It replaces highly inefficient, paper-based inventory logbooks with a 100% accountable digital registry. The system allows technicians to manage high-volume equipment loans during peak academic weeks, instantly track assets by course modules, and proactively forecast procurement needs based on equipment aging and student enrollment sizes.

## Summary of Contributions

### Code Contributed
[Repo dashboard link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=JovianJosh&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

---

### Enhancements Implemented

#### 1. Module-Equipment Association via Add Command (`add` with `m/` parameter)
* **What it does:** Allows technicians to associate equipment with academic modules at the time of inventory addition using the `m/` parameter. Equipment can be linked to multiple modules simultaneously (e.g., `add n/STM32 q/50 m/CG2111A m/EE2026`).
* **Justification:** Equipment usage is fundamentally tied to academic courses. Without this association, the system cannot aggregate demand across modules for procurement forecasting. I designed a flexible multi-tag system where each equipment maintains a list of module codes, enabling efficient cross-referencing for reports and searches.
* **Technical Highlights:** Implemented robust parsing with `extractMultipleArguments()` that supports case-insensitive matching and automatically filters duplicate module codes. Ensured storage compatibility by handling module codes in the equipment file format with proper encoding/decoding to prevent delimiter collisions.

#### 2. Equipment Status Management (`setstatus`)
* **What it does:** Allows technicians to update the loaned and available counts of equipment items, reflecting real-time borrowing and return activity during busy lab hours.
* **Justification:** This is the most frequently used operation in the system, performed under time pressure during peak borrowing periods. I implemented dual targeting (by name or by 1-based list index) to maximize throughput — technicians can quickly type `setstatus 1 q/3 s/loaned` without recalling full equipment names.
* **Technical Highlights:** Enforced critical business rules: loan counts cannot exceed available stock, return counts cannot exceed loaned quantities, and zero/negative counts are rejected. Implemented immediate persistence to storage after each transaction to ensure data integrity.

#### 3. Safety Buffer Configuration (`setbuffer`)
* **What it does:** Enables lab managers to configure a percentage safety buffer on equipment items, ensuring procurement recommendations account for expected wear, breakage, or unexpected demand spikes.
* **Justification:** Raw enrollment figures alone cannot capture real-world equipment attrition. This feature allows technicians to apply domain expertise to procurement forecasts. I implemented dual targeting (name or index) for flexibility, and integrated the buffer value directly into the procurement calculation pipeline.
* **Technical Highlights:** The command strips optional `%` symbols during parsing, validates non-negative percentages, and immediately persists changes to storage. Buffer values default to `0.0` for new equipment, ensuring backward compatibility.

---

### Contributions to the User Guide
* Documented the `add` command's `m/` parameter syntax, explaining how to associate equipment with multiple modules at creation time with practical examples.
* Authored the "Equipment Status Management" section, detailing the `setstatus` command format, dual targeting options, and critical business constraints with examples.
* Authored the "Setting a Safety Buffer" subsection, documenting the `setbuffer` command usage, optional `%` symbol handling, and integration with procurement forecasting.
* Added `setstatus` and `setbuffer` entries to the Command Summary cheat sheet with correct syntax.

---

### Contributions to the Developer Guide
* Authored the feature implementation sections for `SetStatusCommand`, `SetBufferCommand`, and the module-equipment association mechanism within `AddCommand`, covering architecture, execution flow, and design considerations.
* Authored PlantUML Sequence Diagrams for `SetStatusCommand` and `SetBufferCommand`, illustrating the dual-targeting execution flow and name/index resolution with persistence.
* Documented the rationale behind dual targeting (name vs. index), the multi-tag parsing approach, and buffer configuration design considerations.
* Designed manual testing procedures for module-equipment association, loan/return operations, and buffer configuration under "Instructions for Manual Testing".

---

### Contributions to Team-Based Tasks
* Reviewed pull requests related to command parsing and inventory management, ensuring consistent validation patterns and error handling across the codebase.
* Ensured `setstatus`, `setbuffer`, and the `m/` parameter in `add` properly integrated with the Context object pattern and storage mechanisms established by the team.
* Enforced strict command input validation across my features, ensuring users are guided with clear error messages when command formats are incorrect, reducing invalid state errors during runtime.

---

### Review / Mentoring Contributions
* Provided detailed feedback on teammates' command implementations, focusing on consistent error messages, proper validation ordering, and adherence to the team's coding standards (SLAP, Checkstyle).
* Included boundary condition test cases as part of my class implementations (zero quantities, negative percentages, out-of-bounds indices, duplicate module codes) to improve overall code coverage.
* Reviewed and suggested improvements to the User Guide and Developer Guide to ensure technical accuracy and clarity for end users and future developers.