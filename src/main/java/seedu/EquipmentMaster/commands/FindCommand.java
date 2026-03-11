package seedu.EquipmentMaster.commands;

import seedu.EquipmentMaster.equipment.Equipment;
import seedu.EquipmentMaster.equipmentlist.EquipmentList;
import seedu.EquipmentMaster.storage.Storage;
import seedu.EquipmentMaster.ui.Ui;

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
     * Extracts the core logic of finding matching equipment so it can be tested easily.
     * @param equipments The current list of equipment to search through.
     * @return An ArrayList containing only the equipment that matches the keyword.
     */
    public ArrayList<Equipment> getMatchingEquipments(EquipmentList equipments) {
        ArrayList<Equipment> matchingEquipments = new ArrayList<>();
        // Normalize the keyword for case-insensitive, multi-word matching
        String rawKeyword = (this.keyword == null) ? "" : this.keyword.trim();
        // Preserve original behavior: if keyword is empty, all equipments match
        if (rawKeyword.isEmpty()) {
            for (int i = 0; i < equipments.getSize(); i++) {
                matchingEquipments.add(equipments.getEquipment(i));
            }
            return matchingEquipments;
        }
        String[] tokens = rawKeyword.toLowerCase().split("\\s+");
        for (int i = 0; i < equipments.getSize(); i++) {
            Equipment eq = equipments.getEquipment(i);
            String equipmentNameLower = eq.getName().toLowerCase();
            for (String token : tokens) {
                if (!token.isEmpty() && equipmentNameLower.contains(token)) {
                    matchingEquipments.add(eq);
                    break; // Avoid adding the same equipment multiple times
                }
            }
        }
        return matchingEquipments;
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

        ArrayList<Equipment> matchingEquipments = getMatchingEquipments(equipments);

        if (matchingEquipments.isEmpty()) {
            ui.showMessage("There is no matching equipment in your list.");
        } else {
            if (matchingEquipments.size() == 1) {
                ui.showMessage("1 equipment listed!");
            } else {
                ui.showMessage(matchingEquipments.size() + " equipments listed!");
            }

            for (int i = 0; i < matchingEquipments.size(); i++) {
                if (matchingEquipments.size() == 1) {
                    ui.showMessage(matchingEquipments.get(i).toString());
                } else {
                    ui.showMessage((i + 1) + ". " + matchingEquipments.get(i).toString());
                }
            }
        }
    }
}