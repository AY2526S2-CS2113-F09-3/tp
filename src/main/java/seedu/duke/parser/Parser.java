package seedu.duke.parser;

import static seedu.duke.common.Messages.MESSAGE_INVALID_ADD_FORMAT;
import static seedu.duke.common.Messages.MESSAGE_INVALID_INPUT;

import seedu.duke.commands.AddCommand;
import seedu.duke.commands.ByeCommand;
import seedu.duke.commands.Command;
import seedu.duke.commands.ListCommand;
import seedu.duke.exception.EquipmentMasterException;

public class Parser {
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.split(" ");

        switch (words[0]) {
        case "add":
            return parseAdd(fullCommand);
        case "list":
            return new ListCommand();
        case "bye":
            return new ByeCommand();

        default:
            throw new EquipmentMasterException(MESSAGE_INVALID_INPUT);
        }
    }

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
}
