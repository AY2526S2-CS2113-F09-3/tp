package seedu.equipmentmaster.commands;


import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import java.util.ArrayList;

import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_ADD_FORMAT;

/**
 * Represents a command that adds new equipment to the equipment list.
 * The command creates a new {@code Equipment} object with the specified
 * name and quantity, adds it to the equipment list, saves the updated
 * list to storage, and displays a confirmation message to the user.
 */
public class AddCommand extends Command{
    private final String name;
    private final int quantity;
    private final ArrayList<String> moduleCodes;

    /**
     * Constructs an {@code AddCommand} with the specified equipment name and quantity.
     *
     * @param name Name of the equipment to add.
     * @param quantity Number of items to add.
     */
    public AddCommand(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        this.moduleCodes = new ArrayList<>();
    }

    /**
     * Constructs an {@code AddCommand} with the specified equipment name, quantity, and module codes.
     *
     * @param name Name of the equipment to add.
     * @param quantity Number of items to add.
     * @param moduleCodes List of module codes associated with this equipment.
     */
    public AddCommand(String name, int quantity, ArrayList<String> moduleCodes) {
        this.name = name;
        this.quantity = quantity;
        this.moduleCodes = moduleCodes != null ? moduleCodes : new ArrayList<>();
    }

    /**
     * Parses the arguments for the 'add' command and creates an AddCommand object.
     *
     * @param fullCommand The complete input string containing the 'add' command and its arguments.
     * @return An AddCommand object containing the parsed equipment name and quantity.
     * @throws EquipmentMasterException If the format is incorrect, quantity is missing/invalid, or negative.
     */
    public static AddCommand parse(String fullCommand) throws EquipmentMasterException {
        if (!fullCommand.contains("n/") || (!fullCommand.contains("q/"))) {
            throw new EquipmentMasterException(MESSAGE_INVALID_ADD_FORMAT);
        }
        int nameIndex = fullCommand.indexOf("n/");
        int quantityIndex = fullCommand.indexOf("q/");
        String name;
        String qtString;
        if (nameIndex < quantityIndex) {
            // Name appears before quantity: extract up to the start of "q/" and trim.
            name = fullCommand.substring(nameIndex + 2, quantityIndex).trim();
            String afterQ = fullCommand.substring(quantityIndex + 2).trim();
            int nextSpace = afterQ.indexOf(' ');
            if (nextSpace > 0) {
                qtString = afterQ.substring(0, nextSpace).trim();
            } else {
                qtString = afterQ;
            }
        } else {
            // Quantity appears before name: extract up to the start of "n/" and trim.
            String afterQ = fullCommand.substring(quantityIndex + 2, nameIndex).trim();

            int nextSpace = afterQ.indexOf(' ');
            if (nextSpace > 0) {
                qtString = afterQ.substring(0, nextSpace).trim();
            } else {
                qtString = afterQ;
            }
            name = fullCommand.substring(nameIndex + 2).trim();
        }

        if (name.isEmpty() || qtString.isEmpty()) {
            throw new EquipmentMasterException(MESSAGE_INVALID_ADD_FORMAT);
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtString);
            if (quantity <= 0) {
                throw new EquipmentMasterException("Equipment quantity must be positive.");
            }
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Please enter a valid whole number for quantity");
        }

        // Parse module codes (optional, multiple)
        ArrayList<String> moduleCodes = new ArrayList<>();
        String[] parts = fullCommand.split(" ");
        for (String part : parts) {
            if (part.startsWith("m/")) {
                String moduleCode = part.substring(2).toUpperCase().trim();
                if (!moduleCode.isEmpty() && !moduleCodes.contains(moduleCode)) {
                    moduleCodes.add(moduleCode);
                }
            }
        }
        return new AddCommand(name, quantity, moduleCodes);
    }

    /**
     * Executes the add command by creating the equipment,
     * adding it to the equipment list, saving the updated list,
     * and displaying a message to the user.
     *
     * @param equipments The equipment list to add the equipment to.
     * @param ui The user interface used to display messages.
     * @param storage The storage system used to persist data.
     */
    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        Equipment equipment = new Equipment(name, quantity);

        for (String moduleCode : moduleCodes) {
            equipment.addModuleCode(moduleCode);
        }

        equipments.addEquipment(equipment);
        storage.save(equipments.getAllEquipments());
        String message;
        if (moduleCodes.isEmpty()) {
            message = "Added " + quantity + " of " + name + ". (Total Available: " + equipment.getAvailable() + ")";
        } else {
            message = "Added " + quantity + " of " + name + " with modules " + moduleCodes
                    + ". (Total Available: " + equipment.getAvailable() + ")";
        }
        ui.showMessage(message);
    }
}
