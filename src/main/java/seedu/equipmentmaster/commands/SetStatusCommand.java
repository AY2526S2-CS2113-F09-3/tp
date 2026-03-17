package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_SET_STATUS_FORMAT;

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
     * Parses the arguments for the 'setstatus' command.
     * Determines whether the command uses name-based or index-based identification.
     *
     * @param fullCommand The complete input string containing the 'setstatus' command and its arguments.
     * @return A SetStatusCommand object ready to be executed.
     * @throws EquipmentMasterException If the command format is invalid.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        fullCommand = fullCommand.trim();
        if (fullCommand.isEmpty()) {
            throw new EquipmentMasterException("Empty command.");
        }

        // Check if name-based (contains "n/") or index-based
        if (fullCommand.contains("n/")) {
            return parseSetStatusByName(fullCommand);
        } else {
            return parseSetStatusByIndex(fullCommand);
        }
    }

    /**
     * Parses a name-based setstatus command.
     * Expected format: setstatus n/NAME q/QUANTITY s/STATUS
     *
     * @param fullCommand The complete input string containing name-based arguments.
     * @return A SetStatusCommand configured with the parsed name, quantity, and status.
     * @throws EquipmentMasterException If any argument is missing, invalid, or incorrectly formatted.
     */
    private static Command parseSetStatusByName(String fullCommand) throws EquipmentMasterException {
        // Enforce strict order: n/ then q/ then s/
        if (!fullCommand.matches(".*n/.*q/.*s/.*")) {
            throw new EquipmentMasterException(MESSAGE_INVALID_SET_STATUS_FORMAT);
        }
        String[] parts = fullCommand.split("n/|q/|s/");
        if (parts.length < 4) { // first part before n/, then name, quantity, status
            throw new EquipmentMasterException(MESSAGE_INVALID_SET_STATUS_FORMAT);
        }

        String name = parts[1].trim();
        String quantityStr = parts[2].trim();
        String status = parts[3].trim().toLowerCase();

        if (name.isEmpty()) {
            throw new EquipmentMasterException("Equipment name cannot be empty.");
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new EquipmentMasterException("Quantity must be greater than 0.");
            }
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Please enter a valid whole number for quantity");
        }

        if (!status.equals("loaned") && !status.equals("available")) {
            throw new EquipmentMasterException("Status must be 'loaned' or 'available'.");
        }

        return new SetStatusCommand(name, quantity, status);
    }

    /**
     * Parses an index-based setstatus command.
     * Expected format: setstatus INDEX q/QUANTITY s/STATUS
     *
     * @param fullCommand The complete input string containing index-based arguments.
     * @return A SetStatusCommand configured with the parsed index, quantity, and status.
     * @throws EquipmentMasterException If any argument is missing, invalid, or incorrectly formatted.
     */
    private static Command parseSetStatusByIndex(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.trim().split("\\s+");
        if (words.length < 2) {
            throw new EquipmentMasterException(
                    "Missing index. Use: setstatus INDEX q/QUANTITY s/STATUS");
        }

        int index;
        try {
            index = Integer.parseInt(words[1]);
            if (index < 1) {
                throw new EquipmentMasterException("Index must be greater than 0.");
            }
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Please enter a valid whole number for index");
        }

        String[] parts = fullCommand.split("q/|s/");
        if (parts.length < 3) { // first part before q/, quantity, status
            throw new EquipmentMasterException(
                    "Invalid setstatus format. Use: setstatus INDEX q/QUANTITY s/STATUS");
        }

        String quantityStr = parts[1].trim();
        String status = parts[2].trim().toLowerCase();

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new EquipmentMasterException("Quantity must be greater than 0.");
            }
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Please enter a valid whole number for quantity");
        }

        if (!status.equals("loaned") && !status.equals("available")) {
            throw new EquipmentMasterException("Status must be 'loaned' or 'available'.");
        }

        return new SetStatusCommand(index, quantity, status);
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
