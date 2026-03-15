package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

/**
 * Represents a command to delete a specific quantity of equipment.
 * Supports targeting equipment by its index in the list or by its exact name.
 */
public class DeleteCommand extends Command {
    private String name = null;
    private int index = -1;
    private int quantity;

    /**
     * Constructor for name-based deletion.
     *
     * @param name     The exact name of the equipment.
     * @param quantity The amount to be removed.
     */
    public DeleteCommand(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Constructor for index-based deletion.
     *
     * @param index    The 1-based index of the equipment in the list.
     * @param quantity The amount to be removed.
     */
    public DeleteCommand(int index, int quantity) {
        this.index = index;
        this.quantity = quantity;
    }

    /**
     * Finds the target equipment and reduces its quantity.
     *
     * @param equipments The current list of equipment.
     * @param ui         The user interface to display the result.
     * @param storage    The storage utility to save changes.
     * @throws EquipmentMasterException If the item is not found or quantity is invalid.
     */
    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) throws EquipmentMasterException {
        Equipment target = findTarget(equipments);

        int currentQty = target.getQuantity();
        if (quantity > currentQty) {
            throw new EquipmentMasterException("Only " + currentQty + " available. Cannot delete " + quantity + ".");
        }

        target.setQuantity(currentQty - quantity);
        storage.save(equipments.getAllEquipments());
        ui.showMessage("Deleted " + quantity + " unit(s) of " + target.getName() + ".");
    }

    /**
     * Helper method to locate the equipment based on name or index.
     */
    private Equipment findTarget(EquipmentList equipments) throws EquipmentMasterException {
        if (name != null) {
            for (Equipment e : equipments.getAllEquipments()) {
                if (e.getName().equalsIgnoreCase(name)) {
                    return e;
                }
            }
            throw new EquipmentMasterException("Equipment '" + name + "' not found.");
        }

        if (index < 1 || index > equipments.getSize()) {
            throw new EquipmentMasterException("Invalid index. Please check the list again.");
        }
        return equipments.getEquipment(index - 1);
    }
}
