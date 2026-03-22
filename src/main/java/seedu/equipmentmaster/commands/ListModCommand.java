package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.exception.EquipmentMasterException;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.storage.Storage;

/**
 * Represents a command to list all current course modules tracked in the system.
 */
public class ListModCommand extends Command {

    /**
     * Constructs a {@code ListModCommand}.
     * This command requires no additional arguments to execute.
     */
    public ListModCommand() {
        // No initialization required
    }

    /**
     * Executes the list module command.
     * Retrieves all modules from the underlying ModuleList and prints them to the console.
     * If the list is empty, it notifies the user accordingly.
     *
     * @param equipmentList The list of equipments (not used in this command).
     * @param moduleList    The list of course modules to be displayed.
     * @param ui            The user interface for displaying messages.
     * @param storage       The storage component (not used since no data is modified).
     * @throws EquipmentMasterException If an error occurs during execution.
     */
    @Override
    public void execute(EquipmentList equipmentList, ModuleList moduleList, Ui ui, Storage storage)
            throws EquipmentMasterException {
        // 1. Check if the module list is empty
        if (moduleList.getModules().isEmpty()) {
            System.out.println("There are currently no modules tracked in the system.");
            return;
        }

        // 2. Print the header
        System.out.println("Here are the current course modules in your system:");

        // 3. Iterate through the list and print each module with an index number
        int index = 1;
        for (Module m : moduleList.getModules()) {
            System.out.println(index + ". " + m.toString());
            index++;
        }
    }
}
