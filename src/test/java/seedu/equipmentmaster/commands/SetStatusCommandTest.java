package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import static seedu.equipmentmaster.common.Messages.MESSAGE_NAME_CONTAINS_RESERVED_CHARS;


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
        storage = new Storage(tempDir.resolve("test.txt").toString(),
                ui, tempDir.resolve("test_setting.txt").toString(), tempDir.resolve("test_module.txt").toString());
        equipments = new EquipmentList();
    }

    @Test
    public void executeByName_loanPositive_updates() throws EquipmentMasterException {
        ModuleList moduleList = new ModuleList();
        // Arrange
        AcademicSemester testSem = new AcademicSemester("AY2025/26 Sem2");
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 40, 0, testSem, 5.0, 0));
        SetStatusCommand command = new SetStatusCommand("BasyS3 FPGA", 5, "loaned");

        // Act
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        // Assert
        Equipment eq = equipments.getEquipment(0);
        assertEquals(35, eq.getAvailable());
        assertEquals(5, eq.getLoaned());
    }

    @Test
    public void executeByName_loanNegative_noChange() {
        ModuleList moduleList = new ModuleList();
        // Arrange
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 40, 0));
        SetStatusCommand command = new SetStatusCommand("BasyS3 FPGA", -5, "loaned");

        // Act
        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            // Assert
            Equipment eq = equipments.getEquipment(0);
            assertEquals(40, eq.getAvailable());
            assertEquals(0, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void executeByIndex_returnPositive_updates() throws EquipmentMasterException {
        ModuleList moduleList = new ModuleList();
        // Arrange
        AcademicSemester testSem = new AcademicSemester("AY2025/26 Sem2");
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 30, 10, testSem, 5.0, 0));
        SetStatusCommand command = new SetStatusCommand(1, 3, "available");

        // Act
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        // Assert
        Equipment eq = equipments.getEquipment(0);
        assertEquals(33, eq.getAvailable());
        assertEquals(7, eq.getLoaned());
    }

    @Test
    public void executeByIndex_returnNegative_noChange() {
        ModuleList moduleList = new ModuleList();
        // Arrange
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 30, 10));
        SetStatusCommand command = new SetStatusCommand(1, -3, "available");

        // Act
        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            // Assert
            Equipment eq = equipments.getEquipment(0);
            assertEquals(30, eq.getAvailable());
            assertEquals(10, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void executeByName_loanExceedsAvailable_noChange() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 10, 30));
        SetStatusCommand command = new SetStatusCommand("BasyS3 FPGA", 20, "loaned");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(10, eq.getAvailable());
            assertEquals(30, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void executeByName_returnExceedsLoaned_noChange() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 35, 5));
        SetStatusCommand command = new SetStatusCommand("BasyS3 FPGA", 10, "available");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(35, eq.getAvailable());
            assertEquals(5, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void executeByName_equipmentNotFound_noChange() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 40, 0));
        SetStatusCommand command = new SetStatusCommand("NonExistent", 5, "loaned");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(40, eq.getAvailable());
            assertEquals(0, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void executeByIndex_outOfBounds_noChange() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 40, 0));
        SetStatusCommand command = new SetStatusCommand(99, 5, "loaned");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(40, eq.getAvailable());
            assertEquals(0, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void executeByIndex_zeroQuantity_noChange() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 40, 0));
        SetStatusCommand command = new SetStatusCommand(1, 0, "loaned");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(40, eq.getAvailable());
            assertEquals(0, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void executeByIndex_loanExceedsAvailable_noChange() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 10, 30));
        SetStatusCommand command = new SetStatusCommand(1, 20, "loaned");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(10, eq.getAvailable());
            assertEquals(30, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void executeByIndex_returnExceedsLoaned_noChange() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("BasyS3 FPGA", 40, 35, 5));
        SetStatusCommand command = new SetStatusCommand(1, 10, "available");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(35, eq.getAvailable());
            assertEquals(5, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void parseByName_validLoaned_success() throws EquipmentMasterException {
        Command command = SetStatusCommand.parse("setstatus n/BasyS3 FPGA q/5 s/loaned");
        assertEquals(SetStatusCommand.class, command.getClass());
    }

    @Test
    public void parseByIndex_validAvailable_success() throws EquipmentMasterException {
        Command command = SetStatusCommand.parse("setstatus 1 q/3 s/available");
        assertEquals(SetStatusCommand.class, command.getClass());
    }

    @Test
    public void parse_missingQFlag_throwsException() {
        try {
            SetStatusCommand.parse("setstatus n/BasyS3 FPGA 5 s/loaned");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            // expected
        }
    }

    @Test
    public void parse_invalidStatus_throwsException() {
        try {
            SetStatusCommand.parse("setstatus n/BasyS3 FPGA q/5 s/broken");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            // expected
        }
    }

    @Test
    public void parse_nonNumericQuantity_throwsException() {
        try {
            SetStatusCommand.parse("setstatus n/BasyS3 FPGA q/abc s/loaned");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            // expected
        }
    }

    @Test
    public void parse_nonNumericIndex_throwsException() {
        try {
            SetStatusCommand.parse("setstatus abc q/5 s/loaned");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            // expected
        }
    }

    @Test
    public void parse_zeroQuantity_throwsException() {
        try {
            SetStatusCommand.parse("setstatus n/BasyS3 FPGA q/0 s/loaned");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            // expected
        }
    }

    @Test
    public void parse_negativeIndex_throwsException() {
        try {
            SetStatusCommand.parse("setstatus -1 q/5 s/loaned");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            // expected
        }
    }

    @Test
    public void executeByName_loanWithMixedCaseStatus_updates() throws EquipmentMasterException {
        ModuleList moduleList = new ModuleList();
        // Arrange
        AcademicSemester testSem = new AcademicSemester("AY2025/26 Sem2");
        equipments.addEquipment(new Equipment("Oscilloscope", 20, 20, 0, testSem, 5.0, 0));
        SetStatusCommand command = new SetStatusCommand("Oscilloscope", 3, "LOANED"); // Uppercase

        // Act
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        // Assert
        Equipment eq = equipments.getEquipment(0);
        assertEquals(17, eq.getAvailable());
        assertEquals(3, eq.getLoaned());
    }

    @Test
    public void executeByName_returnWithMixedCaseStatus_updates() throws EquipmentMasterException {
        ModuleList moduleList = new ModuleList();
        // Arrange
        equipments.addEquipment(new Equipment("Multimeter", 15, 10, 5));
        SetStatusCommand command = new SetStatusCommand("Multimeter", 2, "AVAILABLE"); // Uppercase

        // Act
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        // Assert
        Equipment eq = equipments.getEquipment(0);
        assertEquals(12, eq.getAvailable());
        assertEquals(3, eq.getLoaned());
    }

    @Test
    public void executeByName_invalidStatus_showsNoChange() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("PowerSupply", 10, 10, 0));
        SetStatusCommand command = new SetStatusCommand("PowerSupply", 2, "damaged");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(10, eq.getAvailable());
            assertEquals(0, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void executeByIndex_invalidStatus_showsNoChange() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("FunctionGen", 8, 8, 0));
        SetStatusCommand command = new SetStatusCommand(1, 2, "reserved");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(8, eq.getAvailable());
            assertEquals(0, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void parseByName_extraSpacesInCommand_success() throws EquipmentMasterException {
        Command command = SetStatusCommand.parse("setstatus   n/STM32   q/10   s/loaned  ");
        assertEquals(SetStatusCommand.class, command.getClass());
    }

    @Test
    public void parseByIndex_extraSpacesInCommand_success() throws EquipmentMasterException {
        Command command = SetStatusCommand.parse("setstatus   2   q/5   s/available  ");
        assertEquals(SetStatusCommand.class, command.getClass());
    }

    @Test
    public void parseByName_emptyName_throwsException() {
        try {
            SetStatusCommand.parse("setstatus n/ q/5 s/loaned");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            assertEquals("Equipment name cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void parseByName_missingStatus_throwsException() {
        try {
            SetStatusCommand.parse("setstatus n/Arduino q/5");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            // expected - format invalid
        }
    }

    @Test
    public void parseByName_missingQuantity_throwsException() {
        try {
            SetStatusCommand.parse("setstatus n/Arduino s/loaned");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            // expected - format invalid
        }
    }

    @Test
    public void parseByIndex_missingQuantity_throwsException() {
        try {
            SetStatusCommand.parse("setstatus 1 s/loaned");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            // expected - format invalid
        }
    }

    @Test
    public void parseByIndex_missingStatus_throwsException() {
        try {
            SetStatusCommand.parse("setstatus 1 q/5");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            // expected - format invalid
        }
    }

    @Test
    public void parseByIndex_missingIndex_throwsException() {
        try {
            SetStatusCommand.parse("setstatus q/5 s/loaned");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            assertEquals("Please enter a valid whole number for index", e.getMessage());
        }
    }

    @Test
    public void executeByName_loanWithZeroQuantity_noChange() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("Camera", 5, 5, 0));
        SetStatusCommand command = new SetStatusCommand("Camera", 0, "loaned");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(5, eq.getAvailable());
            assertEquals(0, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
    }

    @Test
    public void executeByName_loanExactAvailable_works() throws EquipmentMasterException {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("Projector", 3, 3, 0));
        SetStatusCommand command = new SetStatusCommand("Projector", 3, "loaned");

        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        Equipment eq = equipments.getEquipment(0);
        assertEquals(0, eq.getAvailable());
        assertEquals(3, eq.getLoaned());
    }

    @Test
    public void executeByName_returnExactLoaned_works() throws EquipmentMasterException {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("Headphones", 10, 5, 5));
        SetStatusCommand command = new SetStatusCommand("Headphones", 5, "available");

        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        Equipment eq = equipments.getEquipment(0);
        assertEquals(10, eq.getAvailable());
        assertEquals(0, eq.getLoaned());
    }

    @Test
    public void executeByName_caseInsensitiveNameMatch_works() throws EquipmentMasterException {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("RaspberryPi", 25, 25, 0));
        SetStatusCommand command = new SetStatusCommand("raspberrypi", 5, "loaned"); // Lowercase

        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        Equipment eq = equipments.getEquipment(0);
        assertEquals(20, eq.getAvailable());
        assertEquals(5, eq.getLoaned());
    }

    @Test
    public void executeByIndex_loanMultipleTimes_accumulates() throws EquipmentMasterException {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("SolderingIron", 50, 50, 0));

        SetStatusCommand command1 = new SetStatusCommand(1, 10, "loaned");
        SetStatusCommand command2 = new SetStatusCommand(1, 5, "loaned");

        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);

        command1.execute(context);
        command2.execute(context);

        Equipment eq = equipments.getEquipment(0);
        assertEquals(35, eq.getAvailable());
        assertEquals(15, eq.getLoaned());
    }

    @Test
    public void executeByIndex_returnPartialLoaned_works() throws EquipmentMasterException {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("LogicAnalyzer", 20, 10, 10));
        SetStatusCommand command = new SetStatusCommand(1, 4, "available");

        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        Equipment eq = equipments.getEquipment(0);
        assertEquals(14, eq.getAvailable());
        assertEquals(6, eq.getLoaned());
    }

    @Test
    public void parseByName_withSpecialCharactersInName_throwsException() {
        try {
            SetStatusCommand.parse("setstatus n/Test|Name q/5 s/loaned");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            assertEquals(MESSAGE_NAME_CONTAINS_RESERVED_CHARS, e.getMessage());
        }
    }

    @Test
    public void parse_emptyCommand_throwsException() {
        try {
            SetStatusCommand.parse("");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            assertEquals("Empty command.", e.getMessage());
        }
    }

    @Test
    public void parse_whitespaceOnly_throwsException() {
        try {
            SetStatusCommand.parse("   ");
            fail("Expected EquipmentMasterException");
        } catch (EquipmentMasterException e) {
            assertEquals("Empty command.", e.getMessage());
        }
    }

    @Test
    public void executeByName_loanThenReturn_restoresOriginal() throws EquipmentMasterException {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("TestGear", 100, 100, 0));

        SetStatusCommand loanCmd = new SetStatusCommand("TestGear", 30, "loaned");
        SetStatusCommand returnCmd = new SetStatusCommand("TestGear", 30, "available");

        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);

        loanCmd.execute(context);
        returnCmd.execute(context);

        Equipment eq = equipments.getEquipment(0);
        assertEquals(100, eq.getAvailable());
        assertEquals(0, eq.getLoaned());
    }

    @Test
    public void executeByName_indexAndNameBothNull_showsErrorMessage() {
        ModuleList moduleList = new ModuleList();
    }

    @Test
    public void executeByName_loanWithAvailableZero_showsErrorMessage() {
        ModuleList moduleList = new ModuleList();
        equipments.addEquipment(new Equipment("EmptyStock", 10, 0, 10));
        SetStatusCommand command = new SetStatusCommand("EmptyStock", 1, "loaned");

        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            Equipment eq = equipments.getEquipment(0);
            assertEquals(0, eq.getAvailable());
            assertEquals(10, eq.getLoaned());
        } catch (EquipmentMasterException e) {
            fail("Test setup failed: " + e.getMessage());
        }
    }
}
