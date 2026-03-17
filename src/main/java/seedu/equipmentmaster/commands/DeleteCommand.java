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
     * Parses the arguments for the 'delete' command.
     * @param fullCommand The complete input string.
     * @return A DeleteCommand object.
     * @throws EquipmentMasterException If the format is invalid.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        if (!fullCommand.contains("q/")) {
            throw new EquipmentMasterException("Invalid format. Use: delete [INDEX|n/NAME] q/QUANTITY");
        }

        String[] parts = fullCommand.split("q/");
        // Removes the word "delete" to find the name or index
        String identifierPart = parts[0].replaceFirst("(?i)delete", "").trim();
        String quantityStr = parts[1].trim();

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Quantity must be a valid number.");
        }

        if (identifierPart.startsWith("n/")) {
            String name = identifierPart.substring(2).trim();
            return new DeleteCommand(name, quantity);
        } else {
            try {
                int index = Integer.parseInt(identifierPart);
                return new DeleteCommand(index, quantity);
            } catch (NumberFormatException e) {
                throw new EquipmentMasterException("Please provide a valid name (n/) or index.");
            }
        }
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
