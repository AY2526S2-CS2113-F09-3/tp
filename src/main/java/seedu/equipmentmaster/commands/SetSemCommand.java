// @@author Hongyu1231
package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.semester.AcademicSemester;

/**
 * Command to update the global academic semester of the application.
 */
public class SetSemCommand extends Command {
    private final String rawSem;

    /**
     * Initializes the command with the raw semester string provided by the user.
     * @param rawSem The user input string for the new semester.
     */
    public SetSemCommand(String rawSem) {
        this.rawSem = rawSem;
    }

    /**
     * Parses the arguments for the 'setsem' command and creates a SetSemCommand object.
     *
     * @param fullCommand The complete input string containing the 'setsem' command and the semester.
     * @return A SetSemCommand object containing the target academic semester.
     * @throws EquipmentMasterException If the semester argument is missing.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.trim().split("\\s+", 2);

        // Check if the user provided the semester string after "setsem"
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new EquipmentMasterException("Please specify a semester. Usage: setsem AY[YYYY]/[YY] Sem[1/2]");
        }

        String rawSemester = words[1].trim();
        return new SetSemCommand(rawSemester);
    }

    /**
     * Executes the set semester command.
     * Updates the global system academic semester in the context and saves the new setting to the settings file.
     *
     * @param context The application context containing the global semester state, UI, and storage.
     */
    @Override
    public void execute(Context context) {
        Storage storage = context.getStorage();
        Ui ui = context.getUi();

        try {
            // Assertion: Parser should have already verified that rawSem is not null.
            if (rawSem == null || rawSem.trim().isEmpty()) {
                throw new EquipmentMasterException(
                        "Please specify a semester. Usage: setsem AY[YYYY]/[YY] Sem[1/2]");
            }
            AcademicSemester newSem = new AcademicSemester(rawSem);
            context.setCurrentSemester(newSem);
            storage.saveSettings(newSem);
            ui.showMessage("System time updated. Current academic semester is now set to " + newSem);
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
        }
    }
}
