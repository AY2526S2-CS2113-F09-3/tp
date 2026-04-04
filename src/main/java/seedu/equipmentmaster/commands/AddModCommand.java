package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a command to add a new course module to the system.
 * This command handles the validation of module data and updates both memory and storage.
 */
public class AddModCommand extends Command {
    private static final Logger logger = Logger.getLogger(AddModCommand.class.getName());
    private final String moduleName;
    private final int pax;

    /**
     * Constructs an {@code AddModCommand} with the specified module name and enrollment.
     *
     * @param moduleName The unique name/code of the module (e.g., CG2111A).
     * @param pax The number of students enrolled in this module.
     * @throws EquipmentMasterException If the pax value is negative.
     */
    public AddModCommand(String moduleName, int pax) throws EquipmentMasterException {
        if (pax < 0) {
            throw new EquipmentMasterException("Pax cannot be a negative number.");
        }
        this.moduleName = moduleName;
        this.pax = pax;
    }

    /**
     * Executes the command to add a module to the system.
     * It checks for duplicate modules, updates the internal list, and triggers a storage save.
     *
     * @param context The application context containing lists, UI, and storage.
     * @throws EquipmentMasterException If a module with the same name already exists.
     */
    @Override
    public void execute(Context context) throws EquipmentMasterException {
        assert context != null : "Context should not be null during execution";
        logExecution("AddModCommand");

        ModuleList moduleList = context.getModuleList();

        if (moduleList.hasModule(moduleName)) {
            logger.log(Level.WARNING, "Duplicate module addition attempted: " + moduleName);
            throw new EquipmentMasterException("Module '" + moduleName + "' already exists!");
        }

        Module newModule = new Module(moduleName, pax);
        moduleList.addModule(newModule);

        context.getUi().showMessage("Successfully added module: " + newModule);
        saveToStorage(context.getStorage(), moduleList, context.getUi());
    }

    /**
     * Saves the updated module list to the local data file.
     * Any storage-related exceptions are caught and displayed as warnings to the user.
     *
     * @param storage The storage handler used to write data.
     * @param moduleList The current list of modules to be saved.
     * @param ui The user interface for displaying error messages.
     */
    private void saveToStorage(Storage storage, ModuleList moduleList, Ui ui) {
        try {
            if (storage != null) {
                storage.saveModules(moduleList);
            }
        } catch (EquipmentMasterException e) {
            logger.log(Level.SEVERE, "Storage synchronization failed", e);
            ui.showMessage("Warning: Module added to memory but failed to save to disk.");
        }
    }

    /**
     * Parses the user input to create a valid {@code AddModCommand}.
     * The input should follow the format: addmod n/NAME pax/PAX.
     *
     * @param fullCommand The complete command string entered by the user.
     * @return An instance of {@code AddModCommand} ready for execution.
     * @throws EquipmentMasterException If the format is invalid or values fail validation.
     */
    public static AddModCommand parse(String fullCommand) throws EquipmentMasterException {
        logger.log(Level.INFO, "Parsing AddModCommand parameters.");
        String args = fullCommand.replaceFirst("(?i)^addmod\\s*", "").trim();

        Pattern pattern = Pattern.compile("n/(.+?)\\s+pax/(.+)");
        Matcher matcher = pattern.matcher(args);

        if (!matcher.matches()) {
            throw new EquipmentMasterException("Invalid command format.\nExpected: addmod n/NAME pax/QTY");
        }

        String name = matcher.group(1).trim();
        validateName(name);

        String paxString = matcher.group(2).trim();
        try {
            int pax = Integer.parseInt(paxString);
            return new AddModCommand(name, pax);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid pax input: " + paxString);
            throw new EquipmentMasterException("Invalid pax value. Please enter a valid integer.");
        }
    }

    /**
     * Validates the provided module name against reserved characters.
     *
     * @param name The name string to be validated.
     * @throws EquipmentMasterException If the name is empty or contains '|', ',', or '='.
     */
    private static void validateName(String name) throws EquipmentMasterException {
        if (name.isEmpty()) {
            throw new EquipmentMasterException("Module name cannot be empty.");
        }
        if (name.contains("|") || name.contains(",") || name.contains("=")) {
            throw new EquipmentMasterException("Name contains reserved characters: '|', ',', '='");
        }
    }
}
