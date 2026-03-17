package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.EquipmentMaster;

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
            throw new EquipmentMasterException("Please specify the report type. Usage: report aging [Semester]");
        }

        String reportType = words[1].trim().toLowerCase();
        // Extract optional semester argument if it exists
        String targetSem = (words.length == 3) ? words[2].trim() : "";

        return new ReportCommand(reportType, targetSem);
    }

    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        if (!reportType.equalsIgnoreCase("aging")) {
            ui.showMessage("Invalid report type. Currently supported: report aging");
            return;
        }

        AcademicSemester targetSem;
        try {
            if (!targetSemStr.isEmpty()) {
                targetSem = new AcademicSemester(targetSemStr);
            } else {
                targetSem = EquipmentMaster.getCurrentSemester();
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
                String msg = String.format("%d. %s (Qty: %d, Bought: %s) | Age: %.1f Years | Status: [REPLACE SOON]", (i + 1), eq.getName(), eq.getQuantity(), purchaseSem, age);
                ui.showMessage(msg);
            }

        }

        if (!foundAging) {
            ui.showMessage("Great news! No equipment needs replacement for this semester.");
        }
    }
}