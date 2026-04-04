package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.storage.Storage;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a command to delete an existing course module from the system.
 */
public class DelModCommand extends Command {
    private static final Logger logger = Logger.getLogger(DelModCommand.class.getName());
    private static final String FLAG_NAME = "n/";

    // Using regex for parsing to maintain consistency with other command classes
    private static final Pattern COMMAND_FORMAT = Pattern.compile("n/(.+)");

    private final String moduleName;

    /**
     * Constructs a {@code DelModCommand} with the specified module name.
     *
     * @param moduleName The name of the module to be deleted.
     */
    public DelModCommand(String moduleName) {
        assert moduleName != null && !moduleName.isEmpty() : "Module name cannot be null or empty";
        this.moduleName = moduleName;
    }

    /**
     * Executes the delete module command.
     *
     * @param context The application context.
     * @throws EquipmentMasterException If the module is not found.
     */
    @Override
    public void execute(Context context) throws EquipmentMasterException {
        assert context != null : "Context should not be null during execution";
        logExecution("DelModCommand");

        ModuleList moduleList = context.getModuleList();
        logger.log(Level.INFO, "Attempting to delete module: " + moduleName);

        // This call will throw EquipmentMasterException if the module doesn't exist
        moduleList.deleteModule(moduleName);

        context.getUi().showMessage("Successfully deleted module: " + moduleName);
        saveToStorage(context.getStorage(), moduleList, context.getUi());
    }

    /**
     * Updates the storage file with the latest module list.
     */
    private void saveToStorage(Storage storage, ModuleList moduleList, Ui ui) {
        try {
            if (storage != null) {
                storage.saveModules(moduleList);
                logger.log(Level.INFO, "Storage updated successfully.");
            }
        } catch (EquipmentMasterException e) {
            logger.log(Level.SEVERE, "Failed to save module list", e);
            ui.showMessage("Warning: Data deleted in memory but failed to save to disk.");
        }
    }

    /**
     * Parses the user input to create a {@code DelModCommand}.
     *
     * @param fullCommand The raw user input.
     * @return A DelModCommand instance.
     * @throws EquipmentMasterException If the input format is invalid.
     */
    public static DelModCommand parse(String fullCommand) throws EquipmentMasterException {
        logger.log(Level.INFO, "Parsing DelModCommand.");

        String args = fullCommand.replaceFirst("(?i)^delmod\\s*", "").trim();
        Matcher matcher = COMMAND_FORMAT.matcher(args);

        if (!matcher.matches()) {
            logger.log(Level.WARNING, "Parse failed: invalid format for delmod.");
            throw new EquipmentMasterException("Invalid command format. \nExpected: delmod n/NAME");
        }

        String moduleName = matcher.group(1).trim();
        if (moduleName.isEmpty()) {
            throw new EquipmentMasterException("Module name cannot be empty.");
        }

        return new DelModCommand(moduleName);
    }
}
