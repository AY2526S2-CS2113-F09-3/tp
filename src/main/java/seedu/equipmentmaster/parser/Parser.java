package seedu.equipmentmaster.parser;

import seedu.equipmentmaster.commands.AddCommand;
import seedu.equipmentmaster.commands.ByeCommand;
import seedu.equipmentmaster.commands.Command;
import seedu.equipmentmaster.commands.FindCommand;
import seedu.equipmentmaster.commands.ListCommand;
import seedu.equipmentmaster.commands.SetSemCommand;
import seedu.equipmentmaster.commands.SetStatusCommand;
import seedu.equipmentmaster.commands.GetSemCommand;
import seedu.equipmentmaster.commands.DeleteCommand;
import seedu.equipmentmaster.exception.EquipmentMasterException;


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
        String[] words = fullCommand.trim().split("\\s+");

        switch (words[0]) {
        case "add":
            return AddCommand.parse(fullCommand);
        case "list":
            return new ListCommand();
        case "bye":
            return new ByeCommand();
        case "setstatus":
            return SetStatusCommand.parse(fullCommand);
        case "find":
            return FindCommand.parse(fullCommand);
        case "setsem":
            return SetSemCommand.parse(fullCommand);
        case "getsem":
            return new GetSemCommand();
        case "delete":
            return DeleteCommand.parse(fullCommand);

        default:
            throw new EquipmentMasterException(MESSAGE_INVALID_INPUT);
        }
    }
}
