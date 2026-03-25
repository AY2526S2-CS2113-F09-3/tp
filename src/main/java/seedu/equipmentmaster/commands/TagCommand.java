package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

public class TagCommand extends Command{
    private final String equipmentName;
    private final String moduleName;
    private final Double requirementRatio;

    public TagCommand(String moduleName, String equipmentName, Double requirementRatio) {
        this.equipmentName = equipmentName;
        this.moduleName = moduleName;
        this.requirementRatio = requirementRatio;
    }

    @Override
    public void execute(Context context) throws EquipmentMasterException {
        ModuleList modules = context.getModuleList();
        EquipmentList equipments = context.getEquipments();
        Ui ui = context.getUi();
        Storage storage = context.getStorage();
        boolean moduleExists = modules.hasModule(moduleName);
        boolean equipmentExists = equipments.hasEquipment(equipmentName);

        if (!moduleExists && !equipmentExists) {
            throw new EquipmentMasterException("Aborted: Neither the module '" +
                    moduleName + "' nor the equipment '" + equipmentName + "' exists.");
        } else if (!moduleExists) {
            throw new EquipmentMasterException("Aborted: Module '" + moduleName + "' does not exist.");
        } else if (!equipmentExists) {
            throw new EquipmentMasterException("Aborted: Equipment '" + equipmentName + "' does not exist.");
        }

        Module targetModule = modules.getModule(moduleName);
        String officialEquipmentName = equipments.findByName(equipmentName).getName();

        // Use the officialEquipmentName, NOT the moduleName!
        targetModule.addEquipmentRequirement(officialEquipmentName, requirementRatio);
        int studentsPerItem = (int) Math.round(1.0 / requirementRatio);

        ui.showMessage("Successfully linked equipment to module:\n" +
                moduleName + " now requires " + requirementRatio + " x " + equipmentName +
                " per student (1 item per " + studentsPerItem + " students).");

        try {
            storage.saveModules(modules);
        } catch (EquipmentMasterException e) {
            ui.showMessage("Warning: Failed to save the updated tags to the data file. " + e.getMessage());
        }
    }

    /**
     * Parses the raw command string and returns a ready-to-execute TagCommand.
     *
     * @param fullCommand The raw input from the user (e.g., "tag m/CG2111A n/STM32 req/0.2").
     * @return A constructed TagCommand object.
     * @throws EquipmentMasterException If the format, prefixes, or decimals are invalid.
     */
    public static TagCommand parse(String fullCommand) throws EquipmentMasterException {
        // 1. Find the starting positions of our prefixes
        int mIndex = fullCommand.indexOf("m/");
        int nIndex = fullCommand.indexOf("n/");
        int reqIndex = fullCommand.indexOf("req/");

        // 2. Validate that all prefixes exist in the string
        if (mIndex == -1 || nIndex == -1 || reqIndex == -1) {
            throw new EquipmentMasterException(
                    "Invalid command format. \nExpected: tag m/MOD_NAME n/EQ_NAME req/FRACTION"
            );
        }

        // 3. Ensure they are in the correct order so substring extraction works safely
        if (!(mIndex < nIndex && nIndex < reqIndex)) {
            throw new EquipmentMasterException(
                    "Please provide the arguments in the correct order: m/MOD_NAME n/EQ_NAME req/FRACTION"
            );
        }

        // 4. Extract and trim the values
        // moduleName is everything between "m/" and " n/"
        String moduleName = fullCommand.substring(mIndex + 2, nIndex).trim();
        // equipmentName is everything between "n/" and " req/"
        String equipmentName = fullCommand.substring(nIndex + 2, reqIndex).trim();
        // reqString is everything after "req/" to the end of the line
        String reqString = fullCommand.substring(reqIndex + 4).trim();

        // 5. Check for empty inputs (e.g., user typed "tag m/ n/STM32 req/0.2")
        if (moduleName.isEmpty() || equipmentName.isEmpty() || reqString.isEmpty()) {
            throw new EquipmentMasterException(
                    "Module name, equipment name, and requirement ratio cannot be empty."
            );
        }

        // 6. Handle the Invalid Decimals edge case (e.g., "req/0.2.5" or "req/abc")
        double requirementRatio;
        try {
            requirementRatio = Double.parseDouble(reqString);
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException(
                    "Invalid requirement ratio format. Please provide a valid decimal number (e.g., 0.2)."
            );
        }

        // 7. Return the constructed command
        return new TagCommand(moduleName, equipmentName, requirementRatio);
    }

}
