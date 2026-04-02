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

### 1. Equipment Inventory Management
To maintain an organized and efficient laboratory inventory, the system allows you to register physical equipment, track stock levels, associate items with modules, and configure lifecycle and alert settings.

#### Adding new equipment: `add`
Registers new physical equipment into your laboratory inventory. You can perform a basic addition or include optional parameters to immediately tag the equipment to modules, set its expected lifespan, and configure low-stock alerts.

* **Format:** `add n/NAME q/QUANTITY [m/MODULE_CODE]... [bought/SEMESTER life/YEARS] [min/MIN_QTY]`

**Examples:**
* **Basic addition:** `add n/Soldering Iron q/25`
* **Adding with multiple modules:** `add n/STM32 q/150 m/CG2111A m/EE2026`
* **Adding with lifespan tracking:** `add n/Oscilloscope q/10 bought/AY2023/24 Sem1 life/5.0`
* **Adding with a low-stock alert threshold:** `add n/Jumper Wires q/500 min/100`
* **Adding with all parameters:** `add n/Raspberry Pi 4 q/30 m/CG2111A bought/AY2024/25 Sem1 life/4.0 min/5`


### 2. Module Tracking System
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

#### Linking equipment to a module: `tag`
Dynamically maps a piece of physical equipment to an academic module. You must specify a requirement ratio (`req/`) to account for equipment that is shared among groups of students during a lab session. This data is critical for generating accurate procurement forecasts.
* **Format:** `tag m/MOD_NAME n/EQ_NAME req/FRACTION`
* **Example:** `tag m/CG2111A n/STM32 req/1.0` (Every 1 student requires 1 STM32 board)
* **Example:** `tag m/CS2113 n/Soldering Iron req/0.2` (1 Soldering Iron is shared among a group of 5 students)

#### Unlinking equipment from a module: `untag`
Removes a specific equipment requirement from an academic module. This is useful if a course curriculum changes and a specific item is no longer needed for the classes.
* **Format:** `untag m/MOD_NAME n/EQ_NAME`
* **Example:** `untag m/CG2111A n/STM32`

---

### 3. Enhanced Find Feature
#### Searching the inventory: `find`
Locates equipment quickly. You can search not only by the equipment's actual name but also by the module it is tagged to.
* **Format:** `find KEYWORD [MORE_KEYWORDS]`
* **Example:** `find STM32` (Finds equipment named STM32)
* **Example:** `find CG2111A` (Finds all equipment required for the CG2111A module)

---

### 4. Aging Equipment Report
Proactively audit your inventory to find equipment that has exceeded its expected lifespan based on the semantic university timeline.

#### Setting the Academic Context: `setsem`
Sets the current academic semester of the system, which is used as the baseline for calculating equipment age.
* **Format:** `setsem AY[YYYY]/[YY] Sem[1/2]`
* **Example:** `setsem AY2025/26 Sem1`
* **Smart Reminder:** If the semester changes and modules exist in the system, a warning will remind you to update enrollment numbers (`pax`) using `updatemod` to maintain report accuracy.

#### Generating the Aging Report: `report aging`
Scans the inventory and generates a report of all equipment whose age (calculated from their purchase semester to the current semester) meets or exceeds their defined lifespan.
* **Format:** `report aging [AY[YYYY]/[YY] Sem[1/2]]`
    * *(Note: If the optional semester argument is omitted, it defaults to the system's current semester set via `setsem`.)*
* **Example:** `report aging`
* **Example:** `report aging AY2026/27 Sem1` (Simulates an audit for a future semester)

---

### 5. Procurement Report
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

### 5. Equipment Status Management

#### Updating equipment loan status: `setstatus`
Updates the loaned or available count of an equipment item to reflect real-time borrowing and return activity. You can target equipment by name or by its 1-based index in the list.

* **Format:**
    * `setstatus n/NAME q/COUNT s/loaned` — loans out COUNT units, decreasing available stock
    * `setstatus n/NAME q/COUNT s/available` — returns COUNT units, increasing available stock
    * `setstatus INDEX q/COUNT s/loaned/available` — same as above but targets by list index
* **Example:** `setstatus n/BasyS3 FPGA q/5 s/loaned`
* **Example:** `setstatus 1 q/3 s/available`

> **Note:** The count must be a positive whole number (zero and negatives are rejected). When loaning, the count cannot exceed current available stock. When returning, the count cannot exceed current loaned quantity.

---

### 6. Core Commands
These commands form the foundation of navigating and managing your current lab inventory. List-style outputs are beautifully formatted using responsive ASCII tables for maximum readability.

#### Listing all equipment: `list`
Displays your entire equipment inventory in a cleanly aligned, responsive table format.
* **Format:** `list`
* **Note:** The table includes a **Min:** column so you can monitor your safety thresholds alongside current stock levels.

#### Viewing the interactive manual: `help`
Provides a comprehensive, in-application guide to all available commands, their parameters, and usage examples.
* **Format:** `help`

#### Setting low stock thresholds: `setmin`
Configures a minimum quantity threshold for an equipment item. When available stock drops to or below this level, the system triggers a `!!! LOW STOCK ALERT`.
* **Format:** `setmin [n/NAME | INDEX] min/QUANTITY`
* **Example:** `setmin 1 min/15` (Sets the 1st item's threshold to 15)
* **Example:** `setmin n/Resistor min/10`
* **Note:** If the stock is already below the new threshold, the system will warn: `Warning: Item is currently below this new threshold!`

#### Deleting specific quantities: `delete`
Removes a specific number of units from either the available or loaned pool.
* **Format:** `delete [n/NAME | INDEX] q/QUANTITY s/STATUS`
  * `STATUS` must be either `available` or `loaned`.
* **Example:** `delete 1 q/5 s/available`
* **Logic:** If this action causes stock to hit the minimum threshold, a low stock alert will be displayed immediately.

---

## FAQ

**Q: Why am I getting an "Invalid name!" error when trying to add equipment?**

**A:** To protect the integrity of the application's save files, equipment names cannot contain certain reserved characters. Ensure your equipment name does not include vertical bars (|), commas (,), or equals signs (=).


**Q: Can I add a lifespan to an item without specifying when it was bought?**

**A:** To accurately calculate when an item will expire, the system requires both a starting point and a duration. You should provide both the `bought/` semester and the `life/` duration together if you want the system to track an item's age. If you supply only one of these fields, the `add` command will still succeed, but the lifespan and purchase information will be ignored and no expiry tracking will be configured.


**Q: Can I just delete an entire equipment record without typing out the exact quantity?**

**A:** No. Equipment Master requires you to explicitly state the exact quantity and the status (available or loaned) you are deleting. This strict requirement prevents accidental complete deletions and ensures your audit trails remain 100% accurate. If you want to delete the whole record, simply delete the total remaining quantity, and the system will automatically remove the record for you.


**Q: What happens if a deletion causes my inventory to drop below the safety threshold I set when I added the equipment?**

**A:** The system will immediately catch it! If a delete command drops your total quantity down to or below your configured minimum stock threshold, a LOW STOCK ALERT will be prominently displayed on your terminal.


**Q: What happens if I use setmin to set a threshold that is actually higher than my current total stock?**

**A:** The system will successfully update your threshold, but it will immediately display a warning message: "Warning: Item is currently below this new threshold!" This ensures you are instantly aware that you are already in a state of shortage based on your new safety standards.


**Q: Do I need to type the exact, full name of the equipment when using the `find` command?**

**A:** No, the enhanced `find` command is case-insensitive and supports partial keyword matching. For example, searching `find STM` will return "STM32". Additionally, it matches against the module codes stored on each equipment record, so searching `find CG2111A` will return all equipment associated with that specific course.

**Q: Will the system warn me if I add new equipment that is already below the threshold?**

**A:** Yes. If you use the `add` command with a `min/` flag (e.g., `add n/Resistor q/10 min/15`), the system will trigger a `!!! LOW STOCK ALERT` immediately upon addition.

**Q: The student enrollment size for a module just increased. Do I need to delete and recreate the module to update the numbers?**

**A:** Not at all. You can easily update the existing student enrollment size (pax) without affecting anything else by using the `updatemod` command (e.g., `updatemod n/CG2111A pax/200`).


**Q: If a course is no longer offered and I delete it using `delmod`, will the system accidentally delete the physical equipment associated with it?**

**A:** Your equipment is perfectly safe! The system uses a "Safe Dereferencing" mechanism. Deleting a module only removes the course from the academic registry. The actual equipment records and their quantities will remain securely in your inventory.


**Q: Why isn't the `report aging` command finding my older equipment accurately?**

**A:** The aging report calculates equipment age based on the system's *academic semester context*, not your computer's real-world calendar date. Ensure you have correctly set the current semester using the `setsem` command (e.g., `setsem AY2025/26 Sem1`) before generating the report. Alternatively, you can specify the target semester directly in your command (e.g., `report aging AY2026/27 Sem1`).


**Q: What happens if I accidentally enter a negative number for the student enrollment (pax) when adding or updating a module?**

**A:** The system has built-in defensive validation. It will immediately reject negative numbers or invalid text inputs for the pax, displaying an informative error message to help you correct the format, ensuring your lab data remains strictly accurate.

**Q: A student is returning equipment but I only remember the item's position in the list, not its full name. Do I need to look up the name first?**

**A:** No, the `setstatus` command supports both name-based and index-based targeting. You can simply use the item's position in the list (e.g., `setstatus 1 q/3 s/available`) to log a return instantly without needing to recall the full equipment name, keeping the process fast during busy peak hours.

---

## Command Summary (Cheat Sheet)
* **Add Equipment:** `add n/NAME q/QUANTITY [m/MODULE_CODE]... [bought/SEM life/YEARS] [min/MIN_QTY]`
* **Add Module:** `addmod n/NAME pax/QTY`
* **List Modules:** `listmod`
* **Update Pax:** `updatemod n/NAME pax/QTY`
* **Delete Module:** `delmod n/NAME`
* **Find Equipment:** `find KEYWORD`
* **Set Semester:** `setsem AY[YYYY]/[YY] Sem[1/2]`
* **Aging Report:** `report aging [AY[YYYY]/[YY] Sem[1/2]]`
* **Set Buffer:** `setbuffer n/NAME b/PERCENTAGE` or `setbuffer i/INDEX b/PERCENTAGE`
* **Update Loan Status:** `setstatus n/NAME q/COUNT s/loaned/available` or `setstatus INDEX q/COUNT s/loaned/available`
* **Set Min Threshold:** `setmin [n/NAME | INDEX] min/QUANTITY`
* **Delete Specific Quantity:** `delete [n/NAME | INDEX] q/QUANTITY s/STATUS`
* **List Equipment:** `list`
* **Help Manual:** `help`
* **Procurement Report:** `report procurement`
* **Tag Module:** `tag m/MOD_NAME n/EQ_NAME req/FRACTION`
* **Untag Module:** `untag m/MOD_NAME n/EQ_NAME`
