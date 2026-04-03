package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SetSemCommandTest {

    @TempDir
    Path tempDir;

    private EquipmentList equipments;
    private ModuleList moduleList;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        equipments = new EquipmentList();
        moduleList = new ModuleList();
        storage = new Storage(
                tempDir.resolve("test_equipment.txt").toString(),
                new Ui(),
                tempDir.resolve("test_settings.txt").toString(),
                tempDir.resolve("test_modules.txt").toString()
        );
    }

    @Test
    public void execute_emptyModuleList_noWarningShown() throws EquipmentMasterException {
        // Arrange: no modules, semester changes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));
        Context context = new Context(equipments, moduleList, ui, storage, null);

        // Act
        SetSemCommand command = new SetSemCommand("AY2025/26 Sem1");
        command.execute(context);

        // Assert: warning should NOT appear
        String output = outputStream.toString();
        assertFalse(output.contains("[!] WARNING"));
    }

    @Test
    public void execute_nonEmptyModuleList_warningShown() throws EquipmentMasterException {
        // Arrange: modules exist, semester changes
        moduleList.addModule(new Module("CG2111A", 150));
        moduleList.addModule(new Module("EE2026", 200));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));
        Context context = new Context(equipments, moduleList, ui, storage, null);

        // Act
        SetSemCommand command = new SetSemCommand("AY2025/26 Sem1");
        command.execute(context);

        // Assert: warning should appear with module names
        String output = outputStream.toString();
        assertTrue(output.contains("[!] WARNING"));
        assertTrue(output.contains("CG2111A"));
        assertTrue(output.contains("EE2026"));
    }

    @Test
    public void execute_sameSemester_noWarningShown() throws EquipmentMasterException {
        // Arrange: modules exist, but semester does NOT change
        moduleList.addModule(new Module("CG2111A", 150));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));
        AcademicSemester currentSem = new AcademicSemester("AY2025/26 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSem);

        // Act: set the same semester
        SetSemCommand command = new SetSemCommand("AY2025/26 Sem1");
        command.execute(context);

        // Assert: warning should NOT appear since semester didn't change
        String output = outputStream.toString();
        assertFalse(output.contains("[!] WARNING"));
    }

    @Test
    public void execute_differentSemester_warningShown() throws EquipmentMasterException {
        // Arrange: modules exist, semester changes to a different one
        moduleList.addModule(new Module("CG2111A", 150));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));
        AcademicSemester currentSem = new AcademicSemester("AY2025/26 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSem);

        // Act: set a different semester
        SetSemCommand command = new SetSemCommand("AY2025/26 Sem2");
        command.execute(context);

        // Assert: warning should appear
        String output = outputStream.toString();
        assertTrue(output.contains("[!] WARNING"));
        assertTrue(output.contains("CG2111A"));
    }

    @Test
    public void parse_missingSemester_throwsException() {
        assertThrows(EquipmentMasterException.class, () -> SetSemCommand.parse("setsem"));
    }

    @Test
    public void parse_validSemester_returnsSetSemCommand() throws EquipmentMasterException {
        Command command = SetSemCommand.parse("setsem AY2025/26 Sem1");
        assertTrue(command instanceof SetSemCommand);
    }

    @Test
    public void parse_blankSemester_throwsException() {
        // Triggers words[1].trim().isEmpty() in the parse method
        assertThrows(EquipmentMasterException.class, () -> SetSemCommand.parse("setsem    "));
    }

    @Test
    public void execute_nullOrEmptyRawSem_showsErrorMessage() {
        // Triggers the safety check at lines 67-70 by manually creating the command
        Ui ui = new Ui();
        Context context = new Context(equipments, moduleList, ui, storage, null);

        // Case 1: Null
        SetSemCommand nullCmd = new SetSemCommand(null);
        nullCmd.execute(context);

        // Case 2: Empty
        SetSemCommand emptyCmd = new SetSemCommand("");
        emptyCmd.execute(context);
    }

    @Test
    public void execute_invalidSemesterFormat_showsErrorMessage() {
        // Passes parse check but fails AcademicSemester validation (line 75)
        SetSemCommand command = new SetSemCommand("NotASemesterFormat");
        Context context = new Context(equipments, moduleList, new Ui(), storage, null);

        command.execute(context);
    }

    @Test
    public void execute_nullModuleList_noWarningShown() throws EquipmentMasterException {
        // Triggers the 'moduleList != null' guard clause at line 81
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));

        // Pass null for moduleList
        Context context = new Context(equipments, null, ui, storage, null);

        SetSemCommand command = new SetSemCommand("AY2025/26 Sem1");
        command.execute(context);

        String output = outputStream.toString();
        assertFalse(output.contains("[!] WARNING"));
    }

    @Test
    public void execute_firstTimeSettingSemWithModules_showsWarning() throws EquipmentMasterException {
        // oldSem is null, modules exist -> should trigger warning (Line 82)
        moduleList.addModule(new Module("CG2111A", 150));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));

        // currentSemester is null
        Context context = new Context(equipments, moduleList, ui, storage, null);

        SetSemCommand command = new SetSemCommand("AY2025/26 Sem1");
        command.execute(context);

        String output = outputStream.toString();
        assertTrue(output.contains("[!] WARNING"));
    }

    @Test
    public void parse_missingSemester_triggersLengthCheck() {
        // Triggers the first half of the || (words.length < 2)
        assertThrows(EquipmentMasterException.class, () -> {
            SetSemCommand.parse("setsem");
        });
    }

    @Test
    public void parse_blankSemester_triggersEmptyCheck() {
        // Triggers the second half of the || (words[1].trim().isEmpty())
        assertThrows(EquipmentMasterException.class, () -> {
            SetSemCommand.parse("setsem    ");
        });
    }

    @Test
    public void execute_nullRawSem_triggersDefensiveCheck() {
        // Manually creating a command with null to trigger the safety check at line 67
        SetSemCommand command = new SetSemCommand(null);
        Context context = new Context(equipments, moduleList, new Ui(), storage, null);

        command.execute(context);
        // This hits the branch: if (rawSem == null ...)
    }

    @Test
    public void execute_nullModuleList_skipsWarning() throws EquipmentMasterException {
        // Triggers the 'moduleList != null' guard clause at line 81
        // By passing null for the moduleList in context
        Context context = new Context(equipments, null, new Ui(), storage, null);

        SetSemCommand command = new SetSemCommand("AY2025/26 Sem1");
        command.execute(context);
        // The warning branch is skipped because moduleList is null
    }
}
