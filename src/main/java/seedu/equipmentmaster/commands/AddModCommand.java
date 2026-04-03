//@@author Hongyu1231

package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
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
     * Creates a new course module, adds it to the system, and persists the updated module list to storage.
     *
     * @param context The application context containing the module list, UI, and storage.
     */
    @Override
    public void execute(Context context) {
        ModuleList moduleList = context.getModuleList();
        Ui ui = context.getUi();
        Storage storage = context.getStorage();

        try {
            Module newModule = new Module(moduleName, pax);
            moduleList.addModule(newModule);
            ui.showMessage("Successfully added module: " + newModule);

            try {
                if (storage != null) {
                    storage.saveModules(moduleList);
                }
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
            throw new EquipmentMasterException("Invalid command format.\nExpected: addmod n/NAME pax/QTY");
        }

        String moduleName = matcher.group(1).trim();
        // SECURITY CHECK: Prevent Delimiter Collision in Storage
        if (moduleName.contains("|") || moduleName.contains(",") || moduleName.contains("=")) {
            throw new EquipmentMasterException(
                    "Invalid name! Names cannot contain reserved storage characters: '|', ',', or '='"
            );
        }
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
