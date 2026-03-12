package seedu.equipmentmaster.commands;


import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

/**
 * Represents a command that adds new equipment to the equipment list.
 * The command creates a new {@code Equipment} object with the specified
 * name and quantity, adds it to the equipment list, saves the updated
 * list to storage, and displays a confirmation message to the user.
 */
public class AddCommand extends Command{
    private final String name;
    private final int quantity;

    /**
     * Constructs an {@code AddCommand} with the specified equipment name and quantity.
     *
     * @param name Name of the equipment to add.
     * @param quantity Number of items to add.
     */
    public AddCommand(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
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
        equipments.addEquipment(equipment);
        storage.save(equipments.getAllEquipments());
        ui.showMessage("Added " + quantity + " of " + name + ". (Total Available: " + equipment.getAvailable() + ")" );
    }
}
