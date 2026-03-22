package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

public class SetMinCommand extends Command {
    private final String name;
    private final int minQty;

    public SetMinCommand(String name, int minQty) {
        this.name = name;
        this.minQty = minQty;
    }

    @Override
    public void execute(EquipmentList equipments, ModuleList moduleList, Ui ui, Storage storage)
            throws EquipmentMasterException {
        Equipment target = equipments.findByName(name);
        if (target == null) {
            throw new EquipmentMasterException("Equipment not found: " + name);
        }

        target.setMinQuantity(minQty);
        ui.showMessage("Success: Minimum threshold for " + name + " set to " + minQty);

        if (target.getAvailable() < target.getMinQuantity()) {
            ui.showMessage("Warning: Item is currently below this new threshold!");
        }
        storage.save(equipments.getAllEquipments());
    }

    public static Command parse(String command) throws EquipmentMasterException {
        try {
            String name = command.split("n/")[1].split("min/")[0].trim();
            int min = Integer.parseInt(command.split("min/")[1].trim());
            return new SetMinCommand(name, min);
        } catch (Exception e) {
            throw new EquipmentMasterException("Invalid format! Use: setmin n/NAME min/QUANTITY");
        }
    }
}
