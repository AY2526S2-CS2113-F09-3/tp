# Project Portfolio: Hongyu Chen

## 1. Overview
**Equipment Master** is a desktop CLI inventory management system optimized for University Lab Technicians. It replaces error-prone manual logbooks with a relational digital registry, enabling technicians to track hardware against academic modules, monitor lifecycles, and algorithmically forecast procurement needs based on enrollment.

---

## 2. Summary of Contributions

### Code Contributed
[RepoSense Dashboard Link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=hongyu1231&breakdown=true)

### Enhancements Implemented

#### **Core Logic & Asset Intelligence**
* **Semantic Academic Time Engine:** Designed the `AcademicSemester` normalization algorithm to perform floating-point calculations on non-standard university timelines (e.g., `AY2024/25 Sem1`), providing the logic base for time-dependent operations.
* **Automated Aging & Lifespan Audits:** Engineered the **Aging Report** feature, which utilizes the time engine to calculate the real-time age of hardware against its expected lifespan to forecast replacement cycles.
* **Relational Mapping & Bidirectional Sync:** Developed the entity structure linking `Equipment` to `Module`. Implemented a synchronization protocol and **Safe Dereferencing** to maintain a Single Source of Truth during deletions and tag updates.
* **Algorithmic Search Optimization (Find):** Refactored the `find` command for multi-keyword cross-referencing, strictly adhering to **SLAP** to eliminate deeply nested iterations.

#### **Architecture & System Reliability**
* **Context Object Pattern:** Led the architectural refactoring to decouple logic from Storage and UI components, significantly improving system testability through dependency injection.
* **Atomic Persistence (Dual-Save):** Developed a **Dual-Save protocol** to ensure cross-file data consistency between linked database files, preventing data loss after application restarts.
* **Defensive Engineering & Diagnostics:** Migrated business logic to production-grade **Defensive Exception Handling**. Enhanced `Storage` with line-level diagnostics to identify exact corruption points in text files.

### Documentation Contributions

* **User Guide (UG):** Authored the *Introduction*, *FAQ*, and tutorials for *Aging Reports* and *Module Tracking*. Designed the *Error Handling* section and established syntax standards (`[...]` vs `UPPER_CASE`).
* **Developer Guide (DG):** 
  * **Technical Deep-dives:** Authored deep-dives for **Storage Persistence**, **Multi-keyword Find**, **Context Object Pattern**, **Academic Semester Calculation**, and **Aging Report Logic**.
  * **System Modeling:** Integrated complex **PlantUML Sequence, Class, and State diagrams** detailing entity relationships and command execution lifecycles.
  * **Project Framework:** Wrote the *Project Scope*, *Target User Profile*, and a comprehensive list of *Prioritized User Stories*.
  * **Quality & Validation:** Drafted detailed **Use Cases (MSS & Extensions)** and **Manual Testing Instructions** to guide systematic verification of system features.
* **Grading Justification:** Authored the *Project Effort Appendix* to defend the technical complexity of relational mapping and predictive forecasting.

### Team-Based Tasks & Quality Assurance

* **Release & Project Management:** Spearheaded the end-to-end release cycle (v1.0 - v2.1) and managed final typesetting and TOC generation for all PDF deliverables.
* **Quality Assurance (Chief Debugger):** Led the triage phase during PE-D, resolving 10+ high-severity synchronization and persistence vulnerabilities.
* **Mentorship:** Guided the team through the **Command Factory** refactoring and mentored peers on effective JUnit 5 testing strategies.