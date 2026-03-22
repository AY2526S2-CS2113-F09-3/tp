package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit tests for the {@code AddModCommand} class.
 * Tests parsing logic, syntax error handling, and execution behavior.
 */
public class AddModCommandTest {

    private ModuleList moduleList;

    @BeforeEach
    public void setUp() {
        moduleList = new ModuleList();
    }

    @Test
    public void parse_validInput_success() throws EquipmentMasterException {
        String input = "addmod n/CG2111A pax/150";
        AddModCommand command = AddModCommand.parse(input);

        // Command should be successfully created without throwing exceptions
        assertTrue(command instanceof AddModCommand);
    }

    @Test
    public void parse_missingPaxPrefix_throwsException() {
        // Missing the "pax/" keyword
        String input = "addmod n/CG2111A 150";

        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            AddModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("Invalid command format"));
    }

    @Test
    public void parse_nonIntegerPax_throwsException() {
        // Pax is provided as a string instead of an integer
        String input = "addmod n/CG2111A pax/abc";

        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            AddModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("valid integer"));
    }

    @Test
    public void parse_emptyModuleName_throwsException() {
        // Module name is completely empty
        String input = "addmod n/ pax/150";

        assertThrows(EquipmentMasterException.class, () -> {
            AddModCommand.parse(input);
        });
    }
}
