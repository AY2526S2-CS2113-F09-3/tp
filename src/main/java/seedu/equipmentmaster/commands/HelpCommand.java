package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.parser.Parser;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.ui.UiTable;
import seedu.equipmentmaster.ui.UiTableRow;

/**
 * Represents a command that displays a help message listing all available commands
 * and their usage formats.
 *
 * @author XiaoGeNekidora
 */
public class HelpCommand extends Command {

    /**
     * Constructs a new HelpCommand object.
     */
    public HelpCommand() {
    }

    /**
     * Executes the help command.
     * Retrieves the list of command specifications from the Parser and displays
     * them in a formatted table via the UI.
     *
     * @param equipments The equipment list (not used by this command).
     * @param ui The user interface to display the help message.
     * @param storage The storage system (not used by this command).
     */
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
