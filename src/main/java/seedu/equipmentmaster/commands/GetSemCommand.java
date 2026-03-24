// @@author Hongyu1231
package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.semester.AcademicSemester;

/**
 * Command to display the current global academic semester set in the system.
 */
public class GetSemCommand extends Command {

    /**
     * Executes the get semester command.
     * Retrieves and displays the current global academic semester from the application context.
     *
     * @param context The application context containing the global semester state and UI.
     */
    @Override
    public void execute(Context context) {
        Ui ui = context.getUi();
        AcademicSemester current = context.getCurrentSemester();

        if (current == null) {
            ui.showMessage("The system time has not been initialized yet.");
        } else {
            ui.showMessage("The current system time is: " + current);
        }
    }
}
