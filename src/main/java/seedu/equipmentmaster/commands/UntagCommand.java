package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;


/**
 * Removes a specific equipment requirement from a module.
 */
public class UntagCommand extends Command {
    private final String moduleName;
    private final String equipmentName;

    public UntagCommand(String moduleName, String equipmentName) {
        this.moduleName = moduleName;
        this.equipmentName = equipmentName;
    }

    /**
     * Parses the raw command string and returns a ready-to-execute UntagCommand.
     *
     * @param fullCommand The raw input from the user (e.g., "untag m/CG2111A n/STM32").
     * @return A constructed UntagCommand object.
     * @throws EquipmentMasterException If the format or prefixes are invalid.
     */
    public static UntagCommand parse(String fullCommand) throws EquipmentMasterException {
        int mIndex = fullCommand.indexOf("m/");
        int nIndex = fullCommand.indexOf("n/");

        if (mIndex == -1 || nIndex == -1) {
            throw new EquipmentMasterException(
                    "Invalid command format. \nExpected: untag m/MOD_NAME n/EQ_NAME"
            );
        }

        if (!(mIndex < nIndex)) {
            throw new EquipmentMasterException(
                    "Please provide the arguments in the correct order: m/MOD_NAME n/EQ_NAME"
            );
        }

        // Extract and trim the values
        String parsedModuleName = fullCommand.substring(mIndex + 2, nIndex).trim();
        // Since there is no req/, equipmentName goes to the end of the string
        String parsedEquipmentName = fullCommand.substring(nIndex + 2).trim();

        if (parsedModuleName.isEmpty() || parsedEquipmentName.isEmpty()) {
            throw new EquipmentMasterException(
                    "Module name and equipment name cannot be empty."
            );
        }

        return new UntagCommand(parsedModuleName, parsedEquipmentName);
    }

    @Override
    public void execute(Context context) throws EquipmentMasterException {
        ModuleList modules = context.getModuleList();
        EquipmentList equipments = context.getEquipments();
        Ui ui = context.getUi();
        Storage storage = context.getStorage();

        // 1. Double Ghost Reference Check
        boolean moduleExists = modules.hasModule(moduleName);
        boolean equipmentExists = equipments.hasEquipment(equipmentName);

        if (!moduleExists && !equipmentExists) {
            throw new EquipmentMasterException("Aborted: Neither the module '" + moduleName +
                    "' nor the equipment '" + equipmentName + "' exists.");
        } else if (!moduleExists) {
            throw new EquipmentMasterException("Aborted: Module '" + moduleName + "' does not exist.");
        } else if (!equipmentExists) {
            throw new EquipmentMasterException("Aborted: Equipment '" + equipmentName + "' does not exist.");
        }

        // 2. Retrieve the objects
        Module targetModule = modules.getModule(moduleName);
        Equipment targetEquipment = equipments.findByName(equipmentName);

        // Grab the official capitalized name so it matches exactly what's inside the HashMap
        String officialEquipmentName = targetEquipment.getName();

        // 3. Remove the dependency
        boolean isRemoved = targetModule.removeEquipmentRequirement(officialEquipmentName);

        // 4. Output the result
        if (isRemoved) {
            ui.showMessage("Successfully untagged equipment from module:\n" +
                    targetModule.getName() + " no longer requires " + officialEquipmentName + ".");
        } else {
            // Throw an exception if they tried to untag something that wasn't tagged in the first place
            throw new EquipmentMasterException("Aborted: " + targetModule.getName() +
                    " does not currently have " + officialEquipmentName + " as a requirement.");
        }

        try {
            storage.saveModules(modules);
        } catch (EquipmentMasterException e) {
            ui.showMessage("Warning: Failed to save the updated tags to the data file. " + e.getMessage());
        }
    }
}
