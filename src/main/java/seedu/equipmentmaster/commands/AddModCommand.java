package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a command to add a new course module to the system.
 */
public class AddModCommand extends Command {
    private final String moduleName;
    private final int pax;

    /**
     * Constructs an {@code AddModCommand} with the extracted module name and pax.
     *
     * @param moduleName The name of the new module.
     * @param pax        The student enrollment number.
     * @throws EquipmentMasterException If the pax is a negative number.
     */
    public AddModCommand(String moduleName, int pax) throws EquipmentMasterException {
        if (pax < 0) {
            throw new EquipmentMasterException("Pax cannot be a negative number.");
        }
        this.moduleName = moduleName;
        this.pax = pax;
    }

    /**
     * Executes the add module command.
     * Adds the module to the provided ModuleList and displays a success message via the Ui.
     *
     * @param moduleList The list of modules to be updated.
     * @param ui         The user interface to display output.
     */
    @Override
    public void execute(EquipmentList equipments, ModuleList moduleList, Ui ui, Storage storage) {
        try {
            Module newModule = new Module(moduleName, pax);
            moduleList.addModule(newModule);
            ui.showMessage("Successfully added module: " + newModule);

            try {
                storage.saveModules(moduleList);
            } catch (EquipmentMasterException e) {
                ui.showMessage("Warning: Failed to save the new module to the data file. " + e.getMessage());
            }

        } catch (EquipmentMasterException e){
            ui.showMessage(e.getMessage());
        }
    }

    /**
     * Parses the full command string provided by the user to create an {@code AddModCommand}.
     * Extracts the module name and the pax (enrollment number) using regular expressions.
     *
     * @param fullCommand The complete user input string (e.g., "addmod n/CG2111A pax/150").
     * @return An {@code AddModCommand} initialized with the parsed module name and pax.
     * @throws EquipmentMasterException If the command format is invalid or the pax is not an integer.
     */
    public static AddModCommand parse(String fullCommand) throws EquipmentMasterException {
        // Strip the starting command word to isolate the arguments
        String args = fullCommand.replaceFirst("(?i)^addmod\\s*", "").trim();

        Pattern pattern = Pattern.compile("n/(.+?)\\s+pax/(.+)");
        Matcher matcher = pattern.matcher(args);

        if (!matcher.matches()) {
            throw new EquipmentMasterException("Invalid command format. \nExpected: addmod n/NAME pax/QTY");
        }

        String moduleName = matcher.group(1).trim();
        String paxString = matcher.group(2).trim();

        // Add this explicit check to prevent empty module names
        if (moduleName.isEmpty()) {
            throw new EquipmentMasterException("Module name cannot be empty. " +
                    "Please provide a valid name (e.g., n/CG2111A).");
        }

        try {
            int pax = Integer.parseInt(paxString);
            return new AddModCommand(moduleName, pax);
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Invalid pax value. Please enter a valid integer (e.g., pax/150).");
        }
    }
}
