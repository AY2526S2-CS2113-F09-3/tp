package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class AddCommandTest {
    private static final String TEST_FILE_PATH = "test_equipment.txt";
    private static final String TEST_SETTING_FILE_PATH = "test_setting.txt";
    private static final String TEST_MODULE_FILE_PATH = "test_module.txt";

    @Test
    public void execute_validEquipment_addsToList() throws EquipmentMasterException {
        // Arrange
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);
        ModuleList moduleList = new ModuleList();

        // Test basic add (no modules, no semester/lifespan)
        AddCommand command = new AddCommand("STM32", 5);

        // Act
        command.execute(equipments, moduleList, ui, storage);

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
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);
        ModuleList moduleList = new ModuleList();

        // 1. Create dummy data for your new fields
        AcademicSemester testSem = new AcademicSemester("AY2023/24 Sem1");
        double testLifespan = 5.5;

        // 2. Pass all 4 arguments into the AddCommand constructor
        AddCommand command = new AddCommand("STM32", 5, testSem, testLifespan, 0,
                new ArrayList<>());

        command.execute(equipments, moduleList, ui, storage);

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
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);
        ModuleList moduleList = new ModuleList();

        ArrayList<String> modules = new ArrayList<>();
        modules.add("EE2026");
        modules.add("CG2028");

        AddCommand command = new AddCommand("FPGA", 40, modules);

        // Act
        command.execute(equipments, moduleList, ui, storage);

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
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);
        ModuleList moduleList = new ModuleList();

        AcademicSemester testSem = new AcademicSemester("AY2023/24 Sem1");
        double testLifespan = 5.5;
        ArrayList<String> modules = new ArrayList<>();
        modules.add("EE2026");
        modules.add("CG2028");

        AddCommand command = new AddCommand("FPGA", 40, testSem, testLifespan, 0, modules);

        // Act
        command.execute(equipments, moduleList, ui, storage);

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

    @Test
    public void execute_addAtMinThreshold_showsLowStockAlert() {
        EquipmentList equipments = new EquipmentList();
        ModuleList moduleList = new ModuleList();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);

        AddCommand command = new AddCommand("Resistor", 5, null, 0.0, 5, new ArrayList<>());

        command.execute(equipments, moduleList, ui, storage);

        String output = outputStream.toString();
        assertTrue(output.contains("!!! LOW STOCK ALERT: Resistor"));
    }

    @Test
    public void execute_addAboveMinThreshold_noLowStockAlert() {
        EquipmentList equipments = new EquipmentList();
        ModuleList moduleList = new ModuleList();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);

        AddCommand command = new AddCommand("Resistor", 10, null, 0.0, 5, new ArrayList<>());

        command.execute(equipments, moduleList, ui, storage);

        String output = outputStream.toString();
        assertTrue(!output.contains("!!! LOW STOCK ALERT:"));
    }

    @Test
    public void addEquipment_duplicateEquipmentWithNewModules_mergesQuantitiesAndModules()
            throws EquipmentMasterException {
        // Setup
        EquipmentList equipmentList = new EquipmentList();
        AcademicSemester sem = new AcademicSemester("AY2024/25 Sem1");

        // 1. Create and add initial equipment (10 units, module: CG2211)
        ArrayList<String> initialModules = new ArrayList<>(Arrays.asList("CG2211"));
        Equipment firstEquipment = new Equipment("STM32", 10, 10, 0, sem, 5.0, initialModules, 0);
        equipmentList.addEquipment(firstEquipment);

        // 2. Create and add the same equipment (5 units, modules: CG2211 [duplicate], EE2211 [new])
        ArrayList<String> newModules = new ArrayList<>(Arrays.asList("CG2211", "EE2211"));
        Equipment secondEquipment = new Equipment("STM32", 5, 5, 0, sem, 5.0, newModules, 0);
        equipmentList.addEquipment(secondEquipment);

        // 3. Verify the list size hasn't grown (it should have merged)
        assertEquals(1, equipmentList.getSize(), "Equipment list size should remain 1 after merging.");

        // 4. Verify the quantities merged correctly
        Equipment mergedEquipment = equipmentList.getEquipment(0);
        assertEquals(15, mergedEquipment.getQuantity(), "Total quantity should be 10 + 5 = 15.");
        assertEquals(15, mergedEquipment.getAvailable(), "Available quantity should be 10 + 5 = 15.");

        // 5. Verify the modules merged correctly without duplicates
        ArrayList<String> mergedModules = mergedEquipment.getModuleCodes();
        assertEquals(2, mergedModules.size(), "Module list should contain exactly 2 distinct modules.");
        assertTrue(mergedModules.contains("CG2211"), "Merged modules should contain CG2211.");
        assertTrue(mergedModules.contains("EE2211"), "Merged modules should contain EE2211.");
    }
}

