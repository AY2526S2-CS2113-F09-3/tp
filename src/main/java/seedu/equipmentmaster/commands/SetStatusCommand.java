package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

/**
 * Represents a command to update the status (loaned/available) of equipment.
 * This command can identify equipment by either its index in the list or its name.
 * It updates the available and loaned quantities accordingly and saves changes to storage.
 */
public class SetStatusCommand extends Command {

    private final Integer index;
    private final String name;
    private final int quantity;
    private final String status;

    /**
     * Constructor for index-based identification.
     *
     * @param index The position of the equipment in the list (1-based).
     * @param quantity The number of units to update.
     * @param status The new status to apply ("loaned" or "available").
     */
    public SetStatusCommand(Integer index, int quantity, String status) {
        this.index = index;
        this.name = null;
        this.quantity = quantity;
        this.status = status;
    }

    /**
     * Constructor for name-based identification.
     *
     * @param name The name of the equipment to update.
     * @param quantity The number of units to update.
     * @param status The new status to apply ("loaned" or "available").
     */
    public SetStatusCommand(String name, int quantity, String status) {
        this.index = null;
        this.name = name;
        this.quantity = quantity;
        this.status = status;
    }

    /**
     * Executes the set status command.
     * Validates the quantity and status, finds the target equipment by index or name,
     * updates the available and loaned quantities, and saves changes to storage.
     *
     * @param equipments The current list of equipment to operate on.
     * @param ui The user interface to display messages.
     * @param storage The storage utility to save changes.
     */
    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        if (quantity <= 0) {
            ui.showMessage("Quantity must be greater than 0.");
            return;
        }

        if (!status.equalsIgnoreCase("loaned") && !status.equalsIgnoreCase("available")) {
            ui.showMessage("Invalid status. Status must be 'loaned' or 'available'.");
            return;
        }

        Equipment targetEquipment = null;

        // Search by index
        if (index != null) {
            if (index > 0 && index <= equipments.getSize()) {
                targetEquipment = equipments.getEquipment(index - 1);
            } else {
                ui.showMessage("Invalid index. Please provide an index between 1 and "
                        + equipments.getSize() + ".");
                return;
            }
            // Search by name
        } else if (name != null) {
            for (int i = 0; i < equipments.getSize(); i++) {
                Equipment eq = equipments.getEquipment(i);
                if (eq.getName().equalsIgnoreCase(name)) {
                    targetEquipment = eq;
                    break;
                }
            }
            if (targetEquipment == null) {
                ui.showMessage("Equipment with name '" + name + "' not found.");
                return;
            }
        } else {
            ui.showMessage("No equipment identifier provided (index or name).");
            return;
        }

        // Get current values
        String equipmentName = targetEquipment.getName();
        int available = targetEquipment.getAvailable();
        int loaned = targetEquipment.getLoaned();

        switch (status.toLowerCase()) {
        case "loaned":
            if (quantity > available) {
                ui.showMessage("Insufficient available units. Only "
                        + available + " units available.");
                return;
            }
            targetEquipment.setAvailable(available - quantity);
            targetEquipment.setLoaned(loaned + quantity);
            ui.showMessage(quantity + " units of " + equipmentName + " are now LOANED.");
            break;

        case "available":
            if (quantity > loaned) {
                ui.showMessage("Cannot return more than currently loaned. Only "
                        + loaned + " units on loan.");
                return;
            }
            targetEquipment.setAvailable(available + quantity);
            targetEquipment.setLoaned(loaned - quantity);
            ui.showMessage(quantity + " units of " + equipmentName + " are now AVAILABLE.");
            break;

        default:
            ui.showMessage("Invalid status.");
            return;
        }

        // Save changes to file
        storage.save(equipments.getAllEquipments());
    }
}
