package seedu.equipmentmaster.parser;

import seedu.equipmentmaster.commands.*;
import seedu.equipmentmaster.exception.EquipmentMasterException;


import java.util.ArrayList;

import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_INPUT;

public class Parser {

    private static ArrayList<CommandSpec> commandSpecs = new ArrayList<>();

    static{
        commandSpecs.add(new CommandSpec("add", "add n/NAME q/QUANTITY", AddCommand::parse));
        commandSpecs.add(new CommandSpec("list", "list", fullCommand -> new ListCommand()));
        commandSpecs.add(new CommandSpec("bye", "bye", fullCommand -> new ByeCommand()));
        commandSpecs.add(new CommandSpec("setstatus", "setstatus n/NAME s/STATUS", SetStatusCommand::parse));
        commandSpecs.add(new CommandSpec("find", "find KEYWORD", FindCommand::parse));
        commandSpecs.add(new CommandSpec("setsem", "setsem SEMESTER", SetSemCommand::parse));
        commandSpecs.add(new CommandSpec("getsem", "getsem", fullCommand -> new GetSemCommand()));
        commandSpecs.add(new CommandSpec("delete", "delete n/NAME", DeleteCommand::parse));
        commandSpecs.add(new CommandSpec("help","help", fullCommand -> new HelpCommand()));
    }

    public static ArrayList<CommandSpec> getCommandSpecs() {
        return commandSpecs;
    }

    /**
     * Parses the full command string typed by the user and returns the corresponding Command object.
     *
     * @param fullCommand The entire line of text entered by the user.
     * @return A Command object ready to be executed.
     * @throws EquipmentMasterException If the user input is invalid or the command is unknown.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        String[] words = fullCommand.trim().split("\\s+");

        for(CommandSpec spec: commandSpecs){
            if(spec.keyword.equalsIgnoreCase(words[0])){
                return  spec.creator.parse(fullCommand);
            }
        }

        throw new EquipmentMasterException(MESSAGE_INVALID_INPUT);
    }

    public interface CommandFactory {
        Command parse(String fullCommand) throws EquipmentMasterException;
    }

    public static class CommandSpec{
        private String keyword;
        private String format;
        private CommandFactory creator;

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
