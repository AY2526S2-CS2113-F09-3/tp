package seedu.equipmentmaster.parser;

import seedu.equipmentmaster.commands.AddCommand;
import seedu.equipmentmaster.commands.ByeCommand;
import seedu.equipmentmaster.commands.Command;
import seedu.equipmentmaster.commands.FindCommand;
import seedu.equipmentmaster.commands.ListCommand;
import seedu.equipmentmaster.exception.EquipmentMasterException;


import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_ADD_FORMAT;
import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_FIND_FORMAT;
import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_INPUT;

public class Parser {

    /**
     * Parses the full command string typed by the user and returns the corresponding Command object.
     *
     * @param fullCommand The entire line of text entered by the user.
     * @return A Command object ready to be executed.
     * @throws EquipmentMasterException If the user input is invalid or the command is unknown.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.split(" ");

        switch (words[0]) {
        case "add":
            return parseAdd(fullCommand);
        case "list":
            return new ListCommand();
        case "bye":
            return new ByeCommand();
        case "find":
            return parseFind(fullCommand);

        default:
            throw new EquipmentMasterException(MESSAGE_INVALID_INPUT);
        }
    }

    /**
     * Parses the arguments for the 'add' command and creates an AddCommand object.
     *
     * @param fullCommand The complete input string containing the 'add' command and its arguments.
     * @return An AddCommand object containing the parsed equipment name and quantity.
     * @throws EquipmentMasterException If the format is incorrect, quantity is missing/invalid, or negative.
     */
    public static Command parseAdd(String fullCommand) throws EquipmentMasterException {
        if (!fullCommand.contains("n/") || (!fullCommand.contains("q/"))) {
            throw new EquipmentMasterException(MESSAGE_INVALID_ADD_FORMAT);
        }
        int nameIndex = fullCommand.indexOf("n/");
        int quantityIndex = fullCommand.indexOf("q/");
        String name = "";
        String qtString = "";
        if (nameIndex < quantityIndex) {
            name = fullCommand.substring(nameIndex + 2, quantityIndex - 1);
            qtString = fullCommand.substring(quantityIndex + 2);
        } else {
            qtString = fullCommand.substring(quantityIndex + 2, nameIndex - 1);
            name = fullCommand.substring((nameIndex + 2));
        }
        try {
            int quantity = Integer.parseInt(qtString);
            if (quantity < 0) {
                throw new EquipmentMasterException("Equipment quantity cannot be negative.");
            }
            return new AddCommand(name, quantity);
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Please enter a valid whole number for quantity");
        }
    }

    /**
     * Parses the arguments for the 'find' command and creates a FindCommand object.
     *
     * @param fullCommand The complete input string containing the 'find' command and its keywords.
     * @return A FindCommand object containing the search keyword.
     * @throws EquipmentMasterException If the user does not provide a keyword after the 'find' command.
     */
    public static Command parseFind(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.split(" ", 2);

        // Check if the user only typed "find" without any keywords
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new EquipmentMasterException(MESSAGE_INVALID_FIND_FORMAT);
        }

        String keyword = words[1].trim();
        return new FindCommand(keyword);
    }
}
