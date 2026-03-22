package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.exception.EquipmentMasterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.storage.Storage;

/**
 * Represents a command to update the enrollment number (pax) of an existing course module.
 */
public class UpdateModCommand extends Command {
    private final String moduleName;
    private final int newPax;

    /**
     * Constructs an {@code UpdateModCommand} with the specified module name and new pax.
     *
     * @param moduleName The name of the module to be updated.
     * @param newPax     The new student enrollment number.
     */
    public UpdateModCommand(String moduleName, int newPax) {
        this.moduleName = moduleName;
        this.newPax = newPax;
    }

    /**
     * Executes the update module command.
     * Updates the pax in the underlying ModuleList and saves the updated list to storage.
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
        // 1. Update the module. If it doesn't exist, this will throw an EquipmentMasterException.
        moduleList.updateModule(moduleName, newPax);

        // 2. Print success message to the console.
        ui.showMessage("Successfully updated module:");
        ui.showMessage(moduleName + " | Enrollment: " + newPax + " students");

        // 3. Save the updated list to the local text file.
        try {
            storage.saveModules(moduleList);
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
        }
    }

    /**
     * Parses the full command string provided by the user to create an {@code UpdateModCommand}.
     * Extracts the module name and the updated pax (enrollment number) using regular expressions.
     *
     * @param fullCommand The complete user input string (e.g., "updatemod n/CG2111A pax/180").
     * @return An {@code UpdateModCommand} initialized with the parsed module name and new pax.
     * @throws EquipmentMasterException If the command format is invalid or the pax is not an integer.
     */
    public static UpdateModCommand parse(String fullCommand) throws EquipmentMasterException {
        // Strip the starting command word to isolate the arguments
        String args = fullCommand.replaceFirst("(?i)^updatemod\\s*", "").trim();

        Pattern pattern = Pattern.compile("n/(.+?)\\s+pax/(.+)");
        Matcher matcher = pattern.matcher(args);

        if (!matcher.matches()) {
            throw new EquipmentMasterException("Invalid command format. \nExpected: updatemod n/NAME pax/QTY");
        }

        String moduleName = matcher.group(1).trim();
        String paxString = matcher.group(2).trim();

        try {
            int pax = Integer.parseInt(paxString);
            if (pax < 0) {
                throw new EquipmentMasterException("Pax cannot be a negative number.");
            }
            return new UpdateModCommand(moduleName, pax);
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Invalid pax value. Please enter a valid integer.");
        }
    }
}
