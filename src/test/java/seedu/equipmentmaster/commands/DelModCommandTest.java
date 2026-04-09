//@@author Hongyu1231
package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.module.Module;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DelModCommandTest {
    @TempDir
    Path tempDir; // JUnit 5 automatically creates and cleans up this directory

    private ModuleList moduleList;
    private Ui ui;
    private Storage storage;
    private Context context;

    @BeforeEach
    public void setUp() {
        moduleList = new ModuleList();
        ui = new Ui();

        // 1. ARRANGE: Create isolated, temporary paths within the @TempDir sandbox.
        // These paths are unique to each test run and won't pollute your project root.
        String tempEqPath = tempDir.resolve("temp_e.txt").toString();
        String tempSetPath = tempDir.resolve("temp_s.txt").toString();
        String tempModPath = tempDir.resolve("temp_m.txt").toString();

        // 2. INJECT: Use the temporary paths for the Storage instance.
        storage = new Storage(tempEqPath, ui, tempSetPath, tempModPath);

        // 3. CONTEXT: Initialize the shared state for the commands.
        context = new Context(null, moduleList, ui, storage, null);
    }

    @Test
    public void parse_validInput_success() throws EquipmentMasterException {
        // Verifies that a correctly formatted delete command is parsed
        DelModCommand command = DelModCommand.parse("delmod n/CG2111A");
        assertTrue(command instanceof DelModCommand);
    }

    @Test
    public void parse_caseInsensitive_success() throws EquipmentMasterException {
        DelModCommand command = DelModCommand.parse("DELMOD n/CG2111A");
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
    public void parse_noMatcherMatch_throwsException() {
        assertThrows(EquipmentMasterException.class, () -> {
            DelModCommand.parse("delmod random_text");
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
    public void execute_storageIsNull_success() throws EquipmentMasterException {
        moduleList.addModule(new Module("CG2111A", 150));
        Context nullStorageContext = new Context(null, moduleList, ui, null, null);
        DelModCommand command = new DelModCommand("CG2111A");

        assertDoesNotThrow(() -> command.execute(nullStorageContext));
        assertFalse(moduleList.hasModule("CG2111A"));
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

    @Test
    public void parse_emptyModuleName_throwsException() {
        // HITS BRANCH: if (moduleName.isEmpty()) after matching n/
        assertThrows(EquipmentMasterException.class, () -> {
            DelModCommand.parse("delmod n/   ");
        });
    }

    /**
     * Targets the ternary operator branch (moduleName == null)
     * and the subsequent assertion logic in the constructor.
     */
    @Test
    public void constructor_nullName_assertionFails() {
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            new DelModCommand(null);
        });
        assertTrue(thrown.getMessage().contains("Module name cannot be null or empty"));
    }

    /**
     * Targets the assertion branch where the name is not null,
     * but becomes empty after the trim() operation.
     */
    @Test
    public void constructor_emptyName_assertionFails() {
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            new DelModCommand(" ");
        });
        assertTrue(thrown.getMessage().contains("Module name cannot be null or empty"));
    }

    /**
     * Targets the 'assert context != null' branch in the execute() method.
     */
    @Test
    public void execute_nullContext_assertionFails() {
        DelModCommand command = new DelModCommand("CG2111A");
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            command.execute(null);
        });
        assertTrue(thrown.getMessage().contains("Context should not be null during execution"));
    }

    /**
     * Targets the regex zero-whitespace quantifier (\\s*) branch.
     * Evaluates the behavior when 'delmod' is immediately followed by 'n/'.
     */
    @Test
    public void parse_zeroWhitespaceAfterCommand_success() throws EquipmentMasterException {
        // "delmodn/" instead of "delmod n/"
        DelModCommand command = DelModCommand.parse("delmodn/CG2111A");
        assertTrue(command instanceof DelModCommand);
    }

    /**
     * Targets the regex (.+) to ensure it correctly captures module names containing spaces.
     */
    @Test
    public void parse_moduleNameWithSpaces_success() throws EquipmentMasterException {
        DelModCommand command = DelModCommand.parse("delmod n/CS 2113");
        assertTrue(command instanceof DelModCommand);
    }
}
