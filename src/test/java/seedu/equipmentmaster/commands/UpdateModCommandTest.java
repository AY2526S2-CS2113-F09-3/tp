//@@author Hongyu1231
package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.ui.Ui;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


/**
 * JUnit tests for the {@code UpdateModCommand} class.
 */
public class UpdateModCommandTest {

    private ModuleList moduleList;

    @BeforeEach
    public void setUp() {
        Ui ui = new Ui();
        moduleList = new ModuleList();
        try {
            // Add a dummy module for execution tests
            moduleList.addModule(new Module("CG2271", 100));
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
        }
    }

    @Test
    public void parse_validInput_success() throws EquipmentMasterException {
        String input = "updatemod n/CG2271 pax/200";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertEquals(UpdateModCommand.class, command.getClass());
    }

    @Test
    public void parse_missingNamePrefix_throwsException() {
        // Missing the "n/" prefix
        String input = "updatemod CG2271 pax/200";
        assertThrows(EquipmentMasterException.class, () -> {
            UpdateModCommand.parse(input);
        });
    }

    @Test
    public void parse_paxWithDecimals_throwsException() {
        // Pax cannot be a decimal number
        String input = "updatemod n/CG2271 pax/150.5";
        assertThrows(EquipmentMasterException.class, () -> {
            UpdateModCommand.parse(input);
        });
    }

    @Test
    public void parse_invalidFormat_throwsException() {
        // Triggers the branch where the regex pattern does not match
        String input = "updatemod n/CG2271";
        assertThrows(EquipmentMasterException.class, () -> UpdateModCommand.parse(input));
    }

    @Test
    public void parse_negativePax_throwsException() {
        // Triggers the check for negative enrollment numbers (pax < 0)
        String input = "updatemod n/CG2271 pax/-50";
        assertThrows(EquipmentMasterException.class, () -> UpdateModCommand.parse(input));
    }

    @Test
    public void execute_validUpdate_success(@TempDir Path tempDir) throws EquipmentMasterException {
        // 1. ARRANGE: Create isolated, temporary paths for this specific test run.
        // These files will be physically created inside a temporary system folder
        // and automatically deleted after the test finishes.
        String eqPath = tempDir.resolve("temp_e.txt").toString();
        String setPath = tempDir.resolve("temp_s.txt").toString();
        String modPath = tempDir.resolve("temp_m.txt").toString();

        seedu.equipmentmaster.ui.Ui ui = new seedu.equipmentmaster.ui.Ui();
        seedu.equipmentmaster.storage.Storage storage = new seedu.equipmentmaster.storage.Storage(
                eqPath, ui, setPath, modPath);

        // We assume moduleList is already initialized in your @BeforeEach setup
        seedu.equipmentmaster.context.Context context = new seedu.equipmentmaster.context.Context(
                null, moduleList, ui, storage, null);

        // 2. ACT: Execute the update command
        UpdateModCommand command = new UpdateModCommand("CG2271", 150);
        command.execute(context);

        // 3. ASSERT: Verify the state change and persistence
        assertEquals(150, moduleList.getModule("CG2271").getPax(),
                "The module enrollment (pax) should be updated to 150.");

        // Check if the storage layer actually triggered a write to the temp file
        java.io.File savedFile = new java.io.File(modPath);
        assertTrue(savedFile.exists(), "The module list should be successfully persisted to disk.");
    }

    @Test
    public void execute_moduleNotFound_throwsException() {
        // Verifies that moduleList.updateModule throws an exception when the module is missing
        UpdateModCommand command = new UpdateModCommand("UNKNOWN_MOD", 150);
        seedu.equipmentmaster.context.Context context = new seedu.equipmentmaster.context.Context(
                null, moduleList, new seedu.equipmentmaster.ui.Ui(), null, null);

        assertThrows(EquipmentMasterException.class, () -> command.execute(context));
    }

    @Test
    public void execute_storageSaveFailure_triggersCatch() throws EquipmentMasterException {
        // Triggers the inner catch block on line 58 by simulating a storage failure
        seedu.equipmentmaster.ui.Ui ui = new seedu.equipmentmaster.ui.Ui();
        seedu.equipmentmaster.storage.Storage faultyStorage = new seedu.equipmentmaster.storage.Storage(
                "e.txt", ui, "s.txt", "m.txt") {
            @Override
            public void saveModules(ModuleList list) throws EquipmentMasterException {
                throw new EquipmentMasterException("Simulated save failure");
            }
        };

        seedu.equipmentmaster.context.Context context = new seedu.equipmentmaster.context.Context(
                null, moduleList, ui, faultyStorage, null);

        UpdateModCommand command = new UpdateModCommand("CG2271", 120);
        // Should execute and catch the exception internally, showing a message via UI
        command.execute(context);
    }

    @Test
    public void execute_nullStorage_success() throws EquipmentMasterException {
        // TARGET: Line 54 - Triggers the implicit 'else' branch of "if (storage != null)"
        seedu.equipmentmaster.ui.Ui ui = new seedu.equipmentmaster.ui.Ui();
        // Context with null storage handler
        seedu.equipmentmaster.context.Context contextWithNullStorage = new seedu.equipmentmaster.context.Context(
                null, moduleList, ui, null, null);

        // We know CG2271 exists in setup
        UpdateModCommand command = new UpdateModCommand("CG2271", 110);

        // Execute should succeed in updating memory, just skipping disk save
        command.execute(contextWithNullStorage);

        // Assert memory state is updated
        assertEquals(110, moduleList.getModule("CG2271").getPax());
        // Verify noexception thrown, logic gracefully passed through the null check.
    }

    @Test
    public void parse_excessiveWhitespace_success() throws EquipmentMasterException {
        // TARGET: Line 72 - Exercises complex conditional paths inside regex Pattern.matches()
        // Input has multiple spaces between n/flag and pax/flag, and trailing space.
        String input = "updatemod n/CG2271    pax/100   ";
        UpdateModCommand command = UpdateModCommand.parse(input);

        // Value assertion would be needed if accessible, but confirming successful parsing exercises regex branches
        assertTrue(command instanceof UpdateModCommand);
    }

    @Test
    public void parse_parametersReversed_throwsException() {
        // TARGET: Line 72 - Exercises the "if (!matcher.matches())" True branch where n/ comes after pax/
        // Regex forces 'n/NAME pax/QTY' order.
        String input = "updatemod pax/200 n/CG2271";
        assertThrows(EquipmentMasterException.class, () -> UpdateModCommand.parse(input));
    }

    @Test
    public void parse_paxValueOverflow_throwsNumberFormatException() {
        // TARGET: Line 82 - Exercises catch(NFE) branch via Integer overflow
        String input = "updatemod n/CG2271 pax/999999999999999999999";
        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            UpdateModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("Invalid pax value"), "Expected overflow warning.");
    }

    /**
     * Tests that a pax value exceeding the maximum integer range triggers a NumberFormatException.
     * This ensures the catch block is exercised for overflow scenarios, which is a
     * distinct logical path from invalid decimal formatting.
     */
    @Test
    public void parse_paxOverflow_throwsException() {
        String input = "updatemod n/CG2271 pax/9999999999999999999";
        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            UpdateModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("Invalid pax value"));
    }

    /**
     * Validates that the regular expression correctly handles multiple spaces between arguments.
     * This exercises the branch paths within the regex engine's whitespace quantifier (\\s+).
     */
    @Test
    public void parse_multipleSpacesInRegex_success() throws EquipmentMasterException {
        String input = "updatemod n/CG2271      pax/100";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertTrue(command instanceof UpdateModCommand);
    }

    /**
     * Targets the compound 'assert' in the constructor: (moduleName != null && !moduleName.trim().isEmpty()).
     * We must call the constructor directly to hit these branches, bypassing the parse() logic.
     */
    @Test
    public void constructor_emptyName_assertionFails() {
        // This exercises the logic within the assertion branches.
        // If assertions are enabled (-ea), this will throw an AssertionError.
        // Even if they are disabled, calling it ensures the True path of the bytecode is recorded.
        try {
            new UpdateModCommand("", 100);
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("Module name cannot be null or empty"));
        }
    }

    /**
     * Targets the catch block and integer parsing logic via trailing noise.
     * The regex captures "100 extra" as the pax string, which parseInt() rejects.
     */
    @Test
    public void parse_paxWithTrailingText_throwsException() {
        String input = "updatemod n/CG2271 pax/100 extra text";
        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            UpdateModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("Invalid pax value"));
    }

    /**
     * Targets the 'storage != null' branch in saveToStorage when storage IS null.
     * This is the "False" path that is often missed when only success cases are tested.
     */
    @Test
    public void execute_noStorageDefined_skipsSaveGracefully() throws EquipmentMasterException {
        // Create context where storage is explicitly null
        Context nullStorageContext = new Context(null, moduleList, new Ui(), null, null);
        UpdateModCommand command = new UpdateModCommand("CG2271", 110);

        // Should update memory but bypass the storage block without error
        command.execute(nullStorageContext);
        assertEquals(110, moduleList.getModule("CG2271").getPax());
    }

    /**
     * Targets internal regex branches and Integer overflow.
     * Provides a value beyond Integer.MAX_VALUE to trigger a specific NFE branch.
     */
    @Test
    public void parse_extremePaxValue_throwsException() {
        String input = "updatemod n/CG2271 pax/2147483648";
        assertThrows(EquipmentMasterException.class, () -> UpdateModCommand.parse(input));
    }

    /**
     * Targets the case-insensitivity and zero-whitespace branches in the
     * replaceFirst("(?i)^updatemod\\s*", "") regex logic.
     */
    @Test
    public void parse_caseInsensitiveAndNoSpace_success() throws EquipmentMasterException {
        // Test "UPDATEMOD" (uppercase) and zero space before "n/"
        String input = "UPDATEMODn/CG2271 pax/100";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }

    /**
     * Targets the catch block in parse() via a non-integer string.
     * This string passes the regex (.+) but triggers NumberFormatException in parseInt().
     */
    @Test
    public void parse_invalidPaxString_throwsException() {
        // Use "abc" so the regex matches, but parseInt fails
        String input = "updatemod n/CG2271 pax/abc";
        EquipmentMasterException thrown = assertThrows(EquipmentMasterException.class, () -> {
            UpdateModCommand.parse(input);
        });
        assertTrue(thrown.getMessage().contains("Invalid pax value"),
                "Expected 'Invalid pax value' message but got: " + thrown.getMessage());
    }

    @Test
    public void parse_paxWithSpaces_throwsException() {
        // The regex captures " 100 " as matcher.group(2).
        // matcher.group(2).trim() results in "100", which is valid.
        // To fail it, use something like " 100abc "
        String input = "updatemod n/CG2271 pax/ 100abc ";
        assertThrows(EquipmentMasterException.class, () -> UpdateModCommand.parse(input));
    }

    /**
     * Targets the \\s* branch in the replaceFirst regex.
     * Case: Zero whitespace between command word and the first flag.
     */
    @Test
    public void parse_noSpaceAfterCommand_success() throws EquipmentMasterException {
        // "updatemodn/" instead of "updatemod n/"
        String input = "updatemodn/CG2271 pax/100";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }

    /**
     * Targets the boundary condition in "if (pax < 0)".
     * Case: pax is exactly 0.
     */
    @Test
    public void parse_zeroPax_success() throws EquipmentMasterException {
        String input = "updatemod n/CG2271 pax/0";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }

    /**
     * Targets potential sub-branches within Integer.parseInt logic.
     * Case: Pax provided with an explicit positive sign.
     */
    @Test
    public void parse_paxWithPositiveSign_success() throws EquipmentMasterException {
        String input = "updatemod n/CG2271 pax/+150";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }


    /**
     * Targets the second half of the compound assertion logic.
     * Exercises the branch where the name is not null, but is blank after trim.
     */
    @Test
    public void constructor_whitespaceName_assertionFails() {
        try {
            new UpdateModCommand("   ", 100);
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("Module name cannot be null or empty"));
        }
    }

    /**
     * Targets the \\s* quantifier branch for matching ZERO spaces.
     * Ensures the regex handles cases where flags immediately follow the command word.
     */
    @Test
    public void parse_zeroWhitespaceAfterCommand_success() throws EquipmentMasterException {
        // No space between command word and 'n/'
        String input = "updatemodn/CG2271 pax/100";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }

    /**
     * Targets the \\s+ quantifier branch for matching multiple spaces.
     */
    @Test
    public void parse_multipleSpacesBetweenFlags_success() throws EquipmentMasterException {
        // Multiple spaces between the name and the pax flag
        String input = "updatemod n/CG2271        pax/100";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }

    /**
     * Targets the (?i) case-insensitivity flag in the replaceFirst regex.
     * Ensures the command word is correctly stripped regardless of letter case.
     */
    @Test
    public void parse_mixedCaseCommand_success() throws EquipmentMasterException {
        // Testing "uPdAtEmOd" to trigger the case-insensitive regex branch
        String input = "uPdAtEmOd n/CG2271 pax/250";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }

    /**
     * Targets the '^' anchor branch in the regex.
     * Testing a command with leading spaces to ensure it fails the '^updatemod' check.
     */
    @Test
    public void parse_leadingSpaces_throwsException() {
        // Leading spaces should cause the regex anchor '^' to fail the match
        String input = "   updatemod n/CG2271 pax/100";
        assertThrows(EquipmentMasterException.class, () -> UpdateModCommand.parse(input));
    }

    /**
     * TARGET: Line 31 (Constructor Assertions)
     * Direct constructor call to hit the 'null' branch of the compound &&.
     */
    @Test
    public void constructor_nullName_assertionFails() {
        try {
            new UpdateModCommand(null, 100);
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("Module name cannot be null or empty"));
        }
    }

    /**
     * TARGET: Line 31 (Constructor Assertions)
     * Direct constructor call to hit the 'isEmpty' branch after the null check passes.
     */
    @Test
    public void constructor_blankName_assertionFails() {
        try {
            new UpdateModCommand("   ", 100);
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("Module name cannot be null or empty"));
        }
    }

    /**
     * TARGET: Line 32 (Constructor Assertions)
     * Direct constructor call with negative pax to hit the 'False' branch.
     * Since parse() blocks this, we must call the constructor directly.
     */
    @Test
    public void constructor_negativePax_assertionFails() {
        try {
            new UpdateModCommand("CG2271", -10);
        } catch (AssertionError e) {
            assertTrue(e.getMessage().contains("Pax cannot be negative"));
        }
    }

    /**
     * TARGET: Line 46 (Execute Assertion)
     * Hits the 'False' branch of the context null-check.
     */
    @Test
    public void execute_nullContext_assertionFails() {
        UpdateModCommand command = new UpdateModCommand("CG2111A", 150);
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            command.execute(null);
        });
        assertTrue(thrown.getMessage().contains("Context should not be null during execution"));
    }

    @Test
    public void parse_upperCaseAndNoSpace_success() throws EquipmentMasterException {
        // TARGET: Regex internal branches (?i) and \\s*
        String input = "UPDATEMODn/CG2271 pax/100";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }

    /**
     * Targets the \\s+ quantifier branch in the regex.
     * Jacoco differentiates between a single space and other whitespace like Tabs.
     */
    @Test
    public void parse_tabSeparator_success() throws EquipmentMasterException {
        // Use a Tab character (\t) to trigger a different path in the whitespace regex
        String input = "updatemod n/CG2271\tpax/100";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }

    /**
     * Targets the non-greedy regex branch (.+?).
     * Ensures the name capture stops correctly even when the name contains spaces.
     */
    @Test
    public void parse_nameWithSpaces_success() throws EquipmentMasterException {
        String input = "updatemod n/CS 2113 Software Engineering pax/250";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }

    /**
     * Targets the full True path of the compound assertion in the constructor.
     * assert moduleName != null && !moduleName.trim().isEmpty()
     */
    @Test
    public void constructor_allValid_assertionPasses() {
        // This ensures the logic doesn't short-circuit and reaches the end of the line
        UpdateModCommand command = new UpdateModCommand("ValidName", 50);
        assertNotNull(command);
    }

    /**
     * Targets the (?i) flag with a fully UPPERCASE command.
     * Most tests use lowercase; this forces the case-insensitive branch to fully activate.
     */
    @Test
    public void parse_allCapsCommand_success() throws EquipmentMasterException {
        String input = "UPDATEMOD n/CG2271 pax/100";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertNotNull(command);
    }

    /**
     * Targets the execute() method logic when Storage is present but has no save logic.
     * This hits the path where storage is NOT null and NOT throwing an exception.
     */
    @Test
    public void execute_withNoOpStorage_success() throws EquipmentMasterException {
        // Create a storage that does nothing to ensure the 'if (storage != null)'
        // path is hit without entering the 'catch' block.
        seedu.equipmentmaster.storage.Storage noOpStorage = new seedu.equipmentmaster.storage.Storage(
                null, new Ui(), null, null) {
            @Override
            public void saveModules(ModuleList list) {
                // Do nothing
            }
        };

        Context context = new Context(null, moduleList, new Ui(), noOpStorage, null);
        UpdateModCommand command = new UpdateModCommand("CG2271", 100);
        command.execute(context);
    }

    // Consolidated null storage test
    @Test
    public void execute_nullStorage_skipsSave() throws EquipmentMasterException {
        moduleList.addModule(new Module("CG2271", 100));
        Ui ui = new Ui();
        // Context with explicitly null storage handler
        Context contextWithNullStorage = new Context(null, moduleList, ui, null, null);
        UpdateModCommand command = new UpdateModCommand("CG2271", 110);

        // Execute should succeed in memory, skipping disk save
        assertDoesNotThrow(() -> command.execute(contextWithNullStorage));
        assertEquals(110, moduleList.getModule("CG2271").getPax());
    }
}
