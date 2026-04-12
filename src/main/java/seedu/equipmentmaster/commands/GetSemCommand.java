//@@author Hongyu1231
package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a command to retrieve and display the system's current academic semester.
 * Provides a helpful tip if the system is still running on the unconfigured default state.
 */
public class GetSemCommand extends Command {
    private static final Logger logger = Logger.getLogger(GetSemCommand.class.getName());

    /**
     * Executes the get semester command.
     * Retrieves the current global academic semester from the context and formats
     * the output based on whether the user has explicitly configured the settings.
     *
     * @param context The application context containing the global semester state, UI, and Storage.
     */
    @Override
    public void execute(Context context) {
        assert context != null : "Context should not be null during execution";
        logExecution("GetSemCommand");

        Ui ui = context.getUi();
        AcademicSemester current = context.getCurrentSemester();
        Storage storage = context.getStorage();

        if (current == null) {
            logger.log(Level.WARNING, "GetSemCommand executed but semester is uninitialized.");
            ui.showMessage("The system time has not been initialized yet.");
            return;
        }

        logger.log(Level.INFO, "Successfully displayed current semester: " + current);

        /*
         * Rationale for the file existence check:
         * We check if the settings file exists instead of checking if the string is "AY2024/25 Sem1".
         * If the user explicitly sets the semester to "AY2024/25 Sem1" using 'setsem',
         * the settings file will be created. In that case, it is no longer the "System Default",
         * and the tip should be hidden to avoid confusing the user.
         */
        if (storage != null && !storage.hasSettingsFile()) {
            ui.showMessage("The current system time is: " + current + " (System Default)");
            ui.showMessage("Tip: You can update this to your actual current semester using the 'setsem' command.");
        } else {
            // Settings file exists, meaning the user has explicitly configured the semester.
            ui.showMessage("The current system time is: " + current);
        }
    }
}
