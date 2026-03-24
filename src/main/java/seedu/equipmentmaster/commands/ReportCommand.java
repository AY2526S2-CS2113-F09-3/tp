package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.semester.AcademicSemester;

// @@author Hongyu1231
/**
 * Generates specific reports for the equipment inventory.
 */
public class ReportCommand extends Command {
    private final String reportType;
    private final String targetSemStr;


    public ReportCommand(String reportType, String targetSemStr) {
        this.reportType = reportType;
        this.targetSemStr = targetSemStr;
    }

    /**
     * Parses the arguments for the 'report' command.
     * @param fullCommand The complete input string.
     * @return A ReportCommand object.
     * @throws EquipmentMasterException If arguments are missing.
     */
    public static ReportCommand parse(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.trim().split("\\s+", 3);

        if (words.length < 2) {
            throw new EquipmentMasterException("Please specify the report type. Usage: report aging [Semester] OR " +
                    "report lowstock");
        }

        String reportType = words[1].trim().toLowerCase();
        // Extract optional semester argument if it exists
        String targetSem = (words.length == 3) ? words[2].trim() : "";

        return new ReportCommand(reportType, targetSem);
    }


    /**
     * Executes the report command.
     * Analyzes the equipment list to generate and display either a low-stock alert report or an aging equipment report.
     *
     * @param context The application context containing the equipment list, UI, and current system semester.
     */
    @Override
    public void execute(Context context) {
        Ui ui = context.getUi();
        EquipmentList equipments = context.getEquipments();

        if (reportType.equalsIgnoreCase("lowstock")) {
            executeLowStockReport(equipments, ui);
        } else if (reportType.equalsIgnoreCase("aging")) {
            executeAgingReport(equipments, ui, context);
        } else {
            ui.showMessage("Invalid report type. Currently supported: aging, lowstock.");
        }
    }
    // @@author Hongyu1231

    private void executeLowStockReport(EquipmentList equipments, Ui ui) {
        ui.showMessage("Low Stock Alert (Items below minimum threshold):");
        boolean foundLowStock = false;
        int count = 0;

        for (int i = 0; i < equipments.getSize(); i++) {
            Equipment eq = equipments.getEquipment(i);
            if (eq.getQuantity() < eq.getMinQuantity()) {
                foundLowStock = true;
                count++;
                ui.showMessage(count + ". " + eq.getName()
                        + " | Quantity: " + eq.getQuantity()
                        + " | Min: " + eq.getMinQuantity() + " -> RESTOCK NEEDED");
            }
        }

        if (!foundLowStock) {
            ui.showMessage("All inventory levels are above their minimum thresholds.");
        }
    }

    // @@author Hongyu1231
    private void executeAgingReport(EquipmentList equipments, Ui ui, Context context) {
        AcademicSemester targetSem;
        try {
            if (!targetSemStr.isEmpty()) {
                targetSem = new AcademicSemester(targetSemStr);
            } else {
                targetSem = context.getCurrentSemester();
                if (targetSem == null) {
                    throw new EquipmentMasterException("System semester not set! Use 'setsem' first.");
                }
            }
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
            return;
        }

        ui.showMessage("Aging Equipment Report (Calculated for: " + targetSem + "):");

        boolean foundAging = false;

        for (int i = 0; i < equipments.getSize(); i++) {
            Equipment eq = equipments.getEquipment(i);
            AcademicSemester purchaseSem = eq.getPurchaseSem();
            double lifespan = eq.getLifespanYears();
            if (purchaseSem == null || lifespan <= 0) {
                continue;
            }

            double age = purchaseSem.calculateAgeInYears(targetSem);
            if (age >= lifespan) {
                foundAging = true;
                String msg = String.format("%d. %s (Qty: %d, Bought: %s) | Age: %.1f Years | Status: [REPLACE SOON]",
                        (i + 1), eq.getName(), eq.getQuantity(), purchaseSem, age);
                ui.showMessage(msg);
            }

        }

        if (!foundAging) {
            ui.showMessage("Great news! No equipment needs replacement for this semester.");
        }
    }
    // @@author Hongyu1231
}

