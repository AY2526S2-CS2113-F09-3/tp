package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

public class SetMinCommand extends Command {
    private final String name;
    private final int index;
    private final int minQty;

    public SetMinCommand(String name, int minQty) {
        this.name = name;
        this.index = -1;
        this.minQty = minQty;
    }

    /**
     * Constructor for index-based setmin.
     *
     * @param index  The 1-based index of the equipment in the list.
     * @param minQty The minimum threshold to set.
     */
    public SetMinCommand(int index, int minQty) {
        this.name = null;
        this.index = index;
        this.minQty = minQty;
    }

    /**
     * Executes the set minimum threshold command.
     * Updates the minimum stock threshold for a specific equipment to trigger future low-stock alerts.
     *
     * @param context The application context containing the equipment list, UI, and storage.
     * @throws EquipmentMasterException If the equipment is not found or saving fails.
     */
    @Override
    public void execute(Context context) throws EquipmentMasterException {
        EquipmentList equipments = context.getEquipments();
        Ui ui = context.getUi();
        Storage storage = context.getStorage();

        Equipment target = findTarget(equipments);

        target.setMinQuantity(minQty);
        ui.showMessage("Success: Minimum threshold for " + target.getName() + " set to " + minQty);

        if (target.getMinQuantity() > 0 && target.getAvailable() <= target.getMinQuantity()) {
            ui.showMessage("Warning: Item is currently at or below this new threshold!");
        }
        storage.save(equipments.getAllEquipments());
    }

    /**
     * Helper method to locate the equipment based on name or index.
     *
     * @param equipments The current list of equipment.
     * @return The target equipment.
     * @throws EquipmentMasterException If the equipment is not found or index is invalid.
     */
    private Equipment findTarget(EquipmentList equipments) throws EquipmentMasterException {
        if (name != null) {
            Equipment target = equipments.findByName(name);
            if (target == null) {
                throw new EquipmentMasterException("Equipment not found: " + name);
            }
            return target;
        }

        if (index < 1 || index > equipments.getSize()) {
            throw new EquipmentMasterException("Invalid index. Please check the list again.");
        }
        return equipments.getEquipment(index - 1);
    }

    /**
     * Parses the arguments for the 'setmin' command.
     * Supports both name-based (n/NAME) and index-based formats.
     *
     * @param command The complete input string.
     * @return A SetMinCommand object.
     * @throws EquipmentMasterException If the format is invalid.
     */
    public static Command parse(String command) throws EquipmentMasterException {
        try {
            int minIndex = command.indexOf(" min/");
            if (minIndex == -1) {
                throw new EquipmentMasterException("Invalid format! Use: setmin n/NAME min/QUANTITY"
                        + " or setmin INDEX min/QUANTITY");
            }

            int min = Integer.parseInt(command.substring(minIndex + 5).trim());
            if (min < 0) {
                throw new EquipmentMasterException("Minimum threshold cannot be negative.");
            }

            // Name-based
            if (command.contains(" n/")) {
                String name = command.split(" n/")[1].split(" min/")[0].trim();
                if (name.isEmpty()) {
                    throw new EquipmentMasterException("Equipment name cannot be empty.");
                }
                return new SetMinCommand(name, min);
            }

            // Index-based
            String[] parts = command.trim().split("\\s+");
            if (parts.length < 3) {
                throw new EquipmentMasterException("Invalid format! Use: setmin n/NAME min/QUANTITY"
                        + " or setmin INDEX min/QUANTITY");
            }
            int index = Integer.parseInt(parts[1]);
            return new SetMinCommand(index, min);

        } catch (EquipmentMasterException e) {
            throw e;
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new EquipmentMasterException("Invalid format! Use: setmin n/NAME min/QUANTITY"
                    + " or setmin INDEX min/QUANTITY");
        }
    }
}
