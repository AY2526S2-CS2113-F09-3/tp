# User Guide

## Introduction
Equipment Master is a Command Line Interface (CLI) application designed specifically for University Laboratory Technicians. It transforms chaotic peak-hour equipment loans into a streamlined, 100% accountable digital process. By tracking inventory against academic modules and student enrollment, it helps you forecast procurement needs instantly.

---

## Quick Start
1. Ensure that you have Java 17 or above installed.
2. Download the latest version of `EquipmentMaster.jar` into an empty folder.
3. Open a command terminal, navigate to the folder, and run: `java -jar EquipmentMaster.jar`.
4. Type `help` to view the command summary.

---

## Features

### 1. Module Tracking System
To accurately forecast laboratory demands, the system allows you to register academic modules, track their student enrollment (pax), and dynamically map equipment requirements to them.

#### Adding a new module: `addmod`
Registers a new academic course module into the system along with its expected student enrollment.
* **Format:** `addmod n/NAME pax/QTY`
* **Example:** `addmod n/CG2111A pax/150`

#### Listing all modules: `listmod`
Displays a summary of all registered modules and their respective student enrollments.
* **Format:** `listmod`

#### Updating a module's pax: `updatemod`
Updates the student enrollment size of an existing module. The system will automatically use this new pax to recalculate future equipment demands.
* **Format:** `updatemod n/NAME pax/QTY`
* **Example:** `updatemod n/CG2111A pax/180`

#### Deleting a module: `delmod`
Safely removes a module from the registry. Any equipment previously tagged to this module will be safely untagged (Safe Dereferencing) without deleting the equipment itself.
* **Format:** `delmod n/NAME`
* **Example:** `delmod n/CG2111A`

---

### 2. Enhanced Find Feature
#### Searching the inventory: `find`
Locates equipment quickly. You can search not only by the equipment's actual name but also by the module it is tagged to.
* **Format:** `find KEYWORD [MORE_KEYWORDS]`
* **Example:** `find STM32` (Finds equipment named STM32)
* **Example:** `find CG2111A` (Finds all equipment required for the CG2111A module)

---

### 3. Aging Equipment Report
Proactively audit your inventory to find equipment that has exceeded its expected lifespan based on the semantic university timeline.

#### Setting the Academic Context: `setsem`
Sets the current academic semester of the system, which is used as the baseline for calculating equipment age.
* **Format:** `setsem AY[YYYY]/[YY] Sem[1/2]`
* **Example:** `setsem AY2025/26 Sem1`

#### Generating the Aging Report: `report aging`
Scans the inventory and generates a report of all equipment whose age (calculated from their purchase semester to the current semester) meets or exceeds their defined lifespan.
* **Format:** `report aging [AY[YYYY]/[YY] Sem[1/2]]`
    * *(Note: If the optional semester argument is omitted, it defaults to the system's current semester set via `setsem`.)*
* **Example:** `report aging`
* **Example:** `report aging AY2026/27 Sem1` (Simulates an audit for a future semester)

---

### 4. Procurement Report
Forecast your laboratory equipment needs for the upcoming semester to justify budgeting and purchasing requests.

#### Setting a Safety Buffer: `setbuffer`
Sets a percentage safety buffer on specific equipment. This ensures you buy slightly more than the baseline module enrollments to account for potential damage, loss, or unexpected student increases.
* **Format:** `setbuffer n/NAME b/PERCENTAGE` or `setbuffer i/INDEX b/PERCENTAGE`
* **Example:** `setbuffer n/STM32 b/10` (Sets a 10% procurement buffer for STM32 boards)

#### Generating the Procurement Report: `report procurement`
Calculates the exact total number of items needed for the upcoming semester by cross-referencing your current stock levels against the student enrollment sizes (pax) of all associated modules, including any configured safety buffers. This allows you to proactively justify budget requests for equipment shortfalls.

**How the Calculation Works:**
1. **Determine Base Demand:** For each equipment, the system checks all the modules it is currently mapped to. It adds up the student enrollment sizes (pax) of these associated modules.
2. **Apply Safety Buffer & Indivisibility:** The system applies your configured `bufferPercentage` to the Base Demand. Following the "Indivisibility Rule," the result is mathematically rounded *up* to the nearest whole number to ensure you don't procure a fraction of a piece of equipment. This becomes the **Total Required**.
3. **Calculate Shortfall:** The system then subtracts your current **total stock quantity** for that item (all units you own, including any that are currently on loan) from the Total Required quantity.
4. **Generate Output:** If the Total Required exceeds your current total stock, the system flags a shortage. The item is added to the report, displaying the exact shortfall quantity you need to procure.

*Example scenario:* 
If `STM32` boards are needed for `CG2111A` (150 pax) and `CS2113` (50 pax), the **Base Demand** is 200. With a 10% safety buffer set via `setbuffer`, the buffered demand becomes 220. If your current total stock (regardless of how many units are currently loaned out) is 180 units, the `report procurement` command will alert you to a shortfall (TO BUY) of 40 `STM32` boards.

* **Format:** `report procurement`

---

### 5. Core Commands
These commands form the foundation of navigating and managing your current lab inventory. All list and report outputs are beautifully formatted using responsive ASCII tables for maximum readability.

#### Listing all equipment: `list`
Displays your entire equipment inventory in a cleanly aligned, responsive table format.
* **Format:** `list`

#### Viewing the interactive manual: `help`
Provides a comprehensive, in-application guide to all available commands, their parameters, and usage examples.
* **Format:** `help`

---

## FAQ

**Q: Do I need to type the exact, full name of the equipment when using the `find` command?**

**A:** No, the enhanced `find` command is case-insensitive and supports partial keyword matching. For example, searching `find STM` will return "STM32". Additionally, it matches against the module codes stored on each equipment record, so searching `find CG2111A` will return all equipment associated with that specific course.


**Q: The student enrollment size for a module just increased. Do I need to delete and recreate the module to update the numbers?**

**A:** Not at all. You can easily update the existing student enrollment size (pax) without affecting anything else by using the `updatemod` command (e.g., `updatemod n/CG2111A pax/200`).


**Q: If a course is no longer offered and I delete it using `delmod`, will the system accidentally delete the physical equipment associated with it?**

**A:** Your equipment is perfectly safe! The system uses a "Safe Dereferencing" mechanism. Deleting a module only removes the course from the academic registry. The actual equipment records and their quantities will remain securely in your inventory.


**Q: Why isn't the `report aging` command finding my older equipment accurately?**

**A:** The aging report calculates equipment age based on the system's *academic semester context*, not your computer's real-world calendar date. Ensure you have correctly set the current semester using the `setsem` command (e.g., `setsem AY2025/26 Sem1`) before generating the report. Alternatively, you can specify the target semester directly in your command (e.g., `report aging AY2026/27 Sem1`).


**Q: What happens if I accidentally enter a negative number for the student enrollment (pax) when adding or updating a module?**

**A:** The system has built-in defensive validation. It will immediately reject negative numbers or invalid text inputs for the pax, displaying an informative error message to help you correct the format, ensuring your lab data remains strictly accurate.

---

## Command Summary (Cheat Sheet)
* **Add Module:** `addmod n/NAME pax/QTY`
* **List Modules:** `listmod`
* **Update Pax:** `updatemod n/NAME pax/QTY`
* **Delete Module:** `delmod n/NAME`
* **Find Equipment:** `find KEYWORD`
* **Set Semester:** `setsem AY[YYYY]/[YY] Sem[1/2]`
* **Aging Report:** `report aging [AY[YYYY]/[YY] Sem[1/2]]`
* **Set Buffer:** `setbuffer n/NAME b/PERCENTAGE` or `setbuffer i/INDEX b/PERCENTAGE`
* **List Equipment:** `list`
* **Help Manual:** `help`
* **Procurement Report:** `report procurement`
