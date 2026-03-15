package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.nio.file.Path;

public class SetStatusCommandTest {

    @TempDir
    Path tempDir;

    private Storage storage;
    private Ui ui;
    private EquipmentList equipments;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
        // Create a unique file in the temp directory for each test
        storage = new Storage(tempDir.resolve("test.txt").toString(), ui);
        equipments = new EquipmentList();
    }

    @Test
    public void executeByName_loanPositive_updates() {
        // Arrange
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 40, 0));
        SetStatusCommand command = new SetStatusCommand("BasyS3 FPGA", 5, "loaned");

        // Act
        command.execute(equipments, ui, storage);

        // Assert
        Equipment eq = equipments.getEquipment(0);
        assertEquals(35, eq.getAvailable());
        assertEquals(5, eq.getLoaned());
    }

    @Test
    public void executeByName_loanNegative_noChange() {
        // Arrange
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 40, 0));
        SetStatusCommand command = new SetStatusCommand("BasyS3 FPGA", -5, "loaned");

        // Act
        command.execute(equipments, ui, storage);

        // Assert
        Equipment eq = equipments.getEquipment(0);
        assertEquals(40, eq.getAvailable());
        assertEquals(0, eq.getLoaned());
    }

    @Test
    public void executeByIndex_returnPositive_updates() {
        // Arrange
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 30, 10));
        SetStatusCommand command = new SetStatusCommand(1, 3, "available");

        // Act
        command.execute(equipments, ui, storage);

        // Assert
        Equipment eq = equipments.getEquipment(0);
        assertEquals(33, eq.getAvailable());
        assertEquals(7, eq.getLoaned());
    }

    @Test
    public void executeByIndex_returnNegative_noChange() {
        // Arrange
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 30, 10));
        SetStatusCommand command = new SetStatusCommand(1, -3, "available");

        // Act
        command.execute(equipments, ui, storage);

        // Assert
        Equipment eq = equipments.getEquipment(0);
        assertEquals(30, eq.getAvailable());
        assertEquals(10, eq.getLoaned());
    }
}
