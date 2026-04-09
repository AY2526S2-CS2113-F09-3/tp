//@@author Hongyu1231
package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.ui.Ui;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_FIND_FORMAT;

/**
 * Represents a command to find equipment that contains a specific keyword.
 * This command searches through the inventory and prints all matching equipment.
 */
public class FindCommand extends Command {
    private static final Logger logger = Logger.getLogger(FindCommand.class.getName());

    private final String keyword;

    /**
     * Constructor.
     * @param keyword The word to be searched.
     */
    public FindCommand(String keyword) {
        assert keyword != null : "Search keyword cannot be null";
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
        logger.log(Level.INFO, "Parsing FindCommand input.");

        String[] words = fullCommand.split(" ", 2);

        // Check if the user only typed "find" without any keywords
        if (words.length < 2 || words[1].trim().isEmpty()) {
            logger.log(Level.WARNING, "FindCommand parsing failed: missing keyword.");
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
        String rawKeyword = this.keyword.trim();

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
        assert context != null : "Context should not be null during execution"; // ASSERTION
        logExecution("FindCommand");

        EquipmentList equipments = context.getEquipments();
        Ui ui = context.getUi();

        if (equipments.isEmpty()) {
            logger.log(Level.INFO, "Find command executed on an empty inventory.");
            ui.showMessage("There is no equipment in your list.");
            return;
        }

        logger.log(Level.INFO, "Searching for keyword: " + keyword);
        ArrayList<Equipment> matchingEquipments = getMatchingEquipments(equipments);

        displayResults(ui, matchingEquipments);
    }

    /**
     * Helper method to handle the UI presentation of the search results.
     */
    private void displayResults(Ui ui, ArrayList<Equipment> results) {
        if (results.isEmpty()) {
            ui.showMessage("There is no matching equipment in your list.");
            return;
        }

        ui.showMessage(results.size() + " matching equipment(s) found!");

        // Simplified loop: Always use numbered lists for consistency, even if size is 1.
        for (int i = 0; i < results.size(); i++) {
            ui.showMessage((i + 1) + ". " + results.get(i).toString());
        }
    }
}
