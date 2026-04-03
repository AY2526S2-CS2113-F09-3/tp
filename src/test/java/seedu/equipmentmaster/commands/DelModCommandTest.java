//@@author Hongyu1231
package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.storage.Storage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DelModCommandTest {

    private ModuleList moduleList;
    private Ui ui;
    private Storage storage;
    private Context context;

    @BeforeEach
    public void setUp() {
        moduleList = new ModuleList();
        ui = new Ui();
        // Standard 4-parameter Storage constructor
        storage = new Storage("e.txt", ui, "s.txt", "m.txt");
        // Standard Context constructor
        context = new Context(null, moduleList, ui, storage, null);
    }

    @Test
    public void parse_validInput_success() throws EquipmentMasterException {
        // Verifies that a correctly formatted delete command is parsed
        DelModCommand command = DelModCommand.parse("delmod n/CG2111A");
        assertTrue(command instanceof DelModCommand);
    }

    @Test
    public void parse_invalidPrefix_throwsException() {
        // Ensures the parser rejects inputs missing the "n/" prefix
        assertThrows(EquipmentMasterException.class, () -> {
            DelModCommand.parse("delmod CG2111A");
        });
    }

    @Test
    public void parse_emptyName_throwsException() {
        // Ensures the parser rejects commands with an empty module name
        assertThrows(EquipmentMasterException.class, () -> {
            DelModCommand.parse("delmod n/  ");
        });
    }

    @Test
    public void execute_existingModule_success() throws EquipmentMasterException {
        // Setup: add a module to be deleted
        moduleList.addModule(new seedu.equipmentmaster.module.Module("CG2111A", 150));

        DelModCommand command = new DelModCommand("CG2111A");
        command.execute(context);

        // Verification: module list should no longer contain the module
        assertFalse(moduleList.hasModule("CG2111A"));
    }

    @Test
    public void execute_nonExistentModule_throwsException() {
        // Verifies that trying to delete a non-existent module throws an exception
        DelModCommand command = new DelModCommand("NON_EXISTENT");

        assertThrows(EquipmentMasterException.class, () -> {
            command.execute(context);
        });
    }

    @Test
    public void execute_storageSaveFailure_handlesGracefully() throws EquipmentMasterException {
        // To cover the catch block for storage failures, we use a stub that throws an exception
        moduleList.addModule(new seedu.equipmentmaster.module.Module("CG2111A", 150));

        Storage faultyStorage = new Storage("e.txt", ui, "s.txt", "m.txt") {
            @Override
            public void saveModules(ModuleList list) throws EquipmentMasterException {
                throw new EquipmentMasterException("Simulated save failure");
            }
        };

        Context faultyContext = new Context(null, moduleList, ui, faultyStorage, null);
        DelModCommand command = new DelModCommand("CG2111A");

        // The execution should catch the storage exception internally per your code logic
        command.execute(faultyContext);

        // Ensure the module was still removed from memory even if saving to disk failed
        assertFalse(moduleList.hasModule("CG2111A"));
    }
}
