package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.io.TempDir;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Tests the functionality of the DeleteCommand.
 */
public class DeleteCommandTest {
    private static final String TEST_FILE_PATH = "test_equipment.txt";

    @TempDir
    Path tempDir;

    private EquipmentList equipments;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        equipments = new EquipmentList();
        ui = new Ui();
        // Use a temporary file so we don't overwrite real data during tests
        storage = new Storage(tempDir.resolve("test_equipment.txt").toString(), ui);
    }

    @Test
    public void execute_deleteAvailableQuantity_success() throws EquipmentMasterException {
        // Setup: Total 10, Available 10, Loaned 0
        AcademicSemester testSem = new AcademicSemester("AY2024/25 Sem1");
        Equipment eq = new Equipment("Oscilloscope", 10, 10, 0, testSem, 5.0);
        equipments.addEquipment(eq);

        // Action: Delete 3 available
        DeleteCommand command = new DeleteCommand(1, 3, "available");
        command.execute(equipments, ui, storage);

        // Verify: Total becomes 7, Available becomes 7
        assertEquals(7, eq.getQuantity());
        assertEquals(7, eq.getAvailable());
        assertEquals(1, equipments.getSize()); // Item still exists
    }

    @Test
    public void execute_deleteLoanedQuantity_success() throws EquipmentMasterException {
        // Setup: Total 10, Available 6, Loaned 4
        AcademicSemester testSem = new AcademicSemester("AY2024/25 Sem1");
        Equipment eq = new Equipment("Multimeter", 10, 6, 4, testSem, 3.0);
        equipments.addEquipment(eq);

        // Action: Delete 2 loaned
        DeleteCommand command = new DeleteCommand("Multimeter", 2, "loaned");
        command.execute(equipments, ui, storage);

        // Verify: Total becomes 8, Loaned becomes 2, Available remains 6
        assertEquals(8, eq.getQuantity());
        assertEquals(2, eq.getLoaned());
        assertEquals(6, eq.getAvailable());
    }

    @Test
    public void execute_deleteToZero_autoRemovesEquipment() throws EquipmentMasterException {
        // Setup: Total 5, Available 5
        Equipment eq = new Equipment("Soldering Iron", 5);
        eq.setAvailable(5);
        eq.setLoaned(0);
        equipments.addEquipment(eq);

        // Action: Delete all 5 available
        DeleteCommand command = new DeleteCommand(1, 5, "available");
        command.execute(equipments, ui, storage);

        // Verify: The item should be completely removed from the list
        assertEquals(0, equipments.getSize());
        assertTrue(equipments.isEmpty());
    }

    @Test
    public void execute_deleteMoreThanAvailable_throwsException() {
        // Setup: Total 5, Available 2, Loaned 3
        Equipment eq = new Equipment("Breadboard", 5);
        eq.setAvailable(2);
        eq.setLoaned(3);
        equipments.addEquipment(eq);

        // Action & Verify: Try to delete 3 available (only 2 exist)
        DeleteCommand command = new DeleteCommand(1, 3, "available");
        assertThrows(EquipmentMasterException.class, () -> command.execute(equipments, ui, storage));
    }

    @Test
    public void execute_deleteMoreThanLoaned_throwsException() {
        // Setup: Total 5, Available 4, Loaned 1
        Equipment eq = new Equipment("Raspberry Pi", 5);
        eq.setAvailable(4);
        eq.setLoaned(1);
        equipments.addEquipment(eq);

        // Action & Verify: Try to delete 2 loaned (only 1 exists)
        DeleteCommand command = new DeleteCommand("Raspberry Pi", 2, "loaned");
        assertThrows(EquipmentMasterException.class, () -> command.execute(equipments, ui, storage));
    }

    @Test
    public void parse_validFormat_returnsDeleteCommand() throws EquipmentMasterException {
        // Verify the parser correctly handles the new format with 's/STATUS'
        Command cmd1 = DeleteCommand.parse("delete 1 q/5 s/available");
        assertTrue(cmd1 instanceof DeleteCommand);

        Command cmd2 = DeleteCommand.parse("delete n/STM32 q/2 s/loaned");
        assertTrue(cmd2 instanceof DeleteCommand);
    }


    @Test
    public void execute_validIndex_reducesQuantity() throws EquipmentMasterException {
        // Arrange
        // (equipments, ui, and storage are safely set up in @BeforeEach)
        AcademicSemester testSem = new AcademicSemester("AY2025/26 Sem2"); // Adjust to your expected format
        equipments.addEquipment(new Equipment("Basys3 FPGA", 10, 10, 0, testSem, 5.0));

        // Act: Delete 4 units from index 1
        DeleteCommand command = new DeleteCommand(1, 4, "available");
        command.execute(equipments, ui, storage);

        // Assert
        assertEquals(6, equipments.getEquipment(0).getQuantity());
        assertEquals(6, equipments.getEquipment(0).getAvailable());
    }

    @Test
    public void execute_validName_reducesQuantity() throws EquipmentMasterException {
        // Arrange
        AcademicSemester testSem = new AcademicSemester("AY2025/26 Sem2");
        equipments.addEquipment(new Equipment("STM32 Board", 20, 20, 0, testSem, 3.0));

        // Act: Delete 5 units by name
        DeleteCommand command = new DeleteCommand("STM32 Board", 5, "available");
        command.execute(equipments, ui, storage);

        // Assert
        assertEquals(15, equipments.getEquipment(0).getQuantity());
        assertEquals(15, equipments.getEquipment(0).getAvailable());
    }

    @Test
    public void execute_invalidIndex_throwsException() {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);
        equipments.addEquipment(new Equipment("Basys3 FPGA", 10));

        // Act & Assert: Check if it throws exception for out of bounds
        DeleteCommand command = new DeleteCommand(2, 1, "available");
        assertThrows(EquipmentMasterException.class, () -> {
            command.execute(equipments, ui, storage);
        });
    }
}
