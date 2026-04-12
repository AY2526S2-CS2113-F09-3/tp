package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.modulelist.ModuleList;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Generates specific reports for the equipment inventory.
 */
public class ReportCommand extends Command {
    private static final Logger logger = Logger.getLogger(ReportCommand.class.getName());

    private final String reportType;
    private final String targetSemStr;


    public ReportCommand(String reportType, String targetSemStr) {
        this.reportType = reportType;
        this.targetSemStr = targetSemStr;
    }

    /**
     * Parses the arguments for the 'report' command.
     *
     * @param fullCommand The complete input string.
     * @return A ReportCommand object.
     * @throws EquipmentMasterException If arguments are missing.
     */
    public static ReportCommand parse(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.trim().split("\\s+", 3);

        if (words.length < 2) {
            throw new EquipmentMasterException("Please specify the report type. Usage: report aging [Semester] OR " +
                    "report lowstock OR report procurement");
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
        assert context != null : "Context should not be null during execution";

        Ui ui = context.getUi();
        EquipmentList equipments = context.getEquipments();

        if (reportType.equalsIgnoreCase("lowstock")) {
            executeLowStockReport(equipments, ui);
        } else if (reportType.equalsIgnoreCase("aging")) {
            executeAgingReport(equipments, ui, context);
        } else if (reportType.equalsIgnoreCase("procurement")) {
            executeProcurementReport(context);
        } else {
            ui.showMessage("Invalid report type. Currently supported: aging, lowstock, procurement.");
        }
    }

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

    //@@author Hongyu1231

    /**
     * Executes the aging report generation.
     * Identifies and displays equipment that has reached or exceeded its designated lifespan.
     *
     * @param equipments The inventory list to check.
     * @param ui         The UI handler for displaying the report.
     * @param context    The application context containing global states.
     */
    private void executeAgingReport(EquipmentList equipments, Ui ui, Context context) {
        assert equipments != null : "EquipmentList must not be null";
        assert ui != null : "Ui must not be null";
        assert context != null : "Context must not be null";

        AcademicSemester targetSem;
        try {
            targetSem = resolveTargetSemester(context);
        } catch (EquipmentMasterException e) {
            logger.log(Level.WARNING, "Failed to resolve semester for aging report.", e);
            ui.showMessage(e.getMessage());
            return;
        }

        logger.log(Level.INFO, "Generating Aging Report for semester: " + targetSem);
        ui.showMessage("Aging Equipment Report (Calculated for: " + targetSem + "):");

        // SLAP: Extract the iteration and printing logic to a helper method
        int agingCount = displayAgingEquipments(equipments, ui, targetSem);

        if (agingCount == 0) {
            logger.log(Level.INFO, "No aging equipment found.");
            ui.showMessage("Great news! No equipment needs replacement for this semester.");
        }
    }

    /**
     * Resolves the target semester for the aging report.
     * Uses the user-provided semester string if available,
     * otherwise defaults to the system's current semester.
     *
     * @param context The application context.
     * @return The determined AcademicSemester.
     * @throws EquipmentMasterException If the user didn't provide a semester
     *                                  and the system semester is not set.
     */
    private AcademicSemester resolveTargetSemester(Context context)
            throws EquipmentMasterException {
        if (targetSemStr != null && !targetSemStr.trim().isEmpty()) {
            return new AcademicSemester(targetSemStr.trim());
        }

        AcademicSemester current = context.getCurrentSemester();
        if (current == null) {
            throw new EquipmentMasterException("System semester not set! " +
                    "Use 'setsem' first or provide a semester.");
        }
        return current;
    }

    /**
     * Iterates through the equipment list and prints those that have reached their lifespan.
     *
     * @param equipments The inventory list to check.
     * @param ui         The UI handler.
     * @param targetSem  The semester against which the age is calculated.
     * @return The number of aging equipments found.
     */
    private int displayAgingEquipments(EquipmentList equipments, Ui ui, AcademicSemester targetSem) {
        int agingCount = 0;

        for (int i = 0; i < equipments.getSize(); i++) {
            Equipment eq = equipments.getEquipment(i);
            AcademicSemester purchaseSem = eq.getPurchaseSem();
            double lifespan = eq.getLifespanYears();

            // Guard clause: Skip items without purchase date or lifespan setup
            if (purchaseSem == null || lifespan <= 0) {
                continue;
            }

            double age = 0;
            try {
                age = purchaseSem.calculateAgeInYears(targetSem);
            } catch (EquipmentMasterException e) {
                ui.showMessage(String.format("Warning: Skipping equipment '%s' due to invalid purchase semester data.",
                        eq.getName()));
                continue;
            }
            if (age >= lifespan) {
                agingCount++;
                String msg = String.format("%d. %s (Qty: %d, Bought: %s) | " +
                                "Age: %.1f Years | Status: [REPLACE SOON]",
                        agingCount, eq.getName(), eq.getQuantity(), purchaseSem, age);
                ui.showMessage(msg);
            }
        }

        return agingCount;
    }
    //@@author

    private void executeProcurementReport(Context context) {
        Ui ui = context.getUi();
        EquipmentList equipments = context.getEquipments();
        ModuleList moduleList = context.getModuleList();

        ui.showMessage("Procurement Report (Current Sem: " + context.getCurrentSemester() + ")");

        int index = 1;
        boolean foundProcurementNeeded = false;

        for (int i = 0; i < equipments.getSize(); i++) {
            Equipment eq = equipments.getEquipment(i);
            ArrayList<String> relatedModules = eq.getModuleCodes();

            int baseDemand = 0;

            for (String modCode : relatedModules) {
                Module module = moduleList.getModule(modCode);
                if (module != null) {
                    //This is not efficient due to the copying of the getER()
                    var requirements = module.getEquipmentRequirements();
                    if (!requirements.containsKey(eq.getName())) {
                        ui.showMessage("Warning: Equipment '" + eq.getName() +
                                "' is tagged to module '" + module.getName() +
                                "' but not found in its equipment requirements. " +
                                "Skipping this module for demand calculation.");
                    } else {
                        double ratio = requirements.get(eq.getName());
                        baseDemand += (int) Math.ceil(module.getPax() * ratio);
                    }
                } else {
                    ui.showMessage("Warning: Module '" + modCode + "' tagged to equipment '" + eq.getName() +
                            "' not found in module list. Skipping this module for demand calculation.");
                }
            }

            // Only proceed if there is actual demand
            if (baseDemand > 0) {
                double bufferPercentage = eq.getBufferPercentage();
                double bufferedDemand = baseDemand * (1.0 + (bufferPercentage / 100.0));

                // Indivisibility Rule: round up to nearest whole number
                int totalRequired = (int) Math.ceil(bufferedDemand);

                int available = eq.getQuantity();
                int toBuy = totalRequired - available;

                if (toBuy > 0) {
                    foundProcurementNeeded = true;
                    int bufferAmt = totalRequired - baseDemand;
                    String bufferStr = String.format("%.0f%%", bufferPercentage);

                    ui.showMessage(index + ". " + eq.getName());
                    ui.showMessage("   - Base Need: " + baseDemand + " (from " +
                            String.join(", ", relatedModules) + ")");
                    ui.showMessage("   - Buffer: " + bufferStr + " (+" + bufferAmt + ")");
                    ui.showMessage("   - Total Required: " + totalRequired + " | Available: " + available
                            + " | TO BUY: " + toBuy);
                    index++;
                }
            }
        }

        if (!foundProcurementNeeded) {
            ui.showMessage("Great news! No procurement needed based on current module requirements.");
        }
    }
}

