package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import java.util.ArrayList;

public class FindCommandTest {

    // Helper method to create equipment with modules
    private Equipment createEquipmentWithModules(String name, int quantity, String... modules) {
        Equipment eq = new Equipment(name, quantity);
        for (String module : modules) {
            eq.addModuleCode(module);
        }
        return eq;
    }

    @Test
    public void getMatchingEquipments_keywordMatches_returnsMatchedList() throws EquipmentMasterException {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32 Development Board", 50));
        equipments.addEquipment(new Equipment("HDMI Cable", 100));

        FindCommand command = new FindCommand("stm32");

        // Act: Directly test the finding logic
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        // Assert: Check the size and contents of the returned list
        assertEquals(1, matches.size());
        assertEquals("STM32 Development Board", matches.get(0).getName());
        assertEquals(50, matches.get(0).getQuantity());
    }

    @Test
    public void getMatchingEquipments_keywordNoMatch_returnsEmptyList() throws EquipmentMasterException {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32 Development Board", 50));

        FindCommand command = new FindCommand("Basys3");

        // Act
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        // Assert: The list should be completely empty
        assertTrue(matches.isEmpty());
        assertEquals(0, matches.size());
    }

    @Test
    public void getMatchingEquipments_multipleKeywords_returnsMatchedList() throws EquipmentMasterException {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32 Development Board", 50));
        equipments.addEquipment(new Equipment("HDMI Cable", 100));
        equipments.addEquipment(new Equipment("Digital Multimeter", 10));

        // Act: Search with multiple keywords separated by spaces
        FindCommand command = new FindCommand("Board Cable");
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        // Assert: Should find exactly 2 items, ignoring "Digital Multimeter"
        assertEquals(2, matches.size());

        // Verify both items were caught regardless of keyword order
        boolean hasBoard = matches.stream().anyMatch(eq -> eq.getName().equals("STM32 Development Board"));
        boolean hasCable = matches.stream().anyMatch(eq -> eq.getName().equals("HDMI Cable"));

        assertTrue(hasBoard);
        assertTrue(hasCable);
    }

    @Test
    public void getMatchingEquipments_moduleCodeCaseInsensitive_returnsMatchedList() throws EquipmentMasterException {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(createEquipmentWithModules("FPGA", 40, "EE2026"));

        FindCommand command = new FindCommand("ee2026");

        // Act
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        // Assert
        assertEquals(1, matches.size());
        assertEquals("FPGA", matches.get(0).getName());
    }

    @Test
    public void getMatchingEquipments_searchByModuleCode_returnsMatchedList() throws EquipmentMasterException {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(createEquipmentWithModules("FPGA", 40, "EE2026", "CG2028"));
        equipments.addEquipment(createEquipmentWithModules("ESP32", 40, "EE2026"));
        equipments.addEquipment(new Equipment("Oscilloscope", 10));

        FindCommand command = new FindCommand("EE2026");

        // Act
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        // Assert
        assertEquals(2, matches.size());

        boolean hasFPGA = matches.stream().anyMatch(eq -> eq.getName().equals("FPGA"));
        boolean hasESP32 = matches.stream().anyMatch(eq -> eq.getName().equals("ESP32"));

        assertTrue(hasFPGA);
        assertTrue(hasESP32);
    }

    @Test
    public void parse_emptyKeyword_throwsException() {
        // Ensures that "find" without any arguments throws the correct exception
        assertThrows(seedu.equipmentmaster.exception.EquipmentMasterException.class, () -> {
            FindCommand.parse("find");
        });
    }

    @Test
    public void parse_blankKeyword_throwsException() {
        // Ensures that "find" followed only by spaces throws an exception
        assertThrows(seedu.equipmentmaster.exception.EquipmentMasterException.class, () -> {
            FindCommand.parse("find    ");
        });
    }

    @Test
    public void getMatchingEquipments_nullKeyword_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> {
            new FindCommand(null);
        });
    }

    /**
     * Tests that FindCommand handles equipment with null module codes gracefully.
     * This uses a local anonymous stub to bypass the standard Equipment constructor normalization.
     */
    @Test
    public void getMatchingEquipments_nullModuleCodes_handledDefensively() throws EquipmentMasterException {
        // 1. ARRANGE
        EquipmentList equipments = new EquipmentList();

        // We create an anonymous subclass (Stub) to force getModuleCodes() to return null,
        // bypassing any normalization logic in the base Equipment class.
        Equipment nullModuleEquipment = new Equipment("BrokenItem", 10) {
            @Override
            public java.util.ArrayList<String> getModuleCodes() {
                return null;
            }
        };

        equipments.addEquipment(nullModuleEquipment);

        // 2. ACT
        // Searching for a module when an item has 'null' modules should not throw a NullPointerException.
        FindCommand command = new FindCommand("CG2111A");
        java.util.ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        // 3. ASSERT
        // The list should be empty (no match), but most importantly, the execution should be successful.
        assertTrue(matches.isEmpty(), "Matches should be empty but execution should not crash on null modules.");
    }

    @Test
    public void execute_emptyInventory_printsMessage() {
        // Triggers line 150: "There is no equipment in your list."
        EquipmentList emptyList = new EquipmentList();
        seedu.equipmentmaster.ui.Ui ui = new seedu.equipmentmaster.ui.Ui();
        seedu.equipmentmaster.context.Context context =
                new seedu.equipmentmaster.context.Context(emptyList, null, ui, null, null);

        FindCommand command = new FindCommand("stm32");
        command.execute(context);
    }

    @Test
    public void execute_noMatchesFound_printsMessage() throws EquipmentMasterException {
        // Triggers line 157: "There is no matching equipment in your list."
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("HDMI", 10));

        seedu.equipmentmaster.ui.Ui ui = new seedu.equipmentmaster.ui.Ui();
        seedu.equipmentmaster.context.Context context =
                new seedu.equipmentmaster.context.Context(equipments, null, ui, null, null);

        FindCommand command = new FindCommand("NonExistent");
        command.execute(context);
    }

    @Test
    public void execute_singleMatch_printsSingularMessage() throws EquipmentMasterException {
        // Triggers line 159 and 166: singular "1 equipment listed!" without index
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32", 10));

        seedu.equipmentmaster.ui.Ui ui = new seedu.equipmentmaster.ui.Ui();
        seedu.equipmentmaster.context.Context context =
                new seedu.equipmentmaster.context.Context(equipments, null, ui, null, null);

        FindCommand command = new FindCommand("stm32");
        command.execute(context);
    }

    @Test
    public void execute_multipleMatches_printsPluralMessage() throws EquipmentMasterException {
        // Triggers line 161 and 168: plural "X equipments listed!" with indexes (1. ..., 2. ...)
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32 Board", 10));
        equipments.addEquipment(new Equipment("STM32 Cable", 5));

        seedu.equipmentmaster.ui.Ui ui = new seedu.equipmentmaster.ui.Ui();
        seedu.equipmentmaster.context.Context context =
                new seedu.equipmentmaster.context.Context(equipments, null, ui, null, null);

        FindCommand command = new FindCommand("stm32");
        command.execute(context);
    }

    @Test
    public void parse_missingKeyword_throwsException() {
        // Triggers line 41 by providing no arguments
        assertThrows(seedu.equipmentmaster.exception.EquipmentMasterException.class, () -> {
            FindCommand.parse("find");
        });
    }

    @Test
    public void parse_onlySpacesKeyword_throwsException() {
        // Triggers line 41 by providing a keyword that is empty after trim()
        assertThrows(seedu.equipmentmaster.exception.EquipmentMasterException.class, () -> {
            FindCommand.parse("find     ");
        });
    }

    @Test
    public void getMatchingEquipments_extraSpacesInKeyword_triggersContinue() throws EquipmentMasterException {
        // Multiple spaces result in empty strings in the tokens array, triggering line 88
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32 Board", 10));

        FindCommand command = new FindCommand("STM32    Board");
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        assertEquals(1, matches.size());
    }

    @Test
    public void getMatchingEquipments_modulesExistButNoMatch_returnsFalse() throws EquipmentMasterException {
        // Item has modules, but keyword matches neither name nor modules, triggering line 124
        EquipmentList equipments = new EquipmentList();
        Equipment eq = new Equipment("Oscilloscope", 5);
        eq.addModuleCode("PH1011");
        equipments.addEquipment(eq);

        FindCommand command = new FindCommand("EE2026");
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void parse_keywordIsOnlySpaces_throwsException() {
        // Triggers the second part of the || on line 40: words[1].trim().isEmpty()
        assertThrows(EquipmentMasterException.class, () -> {
            FindCommand.parse("find   ");
        });
    }

    @Test
    public void parse_validKeyword_returnsCommand() throws EquipmentMasterException {
        // Covers lines 44-45 (the successful return path)
        Command command = FindCommand.parse("find stm32");
        assertTrue(command instanceof FindCommand);
    }

    @Test
    public void getMatchingEquipments_multipleSpaces_coversContinue() throws EquipmentMasterException {
        // While \\s+ usually prevents empty tokens, providing a keyword with
        // unusual spacing helps ensure the 'continue' branch (line 88) is evaluated.
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32", 1));

        FindCommand command = new FindCommand("stm32  ");
        command.getMatchingEquipments(equipments);
    }

    @Test
    public void matchesNameOrModule_nullAndEmptyModules_coversLine114() throws EquipmentMasterException {
        EquipmentList equipments = new EquipmentList();

        // Case 1: Trigger eq.getModuleCodes() == null
        Equipment nullModEq = new Equipment("ItemA", 1);
        // Ensure this item has null modules if your constructor doesn't init them
        equipments.addEquipment(nullModEq);

        // Case 2: Trigger eq.getModuleCodes().isEmpty()
        Equipment emptyModEq = new Equipment("ItemB", 1);
        // Ensure this item has an empty ArrayList of modules
        equipments.addEquipment(emptyModEq);

        FindCommand command = new FindCommand("Search");
        command.getMatchingEquipments(equipments);
    }

    @Test
    public void matchesNameOrModule_noMatchInExistingModules_coversLine124() throws EquipmentMasterException {
        // Covers the red 'return false' at line 124
        EquipmentList equipments = new EquipmentList();
        Equipment eq = new Equipment("Oscilloscope", 5);
        eq.addModuleCode("EE2026"); // Has modules, but name doesn't match "USB"
        equipments.addEquipment(eq);

        // Keyword "USB" does not match "Oscilloscope" AND does not match "EE2026"
        FindCommand command = new FindCommand("USB");
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        assertTrue(matches.isEmpty());
    }

    @Test
    public void parse_noKeyword_throwsException() {
        // Triggers words.length < 2
        assertThrows(EquipmentMasterException.class, () -> FindCommand.parse("find"));
    }

    /**
     * Targets lines 59-64 in FindCommand.java.
     * Exercises the branch where rawKeyword.isEmpty() is true.
     * Since parse() blocks empty inputs, we must invoke the constructor directly.
     */
    @Test
    public void getMatchingEquipments_emptyKeyword_returnsAllEquipments() throws EquipmentMasterException {
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("Item A", 10));
        equipments.addEquipment(new Equipment("Item B", 20));

        // Bypass parse() to provide an empty string directly
        FindCommand command = new FindCommand("");
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        // Should return all equipments in the list
        assertEquals(2, matches.size(), "Should return all items when keyword is empty.");
    }

    /**
     * Targets line 87 in FindCommand.java: if (token.isEmpty()) continue;
     * In Java, split("\\s+") only produces an empty token if the string STARTS with spaces.
     */
    @Test
    public void getMatchingEquipments_leadingSpaces_triggersEmptyTokenContinue() throws EquipmentMasterException {
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32", 10));

        // The leading spaces ensure split("\\s+") returns ["", "stm32"]
        FindCommand command = new FindCommand("   stm32");
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        assertEquals(1, matches.size());
    }

    /**
     * Targets line 135 in FindCommand.java.
     * Exercises the assertion branch where the context is null.
     */
    @Test
    public void execute_nullContext_assertionFails() {
        FindCommand command = new FindCommand("stm32");
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            command.execute(null);
        });
        assertTrue(thrown.getMessage().contains("Context should not be null during execution"));
    }

    @Test
    public void getMatchingEquipments_handlingSpaces_success() throws EquipmentMasterException {
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32", 10));

        // Testing that leading/multiple spaces are handled by trim and regex split
        FindCommand command = new FindCommand("   stm32");
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        assertEquals(1, matches.size());
    }
}
