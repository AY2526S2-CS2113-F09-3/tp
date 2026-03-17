package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.parser.Parser;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.ui.UiTable;
import seedu.equipmentmaster.ui.UiTableRow;

import java.util.stream.IntStream;

public class HelpCommand extends Command {

    public HelpCommand() {
    }

    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        UiTable table = new UiTable(true);
        table.addRow(new UiTableRow("Command","Format"));
        for(Parser.CommandSpec spec: Parser.getCommandSpecs()){
            table.addRow(new UiTableRow(spec.getKeyword(),spec.getFormat()));
        }

        ui.showMessage("Here are the available commands:");
        ui.showMessage(table.toString().trim());
    }
}
