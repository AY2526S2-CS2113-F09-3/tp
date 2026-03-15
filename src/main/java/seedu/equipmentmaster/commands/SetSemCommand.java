package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.EquipmentMaster;

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
     * Executes the command to update the global system semester.
     * @param equipments The list of equipment (not used in this command).
     * @param ui The user interface to display messages.
     * @param storage The storage handler used to persist the updated semester.
     */
    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        try {
            // Assertion: Parser should have already verified that rawSem is not null.
            assert rawSem != null : "Semester string in SetSemCommand should not be null";
            AcademicSemester newSem = new AcademicSemester(rawSem);
            EquipmentMaster.setCurrentSemester(newSem);
            storage.saveSettings(newSem);
            ui.showMessage("System time updated. Current academic semester is now set to " + newSem);
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
        }
    }
}
