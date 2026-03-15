package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.exception.EquipmentMasterException;

/**
 * Tests the functionality of the DeleteCommand.
 */
public class DeleteCommandTest {
    private static final String TEST_FILE_PATH = "test_equipment.txt";

    @Test
    public void execute_validIndex_reducesQuantity() throws EquipmentMasterException {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);
        equipments.addEquipment(new Equipment("Basys3 FPGA", 10));

        // Act: Delete 4 units from index 1
        DeleteCommand command = new DeleteCommand(1, 4);
        command.execute(equipments, ui, storage);

        // Assert
        assertEquals(6, equipments.getEquipment(0).getQuantity());
    }

    @Test
    public void execute_validName_reducesQuantity() throws EquipmentMasterException {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);
        equipments.addEquipment(new Equipment("STM32 Board", 20));

        // Act: Delete 5 units by name
        DeleteCommand command = new DeleteCommand("STM32 Board", 5);
        command.execute(equipments, ui, storage);

        // Assert
        assertEquals(15, equipments.getEquipment(0).getQuantity());
    }

    @Test
    public void execute_invalidIndex_throwsException() {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);
        equipments.addEquipment(new Equipment("Basys3 FPGA", 10));

        // Act & Assert: Check if it throws exception for out of bounds
        DeleteCommand command = new DeleteCommand(2, 1);
        assertThrows(EquipmentMasterException.class, () -> {
            command.execute(equipments, ui, storage);
        });
    }
}
