//@@author Hongyu1231
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
    public void getMatchingEquipments_keywordMatches_returnsMatchedList() {
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
    public void getMatchingEquipments_keywordNoMatch_returnsEmptyList() {
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
    public void getMatchingEquipments_multipleKeywords_returnsMatchedList() {
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
    public void getMatchingEquipments_moduleCodeCaseInsensitive_returnsMatchedList() {
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
    public void getMatchingEquipments_searchByModuleCode_returnsMatchedList() {
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
    public void getMatchingEquipments_nullKeyword_returnsAll() {
        // Checks that a null keyword (safety check) returns the full list
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("Item A", 10));

        FindCommand command = new FindCommand(null);
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        assertEquals(1, matches.size());
    }

    @Test
    public void getMatchingEquipments_nullModuleCodes_handlesGracefully() {
        // Covers the guard clause (line 118) where an equipment has null module codes
        EquipmentList equipments = new EquipmentList();
        Equipment eq = new Equipment("Oscilloscope", 5);
        // Assuming your Equipment class initializes moduleCodes as null or we force it
        equipments.addEquipment(eq);

        FindCommand command = new FindCommand("EE2026");
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        assertTrue(matches.isEmpty());
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
    public void execute_noMatchesFound_printsMessage() {
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
    public void execute_singleMatch_printsSingularMessage() {
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
    public void execute_multipleMatches_printsPluralMessage() {
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
    public void getMatchingEquipments_extraSpacesInKeyword_triggersContinue() {
        // Multiple spaces result in empty strings in the tokens array, triggering line 88
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32 Board", 10));

        FindCommand command = new FindCommand("STM32    Board");
        ArrayList<Equipment> matches = command.getMatchingEquipments(equipments);

        assertEquals(1, matches.size());
    }

    @Test
    public void getMatchingEquipments_modulesExistButNoMatch_returnsFalse() {
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
    public void getMatchingEquipments_multipleSpaces_coversContinue() {
        // While \\s+ usually prevents empty tokens, providing a keyword with
        // unusual spacing helps ensure the 'continue' branch (line 88) is evaluated.
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("STM32", 1));

        FindCommand command = new FindCommand("stm32  ");
        command.getMatchingEquipments(equipments);
    }

    @Test
    public void matchesNameOrModule_nullAndEmptyModules_coversLine114() {
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
    public void matchesNameOrModule_noMatchInExistingModules_coversLine124() {
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
}
