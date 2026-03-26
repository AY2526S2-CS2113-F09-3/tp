package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.context.Context;
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

public class SetBufferCommandTest {
    private static final String TEST_FILE_PATH = "test_equipment.txt";
    private static final String TEST_SETTING_FILE_PATH = "test_setting.txt";
    private static final String TEST_MODULE_FILE_PATH = "test_module.txt";

    @Test
    public void execute_validBuffer_setsBuffer() throws EquipmentMasterException {
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);
        ModuleList moduleList = new ModuleList();

        AddCommand addCommand = new AddCommand("STM32", 10);
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        addCommand.execute(context);

        SetBufferCommand command = new SetBufferCommand("STM32", 15.0);
        command.execute(context);

        Equipment equipment = equipments.getEquipment(0);
        assertEquals(15.0, equipment.getBufferPercentage(), 0.0001);
    }

    @Test
    public void execute_equipmentNotFound_showsErrorMessage() {
        EquipmentList equipments = new EquipmentList();
        ModuleList moduleList = new ModuleList();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);

        SetBufferCommand command = new SetBufferCommand("NonExistent", 10.0);
        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            String output = outputStream.toString();
            assertTrue(output.contains("Equipment 'NonExistent' not found."));
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
        }
    }

    @Test
    public void parse_inputWithPercentSymbol_createsCommand() throws EquipmentMasterException {
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);
        ModuleList moduleList = new ModuleList();

        AddCommand addCommand = new AddCommand("STM32", 10);
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        addCommand.execute(context);

        SetBufferCommand command = SetBufferCommand.parse("setbuffer n/STM32 b/10%");
        command.execute(context);

        Equipment equipment = equipments.getEquipment(0);
        assertEquals(10.0, equipment.getBufferPercentage(), 0.0001);
    }

    @Test
    public void parseEquipment_withBuffer_setsCorrectBuffer() throws EquipmentMasterException {
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);
        ModuleList moduleList = new ModuleList();

        AddCommand addCommand = new AddCommand("STM32", 10);
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        addCommand.execute(context);

        SetBufferCommand command = new SetBufferCommand("STM32", 15.5);
        command.execute(context);

        storage.save(equipments.getAllEquipments());
        ArrayList<Equipment> loadedList = storage.load();

        assertEquals(1, loadedList.size());
        Equipment loadedEquipment = loadedList.get(0);
        assertEquals(15.5, loadedEquipment.getBufferPercentage(), 0.0001);
    }

    @Test
    public void parseEquipment_withoutBuffer_setsDefaultBuffer() throws EquipmentMasterException {
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);
        ModuleList moduleList = new ModuleList();

        AddCommand addCommand = new AddCommand("STM32", 10);
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        addCommand.execute(context);

        Equipment equipment = equipments.getEquipment(0);
        assertEquals(0.0, equipment.getBufferPercentage(), 0.0001);
    }
}
