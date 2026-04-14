# Equipment Master (v2.1)

> **The professional asset management expert tailored for University Lab Technicians.**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-11%2F17-orange)](https://www.oracle.com/java/)

**Equipment Master** is a desktop Command Line Interface (CLI) inventory management system. It replaces error-prone manual logbooks and scattered Excel sheets by establishing a relational digital registry between lab hardware and academic modules. It empowers technicians to track inventory precisely, predict lifecycle replacements, and optimize procurement decisions based on enrollment data.

---

## 🚀 Core Features

* **Relational Module Tracking:** Goes beyond simple lists. It links equipment to specific academic courses (e.g., `CG2111A`, `EE2026`) with bidirectional synchronization, allowing you to instantly identify hardware shortages for upcoming experiments.
* **Semantic Academic Time Engine:** Features a pioneering normalization algorithm tailored for university timelines (e.g., `AY24/25 Sem1`), supporting precise asset depreciation calculations on non-standard academic calendars.
* **Predictive Aging Report:** Automatically tracks hardware lifecycles. Based on purchase dates and expected lifespan, it proactively alerts technicians about aging tools that require replacement.
* **Atomic Persistence (Dual-Save):** Utilizes a robust `Dual-Save` storage protocol. This ensures absolute data consistency across linked database files, completely eliminating "ghost references" or data desynchronization after application restarts.
* **Defensive Engineering:** Deeply integrates the **SLAP** principle and the **Context Object** design pattern. Features comprehensive exception handling and line-level storage diagnostics to guarantee system stability under edge cases.

---

## 🛠️ Quick Start

### Prerequisites
* Ensure you have **Java 11** or above installed on your computer (Java 17 is recommended).

### Running the Application
1. Download the latest `tp.jar` file from the [Releases](https://github.com/AY2526S2-CS2113-F09-3/tp/releases) page.
2. Place the JAR file into an empty folder.
3. Open your Terminal (or Command Prompt / PowerShell) and navigate to that folder.
4. Run the following command to start the application:
   `java -jar tp.jar`

---

## 📖 Documentation

Our documentation is structured to support both end-users and developers:

* **[User Guide (UG)](docs/UserGuide.md):** Detailed command formats, visual ASCII examples, and a comprehensive FAQ/Error Handling section.
* **[Developer Guide (DG)](docs/DeveloperGuide.md):** In-depth architectural deep-dives, PlantUML diagrams, data models, and core algorithm logic (e.g., Aging Report & Persistence).

---

## ⌨️ Command Sneak Peek

| Action | Example Command |
| :--- | :--- |
| **Add Equipment** | `add n/STM32 q/50 m/CG2111A bought/AY24/25 Sem1 life/2.5` |
| **Aging Report** | `report aging AY2024/25 Sem1` |
| **Multi-keyword Find** | `find STM32 CG2111A` |
| **Update Loan Status** | `setstatus n/STM32 q/10 s/loaned` |
| **Set Safety Buffer** | `setbuffer n/STM32 b/10` |

---

## 👥 Contributors

This project was developed by the **NUS CS2113-F09-3** team.

* **Hongyu Chen** - *Core Logic Design, Architectural Refactoring (Context Pattern), Persistence Mechanism, Predictive Algorithms.*
* *(Add other team members here)*

---

## ⚖️ License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
