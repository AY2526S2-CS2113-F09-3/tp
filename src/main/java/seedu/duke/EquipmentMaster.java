package seedu.duke;


import seedu.duke.commands.Command;
import seedu.duke.equipmentlist.EquipmentList;
import seedu.duke.exception.EquipmentMasterException;
import seedu.duke.parser.Parser;
import seedu.duke.storage.Storage;
import seedu.duke.ui.Ui;

public class EquipmentMaster {
    private static Storage storage;
    private Ui ui;
    private EquipmentList equipments;

    public EquipmentMaster() {
        //storage = new Storage(FILE_PATH);
        ui = new Ui();
        equipments = new EquipmentList();
    }


    public void run() {
        ui.showWelcomeMessage();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(equipments, ui, storage);
                isExit = c.isExit();
            } catch (EquipmentMasterException e) {
                ui.showMessage(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    public static void main(String[] args) throws EquipmentMasterException{
        new EquipmentMaster().run();
    }
}
