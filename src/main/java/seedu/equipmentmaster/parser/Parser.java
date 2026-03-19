package seedu.equipmentmaster.parser;

import seedu.equipmentmaster.commands.AddCommand;
import seedu.equipmentmaster.commands.ByeCommand;
import seedu.equipmentmaster.commands.FindCommand;
import seedu.equipmentmaster.commands.ListCommand;
import seedu.equipmentmaster.commands.GetSemCommand;
import seedu.equipmentmaster.commands.SetSemCommand;
import seedu.equipmentmaster.commands.HelpCommand;
import seedu.equipmentmaster.commands.ReportCommand;
import seedu.equipmentmaster.commands.Command;
import seedu.equipmentmaster.commands.DeleteCommand;
import seedu.equipmentmaster.commands.SetStatusCommand;
import seedu.equipmentmaster.commands.SetMinCommand;
import seedu.equipmentmaster.exception.EquipmentMasterException;


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_INPUT;

/**
 * Parses user input into specific commands for the EquipmentMaster application.
 * Matches input strings against a registry of known command specifications.
 *
 */
public class Parser {
    private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());
    private static ArrayList<CommandSpec> commandSpecs = new ArrayList<>();

    static {
        commandSpecs.add(new CommandSpec("add", "add n/NAME q/QUANTITY [m/MODULE] " +
                "[bought/SEMESTER] [min/MINTHRESHOLD] [life/LIFESPAN]",
                AddCommand::parse));
        commandSpecs.add(new CommandSpec("list", "list", fullCommand -> new ListCommand()));
        commandSpecs.add(new CommandSpec("bye", "bye", fullCommand -> new ByeCommand()));
        commandSpecs.add(new CommandSpec("setstatus", "setstatus n/NAME q/QUANTITY s/STATUS "
                + "or setstatus INDEX q/QUANTITY s/STATUS", SetStatusCommand::parse));
        commandSpecs.add(new CommandSpec("find", "find KEYWORD", FindCommand::parse));
        commandSpecs.add(new CommandSpec("setsem", "setsem AY[YYYY]/[YY] Sem[1/2]", SetSemCommand::parse));
        commandSpecs.add(new CommandSpec("getsem", "getsem", fullCommand -> new GetSemCommand()));
        commandSpecs.add(new CommandSpec("delete", "delete n/NAME q/QUANTITY s/STATUS "
                + "or delete INDEX q/QUANTITY s/STATUS", DeleteCommand::parse));
        commandSpecs.add(new CommandSpec("help", "help", fullCommand -> new HelpCommand()));
        commandSpecs.add(new CommandSpec("setmin", "setmin n/NAME min/QUANTITY", SetMinCommand::parse));
        commandSpecs.add(new CommandSpec("report", "report aging [AY[YYYY]/[YY] Sem[1/2]] or report" +
                "lowstock",
                ReportCommand::parse));
    }

    /**
     * Retrieves the list of all available command specifications.
     *
     * @return The list of registered CommandSpec objects.
     */
    public static ArrayList<CommandSpec> getCommandSpecs() {

        return new ArrayList<>(commandSpecs);
    }

    /**
     * Parses the full command string typed by the user and returns the corresponding Command object.
     *
     * @param fullCommand The entire line of text entered by the user.
     * @return A Command object ready to be executed.
     * @throws EquipmentMasterException If the user input is invalid or the command is unknown.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        LOGGER.log(Level.INFO, "User entered: " + fullCommand);
        String[] words = fullCommand.trim().split("\\s+");

        if (words.length == 0) {
            throw new EquipmentMasterException(MESSAGE_INVALID_INPUT);
        }

        for (CommandSpec spec : commandSpecs) {
            if (spec.keyword.equalsIgnoreCase(words[0])) {
                return spec.creator.parse(fullCommand);
            }
        }

        throw new EquipmentMasterException(MESSAGE_INVALID_INPUT);
    }

    /**
     * Functional interface for creating Command objects from a string.
     */
    public interface CommandFactory {
        Command parse(String fullCommand) throws EquipmentMasterException;
    }

    /**
     * Represents the specification of a command, including its keyword, format, and creation logic.
     */
    public static class CommandSpec {
        private String keyword;
        private String format;
        private CommandFactory creator;

        /**
         * Constructs a new CommandSpec.
         *
         * @param keyword The keyword used to invoke the command (e.g., "add", "list").
         * @param format  The usage format string explaining how to use the command.
         * @param creator The factory to create the command instance.
         */
        public CommandSpec(String keyword, String format, CommandFactory creator) {
            this.keyword = keyword;
            this.format = format;
            this.creator = creator;
        }

        public String getKeyword() {
            return keyword;
        }

        public String getFormat() {
            return format;
        }

        public CommandFactory getCreator() {
            return creator;
        }
    }
}
