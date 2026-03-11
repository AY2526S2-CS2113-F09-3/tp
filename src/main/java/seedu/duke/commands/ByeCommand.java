package seedu.duke.commands;

import seedu.duke.equipmentlist.EquipmentList;
import seedu.duke.storage.Storage;
import seedu.duke.ui.Ui;

public class ByeCommand extends Command {

    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        ui.showGoodByeMessage();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
