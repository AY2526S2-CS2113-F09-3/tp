package seedu.equipmentmaster.commands;


import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

/**
 * Represents an abstract command in the EquipmentMaster application.
 * All concrete command classes should extend this class and implement
 * the {@code execute} method.
 */
public abstract class Command {
    /**
     * Executes the command using the provided equipment list,
     * user interface, and storage system.
     *
     * @param equipments The equipment list that the command operates on.
     * @param ui The user interface used for displaying messages.
     * @param storage The storage system used to save or retrieve data.
     * @throws EquipmentMasterException If an error occurs during execution.
     */
    public abstract void execute(EquipmentList equipments, Ui ui, Storage storage)
            throws EquipmentMasterException;

    /**
     * Indicates whether this command should terminate the application.
     * By default, commands do not exit the program.
     *
     * @return {@code true} if the command exits the program, otherwise {@code false}.
     */
    public boolean isExit() {
        return false;
    }
}
