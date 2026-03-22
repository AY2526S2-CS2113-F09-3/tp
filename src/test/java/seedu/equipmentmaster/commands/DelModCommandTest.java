package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit tests for the {@code DelModCommand} class.
 */
public class DelModCommandTest {

    @Test
    public void parse_validInput_success() throws EquipmentMasterException {
        String input = "delmod n/FIN3701";
        DelModCommand command = DelModCommand.parse(input);
        assertEquals(DelModCommand.class, command.getClass());
    }

    @Test
    public void parse_missingPrefix_throwsException() {
        // User forgot the "n/" prefix
        String input = "delmod FIN3701";

        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            DelModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("Expected: delmod n/NAME"));
    }

    @Test
    public void parse_emptyArguments_throwsException() {
        // User only typed the command word with nothing else
        String input = "delmod ";
        assertThrows(EquipmentMasterException.class, () -> {
            DelModCommand.parse(input);
        });
    }
}
