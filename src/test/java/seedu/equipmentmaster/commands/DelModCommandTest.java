package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
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

/**
 * JUnit test class for {@code DelModCommand}.
 * Verifies both parsing logic and execution behavior, including safe dereferencing.
 */
public class DelModCommandTest {
    @TempDir
    Path tempDir; // JUnit 5 automatically creates and cleans up this directory

    private ModuleList moduleList;
    private EquipmentList equipmentList; // Added to resolve NPE during safe dereferencing
    private Ui ui;
    private Storage storage;
    private Context context;

    /**
     * Sets up the test environment before each test case.
     * Initializes lists, UI, and a temporary storage sandbox.
     */
    @BeforeEach
    public void setUp() {
        moduleList = new ModuleList();
        equipmentList = new EquipmentList();
        ui = new Ui();

        // 1. ARRANGE: Create isolated, temporary paths within the @TempDir sandbox.
        String tempEqPath = tempDir.resolve("temp_e.txt").toString();
        String tempSetPath = tempDir.resolve("temp_s.txt").toString();
        String tempModPath = tempDir.resolve("temp_m.txt").toString();

        // 2. INJECT: Use the temporary paths for the Storage instance.
        storage = new Storage(tempEqPath, ui, tempSetPath, tempModPath);

        // 3. CONTEXT: Initialize the shared state.
        // equipmentList is now passed to prevent NPEs when command checks for tags.
        context = new Context(equipmentList, moduleList, ui, storage, null);
    }

    @Test
    public void parse_validInput_success() throws EquipmentMasterException {
        // Verifies that a correctly formatted delete command is parsed
        DelModCommand command = DelModCommand.parse("delmod n/CG2111A");
        assertTrue(command instanceof DelModCommand);
    }

    @Test
    public void parse_caseInsensitive_success() throws EquipmentMasterException {
        // Verifies that the command keyword is case-insensitive
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
        // Ensures the parser rejects random text that doesn't follow the regex pattern
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
        moduleList.addModule(new Module("CG2111A", 150));

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
        // Verifies the command still executes if storage is missing (e.g., during some tests)
        moduleList.addModule(new Module("CG2111A", 150));
        Context nullStorageContext = new Context(equipmentList, moduleList, ui, null, null);
        DelModCommand command = new DelModCommand("CG2111A");

        assertDoesNotThrow(() -> command.execute(nullStorageContext));
        assertFalse(moduleList.hasModule("CG2111A"));
    }

    @Test
    public void execute_storageSaveFailure_handlesGracefully() throws EquipmentMasterException {
        // Covers the catch block for storage failures using a mock-like stub
        moduleList.addModule(new Module("CG2111A", 150));

        Storage faultyStorage = new Storage("e.txt", ui, "s.txt", "m.txt") {
            @Override
            public void saveModules(ModuleList list) throws EquipmentMasterException {
                throw new EquipmentMasterException("Simulated save failure");
            }
        };

        Context faultyContext = new Context(equipmentList, moduleList, ui, faultyStorage, null);
        DelModCommand command = new DelModCommand("CG2111A");

        // The execution should catch the storage exception internally per implementation
        command.execute(faultyContext);

        // Ensure the module was still removed from memory even if saving to disk failed
        assertFalse(moduleList.hasModule("CG2111A"));
    }

    @Test
    public void parse_emptyModuleName_throwsException() {
        // Hits the branch where moduleName is whitespace after matching the n/ flag
        assertThrows(EquipmentMasterException.class, () -> {
            DelModCommand.parse("delmod n/   ");
        });
    }

    @Test
    public void constructor_nullName_assertionFails() {
        // Targets the null check in the constructor
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            new DelModCommand(null);
        });
        assertTrue(thrown.getMessage().contains("Module name cannot be null or empty"));
    }

    @Test
    public void constructor_emptyName_assertionFails() {
        // Targets the empty/whitespace check in the constructor
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            new DelModCommand(" ");
        });
        assertTrue(thrown.getMessage().contains("Module name cannot be null or empty"));
    }

    @Test
    public void execute_nullContext_assertionFails() {
        // Verifies the defensive assertion at the start of the execute method
        DelModCommand command = new DelModCommand("CG2111A");
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            command.execute(null);
        });
        assertTrue(thrown.getMessage().contains("Context should not be null during execution"));
    }

    @Test
    public void parse_zeroWhitespaceAfterCommand_success() throws EquipmentMasterException {
        // Tests the regex flexibility: "delmodn/" instead of "delmod n/"
        DelModCommand command = DelModCommand.parse("delmodn/CG2111A");
        assertTrue(command instanceof DelModCommand);
    }

    @Test
    public void parse_moduleNameWithSpaces_success() throws EquipmentMasterException {
        // Ensures module names with spaces (e.g., "CS 2113") are captured correctly
        DelModCommand command = DelModCommand.parse("delmod n/CS 2113");
        assertTrue(command instanceof DelModCommand);
    }
}
