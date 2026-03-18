package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.ArrayList;

import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_FIND_FORMAT;

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
     * Parses the arguments for the 'find' command and creates a FindCommand object.
     *
     * @param fullCommand The complete input string containing the 'find' command and its keywords.
     * @return A FindCommand object containing the search keyword.
     * @throws EquipmentMasterException If the user does not provide a keyword after the 'find' command.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.split(" ", 2);

        // Check if the user only typed "find" without any keywords
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new EquipmentMasterException(MESSAGE_INVALID_FIND_FORMAT);
        }

        String keyword = words[1].trim();
        return new FindCommand(keyword);
    }

    /**
     * Extracts the core logic of finding matching equipment so it can be tested easily.
     * Searches in both equipment name AND module codes
     *
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
                // Check module codes
                if (eq.getModuleCodes() != null && !eq.getModuleCodes().isEmpty()) {
                    for (String module : eq.getModuleCodes()) {
                        if (module.toLowerCase().contains(token)) {
                            matchingEquipments.add(eq);
                            break;
                        }
                    }
                    // If added from module search, break out of token loop
                    if (matchingEquipments.contains(eq)) {
                        break;
                    }
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
