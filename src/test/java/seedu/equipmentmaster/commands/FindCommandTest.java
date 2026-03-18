package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
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
}
