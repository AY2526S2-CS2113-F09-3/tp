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
}
