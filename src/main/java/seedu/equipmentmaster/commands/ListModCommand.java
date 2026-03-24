// @@author Hongyu1231
package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.ui.Ui;

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
     * Displays all currently tracked course modules and their respective enrollment numbers.
     *
     * @param context The application context containing the module list and UI.
     * @throws EquipmentMasterException If an error occurs while formatting the output.
     */
    @Override
    public void execute(Context context) throws EquipmentMasterException {
        ModuleList moduleList = context.getModuleList();
        Ui ui = context.getUi();

        // 1. Check if the module list is empty
        if (moduleList.getModules().isEmpty()) {

            ui.showMessage("There are currently no modules tracked in the system.");
            return;
        }

        // 2. Print the header
        ui.showMessage("Here are the current course modules in your system:");

        // 3. Iterate through the list and print each module with an index number
        int index = 1;
        for (Module m : moduleList.getModules()) {
            ui.showMessage(index + ". " + m.toString());
            index++;
        }
    }
}
