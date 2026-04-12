package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_SET_STATUS_FORMAT;
import static seedu.equipmentmaster.common.Messages.MESSAGE_NAME_CONTAINS_RESERVED_CHARS;

import java.nio.file.Path;

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

public class SetStatusCommandTest {

    @TempDir
    Path tempDir;

    private Storage storage;
    private Ui ui;
    private EquipmentList equipments;
    private ModuleList moduleList;
    private AcademicSemester currentSystemSemester;

    @BeforeEach
    public void setUp() throws EquipmentMasterException {
        ui = new Ui();
        storage = new Storage(tempDir.resolve("test.txt").toString(),
                ui, tempDir.resolve("test_setting.txt").toString(), tempDir.resolve("test_module.txt").toString());
        equipments = new EquipmentList();
        moduleList = new ModuleList();
        currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
    }

    private Context createContext() {
        return new Context(equipments, moduleList, ui, storage, currentSystemSemester);
    }

    private void addEquipmentWithSemester(String name, int quantity, int available, int loaned,
                                          AcademicSemester sem, double lifespan) throws EquipmentMasterException {
        Equipment eq = new Equipment(name, quantity, available, loaned, sem, lifespan, 0);
        equipments.addEquipment(eq);
    }

    // =====================================
    // SUCCESSFUL EXECUTION TESTS
    // =====================================

    @Test
    public void executeByName_loanPositive_updates() throws EquipmentMasterException {
        addEquipmentWithSemester("Basys3 FPGA", 40, 40, 0, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand("Basys3 FPGA", 5, "loaned");
        command.execute(createContext());

        Equipment eq = equipments.getEquipment(0);
        assertEquals(35, eq.getAvailable());
        assertEquals(5, eq.getLoaned());
    }

    @Test
    public void executeByIndex_returnPositive_updates() throws EquipmentMasterException {
        addEquipmentWithSemester("Basys3 FPGA", 40, 30, 10, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand(1, 3, "available");
        command.execute(createContext());

        Equipment eq = equipments.getEquipment(0);
        assertEquals(33, eq.getAvailable());
        assertEquals(7, eq.getLoaned());
    }

    @Test
    public void executeByName_loanWithZeroAvailable_throwsException() throws EquipmentMasterException {
        addEquipmentWithSemester("Camera", 5, 0, 5, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand("Camera", 1, "loaned");

        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(createContext());
        });
        assertEquals("Insufficient stock. Only 0 units available.", exception.getMessage());
    }

    @Test
    public void executeByName_loanExactAvailable_works() throws EquipmentMasterException {
        addEquipmentWithSemester("Projector", 3, 3, 0, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand("Projector", 3, "loaned");
        command.execute(createContext());

        Equipment eq = equipments.getEquipment(0);
        assertEquals(0, eq.getAvailable());
        assertEquals(3, eq.getLoaned());
    }

    @Test
    public void executeByName_returnExactLoaned_works() throws EquipmentMasterException {
        addEquipmentWithSemester("Headphones", 10, 5, 5, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand("Headphones", 5, "available");
        command.execute(createContext());

        Equipment eq = equipments.getEquipment(0);
        assertEquals(10, eq.getAvailable());
        assertEquals(0, eq.getLoaned());
    }

    @Test
    public void executeByName_caseInsensitiveNameMatch_works() throws EquipmentMasterException {
        addEquipmentWithSemester("RaspberryPi", 25, 25, 0, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand("raspberrypi", 5, "loaned");
        command.execute(createContext());

        Equipment eq = equipments.getEquipment(0);
        assertEquals(20, eq.getAvailable());
        assertEquals(5, eq.getLoaned());
    }

    @Test
    public void executeByIndex_loanMultipleTimes_accumulates() throws EquipmentMasterException {
        addEquipmentWithSemester("SolderingIron", 50, 50, 0, currentSystemSemester, 5.0);
        SetStatusCommand command1 = new SetStatusCommand(1, 10, "loaned");
        SetStatusCommand command2 = new SetStatusCommand(1, 5, "loaned");

        Context context = createContext();
        command1.execute(context);
        command2.execute(context);

        Equipment eq = equipments.getEquipment(0);
        assertEquals(35, eq.getAvailable());
        assertEquals(15, eq.getLoaned());
    }

    // =====================================
    // EXCEPTION TESTS FOR EXECUTE
    // =====================================

    @Test
    public void executeByIndex_loanExceedsAvailable_throwsException() throws EquipmentMasterException {
        addEquipmentWithSemester("Basys3 FPGA", 40, 10, 30, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand(1, 20, "loaned");

        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(createContext());
        });
        assertEquals("Insufficient stock. Only 10 units available.", exception.getMessage());
    }

    @Test
    public void executeByName_loanExceedsAvailable_throwsException() throws EquipmentMasterException {
        addEquipmentWithSemester("Basys3 FPGA", 40, 10, 30, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand("Basys3 FPGA", 20, "loaned");

        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(createContext());
        });
        assertEquals("Insufficient stock. Only 10 units available.", exception.getMessage());
    }

    @Test
    public void executeByIndex_returnExceedsLoaned_throwsException() throws EquipmentMasterException {
        addEquipmentWithSemester("Basys3 FPGA", 40, 35, 5, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand(1, 10, "available");

        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(createContext());
        });
        assertEquals("Return error. Only 5 units are currently on loan.", exception.getMessage());
    }

    @Test
    public void executeByName_equipmentNotFound_throwsException() throws EquipmentMasterException {
        addEquipmentWithSemester("Basys3 FPGA", 40, 40, 0, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand("NonExistent", 5, "loaned");

        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(createContext());
        });
        assertEquals("Equipment 'NonExistent' not found.", exception.getMessage());
    }

    @Test
    public void executeByIndex_outOfBounds_throwsException() throws EquipmentMasterException {
        addEquipmentWithSemester("Basys3 FPGA", 40, 40, 0, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand(99, 5, "loaned");

        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(createContext());
        });
        assertEquals("Invalid index. List size is 1", exception.getMessage());
    }

    @Test
    public void executeByName_invalidStatus_throwsException() throws EquipmentMasterException {
        addEquipmentWithSemester("PowerSupply", 10, 10, 0, currentSystemSemester, 5.0);
        SetStatusCommand command = new SetStatusCommand("PowerSupply", 2, "damaged");

        // "damaged" goes to else block (processReturn) because it's not "loaned"
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class, () -> {
            command.execute(createContext());
        });
        assertEquals("Return error. Only 0 units are currently on loan.", exception.getMessage());
    }

    // =====================================
    // PARSER TESTS
    // =====================================

    @Test
    public void parseByName_validLoaned_success() throws EquipmentMasterException {
        Command command = SetStatusCommand.parse("setstatus n/Basys3 FPGA q/5 s/loaned");
        assertEquals(SetStatusCommand.class, command.getClass());
    }

    @Test
    public void parseByIndex_validAvailable_success() throws EquipmentMasterException {
        Command command = SetStatusCommand.parse("setstatus 1 q/3 s/available");
        assertEquals(SetStatusCommand.class, command.getClass());
    }

    @Test
    public void parseByName_emptyName_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class,
                () -> SetStatusCommand.parse("setstatus n/ q/5 s/loaned"));
        assertEquals(MESSAGE_INVALID_SET_STATUS_FORMAT, exception.getMessage());
    }

    @Test
    public void parseByIndex_missingIndex_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class,
                () -> SetStatusCommand.parse("setstatus q/5 s/loaned"));
        assertEquals("Please provide a valid 1-based index or use n/NAME.", exception.getMessage());
    }

    @Test
    public void parse_emptyCommand_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class,
                () -> SetStatusCommand.parse(""));
        assertEquals(MESSAGE_INVALID_SET_STATUS_FORMAT, exception.getMessage());
    }

    @Test
    public void parse_whitespaceOnly_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class,
                () -> SetStatusCommand.parse("   "));
        assertEquals(MESSAGE_INVALID_SET_STATUS_FORMAT, exception.getMessage());
    }

    @Test
    public void parseByName_missingStatus_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class,
                () -> SetStatusCommand.parse("setstatus n/Arduino q/5"));
        assertEquals(MESSAGE_INVALID_SET_STATUS_FORMAT, exception.getMessage());
    }

    @Test
    public void parseByName_missingQuantity_throwsException() {
        assertThrows(EquipmentMasterException.class,
                () -> SetStatusCommand.parse("setstatus n/Arduino s/loaned"));
    }

    @Test
    public void parse_nonNumericQuantity_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class,
                () -> SetStatusCommand.parse("setstatus n/Basys3 FPGA q/abc s/loaned"));
        assertEquals("Quantity must be a positive whole number.", exception.getMessage());
    }

    @Test
    public void parse_negativeQuantity_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class,
                () -> SetStatusCommand.parse("setstatus n/Basys3 FPGA q/-5 s/loaned"));
        assertEquals("Quantity must be a positive whole number.", exception.getMessage());
    }

    @Test
    public void parse_invalidStatus_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class,
                () -> SetStatusCommand.parse("setstatus n/Basys3 FPGA q/5 s/broken"));
        assertEquals("Status must be either 'loaned' or 'available'.", exception.getMessage());
    }

    @Test
    public void parseByName_extraSpacesInCommand_success() throws EquipmentMasterException {
        Command command = SetStatusCommand.parse("setstatus   n/STM32   q/10   s/loaned  ");
        assertEquals(SetStatusCommand.class, command.getClass());
    }

    @Test
    public void parseByName_withSpecialCharactersInName_throwsException() {
        EquipmentMasterException exception = assertThrows(EquipmentMasterException.class,
                () -> SetStatusCommand.parse("setstatus n/Test|Name q/5 s/loaned"));
        assertEquals(MESSAGE_NAME_CONTAINS_RESERVED_CHARS, exception.getMessage());
    }
}
