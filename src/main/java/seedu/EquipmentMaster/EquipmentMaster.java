package seedu.EquipmentMaster;


import seedu.EquipmentMaster.commands.Command;
import seedu.EquipmentMaster.equipmentlist.EquipmentList;
import seedu.EquipmentMaster.exception.EquipmentMasterException;
import seedu.EquipmentMaster.parser.Parser;
import seedu.EquipmentMaster.storage.Storage;
import seedu.EquipmentMaster.ui.Ui;

import java.util.ArrayList;

public class EquipmentMaster {
    private static Storage storage;
    private Ui ui;
    private EquipmentList equipments;

    public EquipmentMaster(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath, ui);
        this.equipments = new EquipmentList(storage.load());
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
        new EquipmentMaster("data/equipment.txt").run();
    }

    public EquipmentList getEquipmentList() {
        return this.equipments;
    }
}
