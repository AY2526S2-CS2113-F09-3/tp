package seedu.equipmentmaster.commands;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.ArrayList;

class TagCommandTest {

    private Context context;
    private ModuleList moduleList;
    private EquipmentList equipmentList;

    /**
     * This method runs BEFORE every single @Test.
     * It ensures each test starts with a completely fresh, empty system,
     * preventing tests from accidentally messing with each other's data.
     */
    @BeforeEach
    void setUp() throws EquipmentMasterException {
        // 1. Initialize empty lists
        equipmentList = new EquipmentList();
        moduleList = new ModuleList();

        // 2. Initialize dummy UI
        Ui dummyUi = new Ui();

        // 3. Initialize dummy Storage using "test" file paths
        Storage dummyStorage = new Storage(
                "test_equipment.txt",
                dummyUi,
                "test_settings.txt",
                "test_modules.txt"
        );

        // 4. Initialize a dummy AcademicSemester
        // (Assuming its constructor takes a string like your Storage class does!)
        AcademicSemester dummySemester = new AcademicSemester("AY2024/25 Sem1");

        // 5. Build the context with our dummies matching your EXACT constructor!
        context = new Context(equipmentList, moduleList, dummyUi, dummyStorage, dummySemester);
    }

    // ==========================================
    // PARSE METHOD TESTS
    // ==========================================

    @Test
    void parse_validInput_returnsTagCommand() {
        assertDoesNotThrow(() -> {
            TagCommand command = TagCommand.parse("tag m/CG2111 n/stm32 req/0.2");
            assertNotNull(command);
        });
    }

    @Test
    void parse_missingPrefixes_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            TagCommand.parse("tag m/CG2111 n/stm32 0.2");
        });
        assertTrue(exception.getMessage().contains("Expected: tag m/MOD_NAME n/EQ_NAME req/FRACTION"));
    }

    @Test
    void parse_invalidDecimals_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            TagCommand.parse("tag m/CG2111 n/stm32 req/abc");
        });
        assertTrue(exception.getMessage().contains("valid decimal number"));
    }

    // ==========================================
    // EXECUTE METHOD TESTS
    // ==========================================

    @Test
    void execute_bothModuleAndEquipmentMissing_throwsException() {
        // Since setUp() just ran, our context is completely empty!
        TagCommand command = new TagCommand("GhostMod", "GhostEq", 0.5);

        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(context);
        });

        assertTrue(exception.getMessage().contains
                ("Neither the module 'GhostMod' nor the equipment 'GhostEq' exists."));
    }

    @Test
    void execute_moduleMissing_throwsException() {
        // Add an equipment to the dummy context, but NO module
        equipmentList.addEquipment(new Equipment("stm32", 50, 50, 0, null, 5.0, new ArrayList<>(), 5, 0.0));

        TagCommand command = new TagCommand("GhostMod", "stm32", 0.5);

        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(context);
        });

        assertTrue(exception.getMessage().contains("Module 'GhostMod' does not exist."));
    }

    @Test
    void execute_validData_successfullyAddsTag() throws EquipmentMasterException {
        // 1. Add valid Module and Equipment to our dummy context
        Module testModule = new Module("CG2111", 50);
        moduleList.addModule(testModule);

        Equipment testEquipment = new Equipment("stm32", 50, 50, 0, null, 5.0, new ArrayList<>(), 5, 0.0);
        equipmentList.addEquipment(testEquipment);

        // 2. Create and execute the command
        TagCommand command = new TagCommand("CG2111", "stm32", 0.2);

        assertDoesNotThrow(() -> {
            command.execute(context);
        });

        // 3. Verify the state changed correctly inside the Module's HashMap!
        assertTrue(testModule.getEquipmentRequirements().containsKey("stm32"));
        assertEquals(0.2, testModule.getEquipmentRequirements().get("stm32"));
    }
}
