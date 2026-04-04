package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents an abstract command in the EquipmentMaster application.
 * All concrete command classes should extend this class and implement
 * the {@code execute} method.
 */
public abstract class Command {

    // 1. LOGGING: Base logger could be used for general command-related traces
    private static final Logger logger = Logger.getLogger(Command.class.getName());

    protected Logger getLogger() {
        return logger;
    }

    /**
     * Executes the command using the provided application context.
     *
     * @param context The {@code Context} object containing all necessary global states
     *     (e.g., equipment list, module list, UI, storage, and current semester).
     * @throws EquipmentMasterException If an error specific to the command's execution occurs.
     */
    public abstract void execute(Context context) throws EquipmentMasterException;
    /**
     * Indicates whether this command should terminate the application.
     * By default, commands do not exit the program.
     *
     * @return {@code true} if the command exits the program, otherwise {@code false}.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Optional: A helper method for subclasses to log execution starts consistently.
     * This reduces code duplication across different Command subclasses.
     */
    protected void logExecution(String commandName) {
        logger.log(Level.INFO, "Executing " + getClass().getSimpleName());
    }
}
