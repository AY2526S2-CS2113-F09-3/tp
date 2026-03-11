package seedu.duke.commands;


import seedu.duke.equipment.Equipment;
import seedu.duke.equipmentlist.EquipmentList;
import seedu.duke.storage.Storage;
import seedu.duke.ui.Ui;

public class AddCommand extends Command{
    private final String name;
    private final int quantity;

    public AddCommand(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        Equipment equipment = new Equipment(name, quantity);
        equipments.addEquipment(equipment);
        //storage.Save(tasks);
        ui.showMessage("Added " + quantity + " of " + name + ". (Total Available: " + equipment.getAvailable() + ")" );
    }
}
