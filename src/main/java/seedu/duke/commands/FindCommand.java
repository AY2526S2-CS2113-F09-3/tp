package seedu.duke.commands;

import seedu.duke.equipment.Equipment;
import seedu.duke.equipmentlist.EquipmentList;
import seedu.duke.storage.Storage;
import seedu.duke.ui.Ui;

import java.util.ArrayList;

/**
 * Represents a command to find equipment that contains a specific keyword.
 * This command searches through the inventory and prints all matching equipment.
 */
public class FindCommand extends Command {
    private String keyword;

    /**
     * Constructor.
     * @param keyword The word to be searched.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Iterates through the equipment list to find matches and displays them.
     * @param equipments The current list of equipment to search through.
     * @param ui         The user interface to handle printing the results.
     * @param storage    The storage utility.
     */
    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        if (equipments.isEmpty()) {
            ui.showMessage("There is no equipment in your list.");
            return;
        }

        ArrayList<Equipment> matchingEquipments = new ArrayList<>();

        // Iterate through the whole list to find if each equipment contains the keyword
        for (int i = 0; i < equipments.getSize(); i++) {
            Equipment eq = equipments.getEquipment(i);
            if (eq.getName().toLowerCase().contains(keyword.toLowerCase())) {
                matchingEquipments.add(eq);
            }
        }

        if (matchingEquipments.isEmpty()) {
            ui.showMessage("There is no matching equipment in your list.");
        } else {
            if (matchingEquipments.size() == 1) {
                ui.showMessage("1 equipment listed!");
            } else {
                ui.showMessage(matchingEquipments.size() + " equipments listed!");
            }

            for (int i = 0; i < matchingEquipments.size(); i++) {
                ui.showMessage((i + 1) + ". " + matchingEquipments.get(i).toString());
            }
        }
    }
}