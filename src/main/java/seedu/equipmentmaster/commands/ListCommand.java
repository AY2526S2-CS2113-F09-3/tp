package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.ui.UiTable;
import seedu.equipmentmaster.ui.UiTableRow;

import java.util.stream.IntStream;

public class ListCommand extends Command {

    public ListCommand() {
    }

    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        UiTable table = new UiTable();

        IntStream.range(0, equipments.getSize())
                .mapToObj(i -> new UiTableRow(equipments.getEquipment(i)))
                .forEach(table::addRow);

        ui.showMessage("Here is the equipment log:");
        ui.showMessage(table.toString().trim());
    }
}
