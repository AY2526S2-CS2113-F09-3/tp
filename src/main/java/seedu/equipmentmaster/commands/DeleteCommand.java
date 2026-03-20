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
    private String status;

    /**
     * Constructor for name-based deletion.
     *
     * @param name     The exact name of the equipment.
     * @param quantity The amount to be removed.
     * @param status   The specific status of the equipment.
     */
    public DeleteCommand(String name, int quantity, String status) {
        this.name = name;
        this.quantity = quantity;
        this.status = status;
    }

    /**
     * Constructor for index-based deletion.
     *
     * @param index    The 1-based index of the equipment in the list.
     * @param quantity The amount to be removed.
     * @param status   The specific status of the equipment.
     */
    public DeleteCommand(int index, int quantity, String status) {
        this.index = index;
        this.quantity = quantity;
        this.status = status;
    }

    /**
     * Parses the arguments for the 'delete' command.
     * @param fullCommand The complete input string.
     * @return A DeleteCommand object.
     * @throws EquipmentMasterException If the format is invalid.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        // Pad with space to accurately find flags, avoiding prefix conflicts
        String paddedCommand = " " + fullCommand.replaceFirst("(?i)delete", "").trim() + " ";

        int qIndex = paddedCommand.indexOf(" q/");
        int sIndex = paddedCommand.indexOf(" s/");

        if (qIndex == -1 || sIndex == -1) {
            throw new EquipmentMasterException("Invalid format. Use: delete [INDEX|n/NAME] q/QUANTITY s/STATUS");
        }

        // Extract identifier (Index or Name) which comes before the first flag
        int firstFlagIndex = Math.min(qIndex, sIndex);
        String identifierPart = paddedCommand.substring(0, firstFlagIndex).trim();

        // Extract quantity and status based on order
        String qtString = "";
        String statusStr = "";

        if (qIndex < sIndex) {
            qtString = paddedCommand.substring(qIndex + 3, sIndex).trim();
            statusStr = paddedCommand.substring(sIndex + 3).trim();
        } else {
            statusStr = paddedCommand.substring(sIndex + 3, qIndex).trim();
            qtString = paddedCommand.substring(qIndex + 3).trim();
        }

        // Validate Quantity
        int quantity;
        try {
            quantity = Integer.parseInt(qtString);
            if (quantity <= 0) {
                throw new EquipmentMasterException("Quantity to delete must be greater than 0.");
            }
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Quantity must be a valid whole number.");
        }

        // Validate Status
        statusStr = statusStr.toLowerCase();
        if (!statusStr.equals("available") && !statusStr.equals("loaned")) {
            throw new EquipmentMasterException("Status must be 'available' or 'loaned'.");
        }

        // Parse Name or Index
        if (identifierPart.startsWith("n/")) {
            String name = identifierPart.substring(2).trim();
            if (name.isEmpty()) {
                throw new EquipmentMasterException("Equipment name cannot be empty.");
            }
            return new DeleteCommand(name, quantity, statusStr);
        } else {
            try {
                int index = Integer.parseInt(identifierPart);
                return new DeleteCommand(index, quantity, statusStr);
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

        // 1. Check and deduct from specific status
        if (status.equals("available")) {
            int currentAvailable = target.getAvailable();
            if (quantity > currentAvailable) {
                throw new EquipmentMasterException("Only " + currentAvailable +
                        " available unit(s). Cannot delete " + quantity + ".");
            }
            target.setAvailable(currentAvailable - quantity);
        } else if (status.equals("loaned")) {
            int currentLoaned = target.getLoaned();
            if (quantity > currentLoaned) {
                throw new EquipmentMasterException("Only " + currentLoaned +
                        " loaned unit(s). Cannot delete " + quantity + ".");
            }
            target.setLoaned(currentLoaned - quantity);
        }

        // 2. Deduct from total quantity
        int newTotal = target.getQuantity() - quantity;
        target.setQuantity(newTotal);

        // 3. Print success message exactly once
        if (newTotal == 0) {
            equipments.removeEquipment(target);
            ui.showMessage("Deleted " + quantity + " " + status + " unit(s) of " + target.getName() + ".");
            ui.showMessage("Notice: Total quantity reached 0. The item has been completely removed from the list.");
        } else {
            ui.showMessage("Deleted " + quantity + " " + status + " unit(s) of " + target.getName() + ".");
            if (newTotal <= target.getMinQuantity()) {
                ui.showMessage("!!! LOW STOCK ALERT: " + target.getName() +
                        " is at or below threshold! (Current: " + newTotal +
                        ", Min: " + target.getMinQuantity() + ")");
            }
        }

        storage.save(equipments.getAllEquipments());
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
