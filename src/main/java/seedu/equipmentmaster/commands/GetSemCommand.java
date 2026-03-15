package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.EquipmentMaster;

/**
 * Command to display the current global academic semester set in the system.
 */
public class GetSemCommand extends Command {

    /**
     * Executes the command to fetch and display the current system semester.
     *
     * @param equipments The list of equipment (not used in this command).
     * @param ui The user interface used to display the output.
     * @param storage The storage handler (not used in this command).
     */
    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        AcademicSemester current = EquipmentMaster.getCurrentSemester();

        if (current == null) {
            ui.showMessage("The system time has not been initialized yet.");
        } else {
            ui.showMessage("The current system time is: " + current.toString());
        }
    }
}
