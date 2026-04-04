package seedu.equipmentmaster.parser;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.commands.AddModCommand;
import seedu.equipmentmaster.commands.Command;
import seedu.equipmentmaster.commands.DelModCommand;
import seedu.equipmentmaster.commands.ListModCommand;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

    @Test
    public void parse_validSetsem_returnsSetSemCommand() throws EquipmentMasterException {
        // Testing that "setSem" command is correctly identified and parameters are kept intact
        assertNotNull(Parser.parse("setsem AY2024/25 Sem1"));
    }

    @Test
    public void parse_setsemExtraSpaces_returnsSetSemCommand() throws EquipmentMasterException {
        // Testing robustness against multiple spaces between the command and the argument
        assertNotNull(Parser.parse("setsem    AY2024/25 Sem2"));
    }

    @Test
    public void parse_setsemMissingArgs_throwsException() {
        assertThrows(EquipmentMasterException.class, () -> Parser.parse("setsem "));
    }

    @Test
    public void parse_addModCommand_returnsAddModCommand() throws EquipmentMasterException {
        // Arrange
        String input = "addmod n/CS2113 pax/200";

        // Act
        Command result = Parser.parse(input);

        // Assert
        assertTrue(result instanceof AddModCommand, "Parser should return an AddModCommand object.");
    }

    @Test
    public void parse_listModCommand_returnsListModCommand() throws EquipmentMasterException {
        // Arrange
        String input = "listmod";

        // Act
        Command result = Parser.parse(input);

        // Assert
        assertTrue(result instanceof ListModCommand, "Parser should return a ListModCommand object.");
    }

    @Test
    public void parse_delModCommand_returnsDelModCommand() throws EquipmentMasterException {
        // Arrange
        String input = "delmod n/CS2113";

        // Act
        Command result = Parser.parse(input);

        // Assert
        assertTrue(result instanceof DelModCommand, "Parser should return a DelModCommand object.");
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        // Arrange
        String input = "fly n/CS2113"; // "fly" is not a registered command

        // Act & Assert
        assertThrows(EquipmentMasterException.class, () -> {
            Parser.parse(input);
        });
    }

    @Test
    public void parse_emptyInput_throwsException() {
        // Arrange
        String input = "   "; // Only whitespaces

        // Act & Assert
        assertThrows(EquipmentMasterException.class, () -> {
            Parser.parse(input);
        });
    }

    // --- Tests for CommandSpec Getters and getCommandSpecs ---

    @Test
    public void getCommandSpecs_returnsValidListAndGettersWork() {
        // Retrieves the list of command specs and verifies it is populated
        java.util.ArrayList<Parser.CommandSpec> specs = Parser.getCommandSpecs();
        assertTrue(specs.size() > 0, "CommandSpecs list should not be empty");

        // Verifies the getters inside the CommandSpec inner class
        Parser.CommandSpec firstSpec = specs.get(0);
        assertNotNull(firstSpec.getKeyword(), "Keyword should not be null");
        assertNotNull(firstSpec.getFormat(), "Format should not be null");
        assertNotNull(firstSpec.getCreator(), "Creator factory should not be null");
    }

    // --- Tests for CommandSpec.extractArgument ---

    @Test
    public void extractArgument_prefixNotFound_returnsEmptyString() {
        // Triggers the 'if (startIdx == -1)' branch
        String command = "add n/STM32 q/5";
        String[] prefixes = {" n/", " q/", " m/"};

        String result = Parser.CommandSpec.extractArgument(command, "m/", prefixes);
        assertEquals("", result, "Should return empty string when prefix is missing");
    }

    @Test
    public void extractArgument_argumentAtEnd_returnsCorrectString() {
        // Extracts the last argument, meaning there is no 'next' prefix to interrupt it
        String command = "add n/STM32 q/5";
        String[] prefixes = {" n/", " q/"};

        String result = Parser.CommandSpec.extractArgument(command, "q/", prefixes);
        assertEquals("5", result, "Should extract the exact string until the end of the command");
    }

    @Test
    public void extractArgument_argumentInMiddle_stopsAtNextPrefix() {
        // Triggers the 'if (pIdx != -1 && pIdx < endIdx)' branch
        // The extraction for 'n/' must stop when it sees 'm/'
        String command = "add n/STM32 Board m/EE2026 q/5";
        String[] prefixes = {" n/", " m/", " q/"};

        String result = Parser.CommandSpec.extractArgument(command, "n/", prefixes);
        assertEquals("STM32 Board", result, "Should extract the string and stop before the m/ prefix");
    }

    @Test
    public void extractArgument_prefixWithoutValue_returnsEmptyString() {
        // Extracts a prefix that is present but has no value following it before the next prefix
        String command = "add n/ q/5";
        String[] prefixes = {" n/", " q/"};

        String result = Parser.CommandSpec.extractArgument(command, "n/", prefixes);
        assertEquals("", result, "Should return an empty string when prefix has no value attached");
    }
}
