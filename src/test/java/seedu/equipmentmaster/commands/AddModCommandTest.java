//@@author Hongyu1231
package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.module.Module;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit tests for the {@code AddModCommand} class.
 * Tests parsing logic, validation rules, and execution behavior including error handling.
 */
public class AddModCommandTest {

    private ModuleList moduleList;
    private Ui ui;
    private Context context;

    @BeforeEach
    public void setUp() {
        moduleList = new ModuleList();
        ui = new Ui();
        // Standard context for execution tests
        context = new Context(null, moduleList, ui, null, null);
    }

    @Test
    public void parse_validInput_success() throws EquipmentMasterException {
        String input = "addmod n/CG2111A pax/150";
        AddModCommand command = AddModCommand.parse(input);
        assertTrue(command instanceof AddModCommand);
    }

    @Test
    public void parse_invalidFormat_throwsException() {
        // Missing the "pax/" keyword
        String input = "addmod n/CG2111A 150";
        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            AddModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("Invalid command format"));
    }

    @Test
    public void parse_nonIntegerPax_throwsException() {
        String input = "addmod n/CG2111A pax/abc";
        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            AddModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("Invalid pax value"));
    }

    @Test
    public void parse_emptyModuleName_throwsException() {
        // Module name is whitespace only
        String input = "addmod n/   pax/150";
        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            AddModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("Module name cannot be empty"));
    }

    @Test
    public void parse_reservedCharacters_throwsException() {
        // Testing '|', ',', and '=' validation
        String[] invalidInputs = {
            "addmod n/CG|2111 pax/100",
            "addmod n/CG,2111 pax/100",
            "addmod n/CG=2111 pax/100"
        };

        for (String input : invalidInputs) {
            EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
                AddModCommand.parse(input);
            });
            assertTrue(thrown.getMessage().contains("Name contains reserved characters"),
                    "Failed to reject reserved character in: " + input);
        }
    }

    @Test
    public void constructor_negativePax_throwsException() {
        assertThrows(EquipmentMasterException.class, () -> {
            new AddModCommand("CG2111A", -5);
        });
    }

    @Test
    public void execute_validModule_success() throws EquipmentMasterException {
        AddModCommand command = new AddModCommand("CG2111A", 150);
        command.execute(context);
        assertTrue(moduleList.hasModule("CG2111A"));
        assertEquals(1, moduleList.getModules().size());
    }

    @Test
    public void execute_duplicateModule_throwsException() throws EquipmentMasterException {
        // Pre-add a module
        moduleList.addModule(new Module("CG2111A", 150));

        AddModCommand command = new AddModCommand("CG2111A", 200);

        // Execute should now THROW the exception to the caller/test
        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(context);
        });
        assertTrue(thrown.getMessage().contains("already exists"));
    }

    @Test
    public void execute_storageFailure_handledGracefully() throws EquipmentMasterException {
        // Create a stub Storage that fails during save
        Storage faultyStorage = new Storage("e.txt", ui, "s.txt", "m.txt") {
            @Override
            public void saveModules(ModuleList list) throws EquipmentMasterException {
                throw new EquipmentMasterException("Simulated disk failure");
            }
        };

        Context faultyContext = new Context(null, moduleList, ui, faultyStorage, null);
        AddModCommand command = new AddModCommand("CG2111A", 150);

        // Should NOT throw exception because execute() catches storage errors internally
        command.execute(faultyContext);

        // Logic check: Module should still be in memory despite storage failure
        assertTrue(moduleList.hasModule("CG2111A"));
    }

    @Test
    public void parse_largePaxValue_throwsException() {
        // Triggers NumberFormatException via overflow
        String input = "addmod n/CG2111A pax/999999999999999";
        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            AddModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("Invalid pax value"));
    }
}
