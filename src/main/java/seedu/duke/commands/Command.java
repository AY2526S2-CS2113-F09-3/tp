package seedu.duke.commands;


import seedu.duke.equipmentlist.EquipmentList;
import seedu.duke.exception.EquipmentMasterException;
import seedu.duke.storage.Storage;
import seedu.duke.ui.Ui;

public abstract class Command {
    public abstract void execute(EquipmentList equipments, Ui ui, Storage storage)
            throws EquipmentMasterException;

    public boolean isExit() {
        return false;
    }
}
