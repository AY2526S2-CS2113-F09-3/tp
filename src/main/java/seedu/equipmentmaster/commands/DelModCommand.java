// @@author Hongyu1231
package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.storage.Storage;

/**
 * Represents a command to delete an existing course module from the system.
 */
public class DelModCommand extends Command {
    private final String moduleName;

    /**
     * Constructs a {@code DelModCommand} with the specified module name.
     *
     * @param moduleName The name of the module to be deleted.
     */
    public DelModCommand(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * Executes the delete module command.
     * Removes the specified course module from the system and updates the storage file.
     *
     * @param context The application context containing the module list, UI, and storage.
     * @throws EquipmentMasterException If the module is not found or saving fails.
     */
    @Override
    public void execute(Context context) throws EquipmentMasterException {
        ModuleList moduleList = context.getModuleList();
        Ui ui = context.getUi();
        Storage storage = context.getStorage();

        // 1. Delete the module. If it doesn't exist, this throws an EquipmentMasterException.
        moduleList.deleteModule(moduleName);

        // 2. Print success message to the console.
        ui.showMessage("Successfully deleted module: " + moduleName);

        // 3. Save the updated list to the local text file.
        try {
            storage.saveModules(moduleList);
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
        }
    }

    /**
     * Parses the full command string provided by the user to create a {@code DelModCommand}.
     * Extracts the target module name to be deleted.
     *
     * @param fullCommand The complete user input string (e.g., "delmod n/CG2111A").
     * @return A {@code DelModCommand} initialized with the target module name.
     * @throws EquipmentMasterException If the command format is invalid or the module name is missing.
     */
    public static DelModCommand parse(String fullCommand) throws EquipmentMasterException {
        // Strip the starting command word to isolate the arguments
        String args = fullCommand.replaceFirst("(?i)^delmod\\s*", "").trim();

        if (!args.startsWith("n/")) {
            throw new EquipmentMasterException("Invalid command format. \nExpected: delmod n/NAME");
        }

        String moduleName = args.replace("n/", "").trim();

        if (moduleName.isEmpty()) {
            throw new EquipmentMasterException("Module name cannot be empty.");
        }

        return new DelModCommand(moduleName);
    }
}
