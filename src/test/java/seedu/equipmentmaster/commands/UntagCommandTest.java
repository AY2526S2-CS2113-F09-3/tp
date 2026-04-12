package seedu.equipmentmaster.commands;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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

class UntagCommandTest {

    private Context context;
    private ModuleList moduleList;
    private EquipmentList equipmentList;

    @BeforeEach
    void setUp() throws EquipmentMasterException {
        // 1. Initialize empty lists
        equipmentList = new EquipmentList();
        moduleList = new ModuleList();

        // 2. Initialize dummies
        Ui dummyUi = new Ui();
        Storage dummyStorage = new Storage(
                "test_equipment.txt",
                dummyUi,
                "test_settings.txt",
                "test_modules.txt"
        );
        AcademicSemester dummySemester = new AcademicSemester("AY2024/25 Sem1");

        // 3. Build the context perfectly matching your constructor
        context = new Context(equipmentList, moduleList, dummyUi, dummyStorage, dummySemester);
    }

    // ==========================================
    // PARSE METHOD TESTS
    // ==========================================

    @Test
    void parse_validInput_returnsUntagCommand() {
        assertDoesNotThrow(() -> {
            UntagCommand command = UntagCommand.parse("untag m/CG2111 n/stm32");
            assertNotNull(command);
        });
    }

    @Test
    void parse_missingPrefixes_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            UntagCommand.parse("untag m/CG2111 stm32");
        });
        assertTrue(exception.getMessage().contains("Expected: untag m/MOD_NAME n/EQ_NAME"));
    }

    @Test
    void parse_anyOrder_returnsUntagCommand() {
        // Test that the new parser can handle flags in reverse order!
        assertDoesNotThrow(() -> {
            UntagCommand command = UntagCommand.parse("untag n/stm32 m/CG2111");
            assertNotNull(command);
        });
    }

    // ==========================================
    // EXECUTE METHOD TESTS
    // ==========================================

    @Test
    void execute_equipmentMissing_throwsException() {
        // Even if the module is also missing ("GhostMod"), the equipment check fails first
        UntagCommand command = new UntagCommand("GhostMod", "GhostEq");

        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(context);
        });

        // The system now throws the standard missing equipment message
        assertTrue(exception.getMessage().contains("Aborted: Equipment 'GhostEq' does not exist."));
    }

    @Test
    void execute_validData_successfullyUntags() throws EquipmentMasterException {
        // 1. Setup Context with a valid Module and valid Equipment
        Module testModule = new Module("CG2111", 50);
        // Pre-tag the module so we have something to untag!
        testModule.addEquipmentRequirement("stm32", 0.2);
        moduleList.addModule(testModule);

        Equipment testEquipment = new Equipment("stm32", 50, 50, 0, null, 5.0, new ArrayList<>(), 5, 0.0);
        equipmentList.addEquipment(testEquipment);

        // 2. Verify it is actually tagged before we run the command
        assertTrue(testModule.getEquipmentRequirements().containsKey("stm32"));

        // 3. Create and execute the Untag command
        UntagCommand command = new UntagCommand("CG2111", "stm32");
        assertDoesNotThrow(() -> {
            command.execute(context);
        });

        // 4. Verify the requirement was completely removed!
        assertFalse(testModule.getEquipmentRequirements().containsKey("stm32"));
    }

    @Test
    void execute_untagNonExistentRequirement_throwsException() {
        // 1. Setup Context, but DO NOT pre-tag the module
        Module testModule = null;
        try {
            testModule = new Module("CG2111", 50);
        } catch (EquipmentMasterException e) {
            fail("Module creation failed");
        }
        moduleList.addModule(testModule);

        Equipment testEquipment = new Equipment("stm32", 50, 50, 0, null, 5.0, new ArrayList<>(), 5, 0.0);
        equipmentList.addEquipment(testEquipment);

        // 2. Execute command
        UntagCommand command = new UntagCommand("CG2111", "stm32");

        // 3. Verify it gracefully aborts when trying to untag something that isn't there
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(context);
        });

        assertTrue(exception.getMessage().contains("does not currently have stm32 as a requirement"));
    }
}
