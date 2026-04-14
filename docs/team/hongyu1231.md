# Project Portfolio: Hongyu Chen

## 1. Overview
**Equipment Master** is a desktop CLI inventory management system optimized for University Lab Technicians. It replaces error-prone manual logbooks with a relational digital registry, enabling technicians to track hardware against academic modules, monitor lifecycles, and algorithmically forecast procurement needs based on enrollment.

---

## 2. Summary of Contributions

### Code Contributed
[RepoSense Dashboard Link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=hongyu1231&breakdown=true)

### Enhancements Implemented

#### **Core Logic & Predictive Analytics**
* **Semantic Academic Time Engine:** Designed and implemented the `AcademicSemester` normalization engine. This algorithm performs floating-point mathematical operations on non-standard university timelines (e.g., `AY24/25 Sem1`), providing the underlying logic for time-based calculations.
* **Automated Aging & Lifespan Audits:** Engineered the **Aging Report** feature, which utilizes the time engine to calculate the real-time age of hardware against its expected lifespan. This allows technicians to proactively identify aging equipment and forecast replacement cycles.
* **Relational Module Tracking:** Developed the entity structure linking `Equipment` to `Module` requirements. Implemented **Bidirectional Entity Synchronization** and **Safe Dereferencing** to ensure a Single Source of Truth, preventing "ghost references" during module deletions and tag updates.
* **Multi-Keyword Search Optimization (Find):** Refactored the `find` command to support complex multi-keyword cross-referencing across both item names and module codes, strictly adhering to the **Single Level of Abstraction Principle (SLAP)** to eliminate deeply nested iterations.

#### **System Architecture & Robustness (Technical Hardening)**
* **Atomic Persistence Strategy (Dual-Save):** Implemented a **Dual-Save protocol** to prevent data desynchronization between database files (`equipment.txt` and `module.txt`). This ensures cross-linked data is written atomically, maintaining integrity after application restarts.
* **Architectural Decoupling (Context Pattern):** Led the system-wide integration of the **Context Object Pattern**. By encapsulating Storage, UI, and state into a unified `Context`, I decoupled command logic from infrastructure, significantly improving testability via dependency injection.
* **State Management & UX Optimization:** Refactored configuration commands (e.g., `getsem`) to distinguish between "implicit system defaults" and "explicit user values" via persistence file checks, eliminating confusing UI prompts and enhancing initialization logic.
* **Defensive API Hardening & Diagnostics:** Systematically migrated critical business logic from Java assertions to explicit **Defensive Exception Handling**. Enhanced the `Storage` component with line-level validation to identify exact corruption locations, preventing silent data loss.

### Documentation Contributions

* **User Guide (UG):** 
  * **Core Feature Tutorials:** Authored the *Introduction*, *Quick Start*, *FAQ*, and comprehensive tutorials for *Module Tracking*, *Advanced Find*, and *Aging Reports*.
  * **UX Design:** Designed the *Error Handling* section and integrated **responsive ASCII screenshots** to visually demonstrate command outcomes.
  * **Standardization:** Established strict syntax guidelines (`[...]` vs `UPPER_CASE`) and formatted the *Command Summary (Cheat Sheet)*.
* **Developer Guide (DG):** 
  * **Architectural Deep-dives:** Authored technical documentation for the **Storage Persistence Mechanism**, **Multi-keyword Find logic**, **Context Object Pattern**, and **Academic Semester calculation**.
  * **Visual Modeling:** Integrated complex **PlantUML Sequence, Class, and State diagrams** detailing the execution lifecycle, relational mapping, and component interactions.
  * **Requirements Engineering:** Drafted comprehensive **Use Cases (MSS & Extensions)**, the *Target User Profile*, and *User Stories*.
  * **Project Effort & Grading Justification:** Drafted the *Appendix* to explicitly defend the team's technical complexity, articulating the engineering challenges of relational mapping and lifecycle forecasting to justify the grading criteria.

### Team-Based Tasks & Quality Assurance

* **Release & Deliverable Management:** Spearheaded the end-to-end release cycle (JAR packaging and GitHub Releases) across all iterations. Managed the final typesetting, Markdown standardization, and dynamic TOC generation for all PDF deliverables (UG/DG).
* **Quality Assurance (Chief Debugger):** Led the triage phase during internal testing and PE-D. Coordinated integration testing to maintain Storage backward compatibility and successfully resolved 10+ high-severity vulnerabilities.
* **Code Gatekeeping & Mentorship:** Acted as the primary reviewer for team PRs, rigorously enforcing Checkstyle compliance, SLAP, and robust exception handling. Guided the team through architectural refactoring (e.g., transitioning to the **Command Factory pattern**) and mentored peers on effective JUnit 5 testing strategies.