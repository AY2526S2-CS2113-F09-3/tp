package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.ArrayList;

public class AddCommandTest {
    private static final String TEST_FILE_PATH = "test_equipment.txt";

    @Test
    public void execute_validEquipment_addsToList() throws EquipmentMasterException {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);

        // Test basic add (no modules, no semester/lifespan)
        AddCommand command = new AddCommand("STM32", 5);

        // Act
        command.execute(equipments, ui, storage);

        // Assert
        assertEquals(1, equipments.getSize());

        Equipment added = equipments.getEquipment(0);
        assertEquals("STM32", added.getName());
        assertEquals(5, added.getQuantity());
        assertEquals(null, added.getPurchaseSem());
        assertEquals(0.0, added.getLifespanYears(), 0.0001);
        assertTrue(added.getModuleCodes().isEmpty());
    }

    @Test
    public void execute_validEquipmentWithSemesterAndLifespan_addsToList() throws EquipmentMasterException {

        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);

        // 1. Create dummy data for your new fields
        AcademicSemester testSem = new AcademicSemester("AY2023/24 Sem1");
        double testLifespan = 5.5;

        // 2. Pass all 4 arguments into the AddCommand constructor
        AddCommand command = new AddCommand("STM32", 5, testSem, testLifespan);

        command.execute(equipments, ui, storage);

        // Assert
        assertEquals(1, equipments.getSize());

        Equipment added = equipments.getEquipment(0);
        assertEquals("STM32", added.getName());
        assertEquals(5, added.getQuantity());
        assertEquals(testSem, added.getPurchaseSem());
        assertEquals(testLifespan, added.getLifespanYears(), 0.0001);
        assertTrue(added.getModuleCodes().isEmpty());
    }

    @Test
    public void execute_validEquipmentWithModules_addsToList() {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);

        ArrayList<String> modules = new ArrayList<>();
        modules.add("EE2026");
        modules.add("CG2028");

        AddCommand command = new AddCommand("FPGA", 40, modules);

        // Act
        command.execute(equipments, ui, storage);

        // Assert
        assertEquals(1, equipments.getSize());

        Equipment added = equipments.getEquipment(0);
        assertEquals("FPGA", added.getName());
        assertEquals(40, added.getQuantity());
        assertEquals(2, added.getModuleCodes().size());
        assertTrue(added.getModuleCodes().contains("EE2026"));
        assertTrue(added.getModuleCodes().contains("CG2028"));
        assertEquals(null, added.getPurchaseSem());
        assertEquals(0.0, added.getLifespanYears(), 0.0001);
    }

    @Test
    public void execute_validEquipmentWithAllFields_addsToList() throws EquipmentMasterException {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);

        AcademicSemester testSem = new AcademicSemester("AY2023/24 Sem1");
        double testLifespan = 5.5;
        ArrayList<String> modules = new ArrayList<>();
        modules.add("EE2026");
        modules.add("CG2028");

        AddCommand command = new AddCommand("FPGA", 40, testSem, testLifespan, modules);

        // Act
        command.execute(equipments, ui, storage);

        // Assert
        assertEquals(1, equipments.getSize());

        Equipment added = equipments.getEquipment(0);
        assertEquals("FPGA", added.getName());
        assertEquals(40, added.getQuantity());
        assertEquals(testSem, added.getPurchaseSem());
        assertEquals(testLifespan, added.getLifespanYears(), 0.0001);
        assertEquals(2, added.getModuleCodes().size());
        assertTrue(added.getModuleCodes().contains("EE2026"));
        assertTrue(added.getModuleCodes().contains("CG2028"));
    }
}

