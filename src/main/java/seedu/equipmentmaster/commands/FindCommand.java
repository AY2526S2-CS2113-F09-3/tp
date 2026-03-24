// @@author Hongyu1231
package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
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
     *
     * @param equipments The current list of equipment to search through.
     * @return An ArrayList containing only the equipment that matches the keyword.
     */
    public ArrayList<Equipment> getMatchingEquipments(EquipmentList equipments) {
        ArrayList<Equipment> matchingEquipments = new ArrayList<>();
        String rawKeyword = (this.keyword == null) ? "" : this.keyword.trim();

        // Preserve original behavior: if keyword is empty, all equipments match
        if (rawKeyword.isEmpty()) {
            for (int i = 0; i < equipments.getSize(); i++) {
                matchingEquipments.add(equipments.getEquipment(i));
            }
            return matchingEquipments;
        }

        String[] tokens = rawKeyword.toLowerCase().split("\\s+");

        // Refactored: Main loop is now clean and easy to read (SLAP Principle)
        for (int i = 0; i < equipments.getSize(); i++) {
            Equipment eq = equipments.getEquipment(i);
            if (isMatchFound(eq, tokens)) {
                matchingEquipments.add(eq);
            }
        }
        return matchingEquipments;
    }

    /**
     * Helper method to check if an equipment matches any of the given search tokens.
     *
     * @param eq     The equipment to check.
     * @param tokens The array of search keywords.
     * @return true if the equipment matches at least one token, false otherwise.
     */
    private boolean isMatchFound(Equipment eq, String[] tokens) {
        for (String token : tokens) {
            if (token.isEmpty()) {
                continue;
            }
            // Early return: As soon as we find one match, we return true.
            // This eliminates the need for the clunky "contains(eq) -> break" logic!
            if (matchesNameOrModule(eq, token)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to check if a single token matches the equipment's name or its module codes.
     *
     * @param eq    The equipment to check.
     * @param token The single search keyword.
     * @return true if the token is found in the name or module codes.
     */
    private boolean matchesNameOrModule(Equipment eq, String token) {
        // 1. Check equipment name
        if (eq.getName().toLowerCase().contains(token)) {
            return true;
        }

        // 2. Guard Clause: If there are no modules, stop checking here
        if (eq.getModuleCodes() == null || eq.getModuleCodes().isEmpty()) {
            return false;
        }

        // 3. Check module codes
        for (String module : eq.getModuleCodes()) {
            if (module.toLowerCase().contains(token)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Executes the find command.
     * Searches the equipment list for items whose names or module codes match the keyword and displays them.
     *
     * @param context The application context containing the equipment list and UI.
     */
    @Override
    public void execute(Context context) {
        EquipmentList equipments = context.getEquipments();
        Ui ui = context.getUi();

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
