package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListCommandTest {
    private static final String TEST_FILE_PATH = "test_equipment.txt";
    private static final String TEST_SETTING_FILE_PATH = "test_setting.txt";
    private static final String TEST_MODULE_FILE_PATH = "test_module.txt";

    @Test
    public void execute_emptyList_showsEmptyMessage() throws EquipmentMasterException {
        EquipmentList equipments = new EquipmentList();
        ModuleList moduleList = new ModuleList();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);
        AcademicSemester currentSem = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSem);

        new ListCommand().execute(context);

        // This will now pass because ListCommand actually prints this string
        assertTrue(outputStream.toString().contains("The equipment list is currently empty."));
    }

    @Test
    public void execute_nonEmptyList_showsTable() throws EquipmentMasterException {
        EquipmentList equipments = new EquipmentList();
        equipments.addEquipment(new Equipment("Oscilloscope", 2, 2, 0,
                new AcademicSemester("AY2024/25 Sem1"), 10.0, new ArrayList<>(), 0, 0.0));

        ModuleList moduleList = new ModuleList();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));
        Storage storage = new Storage(TEST_FILE_PATH, ui, TEST_SETTING_FILE_PATH, TEST_MODULE_FILE_PATH);
        AcademicSemester currentSem = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSem);

        new ListCommand().execute(context);

        assertTrue(outputStream.toString().contains("Oscilloscope"));
    }
}
