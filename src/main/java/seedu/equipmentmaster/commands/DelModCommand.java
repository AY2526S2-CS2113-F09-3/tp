package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.exception.EquipmentMasterException;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
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
     * Removes the module from the ModuleList and saves the updated list to storage.
     *
     * @param equipmentList The list of equipments (not used in this command).
     * @param moduleList    The list of course modules.
     * @param ui            The user interface.
     * @param storage       The storage to save data to the hard disk.
     * @throws EquipmentMasterException If the specified module is not found.
     */
    @Override
    public void execute(EquipmentList equipmentList, ModuleList moduleList, Ui ui, Storage storage)
            throws EquipmentMasterException {
        // 1. Delete the module. If it doesn't exist, this throws an EquipmentMasterException.
        moduleList.deleteModule(moduleName);

        // 2. Print success message to the console.
        System.out.println("Successfully deleted module: " + moduleName);

        // 3. Save the updated list to the local text file.
        try {
            storage.saveModules(moduleList);
        } catch (EquipmentMasterException e) {
            System.out.println(e.getMessage());
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
