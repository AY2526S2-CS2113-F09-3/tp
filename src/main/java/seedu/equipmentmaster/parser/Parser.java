package seedu.equipmentmaster.parser;

import seedu.equipmentmaster.commands.*;
import seedu.equipmentmaster.exception.EquipmentMasterException;


import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_ADD_FORMAT;
import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_FIND_FORMAT;
import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_INPUT;
import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_SET_STATUS_FORMAT;

public class Parser {

    /**
     * Parses the full command string typed by the user and returns the corresponding Command object.
     *
     * @param fullCommand The entire line of text entered by the user.
     * @return A Command object ready to be executed.
     * @throws EquipmentMasterException If the user input is invalid or the command is unknown.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.trim().split("\\s+");

        switch (words[0]) {
        case "add":
            return parseAdd(fullCommand);
        case "list":
            return new ListCommand();
        case "bye":
            return new ByeCommand();
        case "setstatus":
            return parseSetStatus(fullCommand);
        case "find":
            return parseFind(fullCommand);
        case "setsem":
            return parseSetSem(fullCommand);
        case "getsem":
            return new GetSemCommand();

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

    /**
     * Parses the arguments for the 'setstatus' command.
     * Determines whether the command uses name-based or index-based identification.
     *
     * @param fullCommand The complete input string containing the 'setstatus' command and its arguments.
     * @return A SetStatusCommand object ready to be executed.
     * @throws EquipmentMasterException If the command format is invalid.
     */
    public static Command parseSetStatus(String fullCommand) throws EquipmentMasterException {
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
     * Parses the arguments for the 'setsem' command and creates a SetSemCommand object.
     *
     * @param fullCommand The complete input string containing the 'setsem' command and the semester.
     * @return A SetSemCommand object containing the target academic semester.
     * @throws EquipmentMasterException If the semester argument is missing.
     */
    public static Command parseSetSem(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.trim().split("\\s+", 2);

        // Check if the user provided the semester string after "setsem"
        if (words.length < 2 || words[1].trim().isEmpty()) {
            throw new EquipmentMasterException("Please specify a semester. Usage: setsem AY[YYYY]/[YY] Sem[1/2]");
        }

        String rawSemester = words[1].trim();
        return new SetSemCommand(rawSemester);
    }
}
