package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.semester.AcademicSemester;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


/**
 * Tests the ReportCommand, focusing on parsing logic and the accuracy of the aging report.
 */
public class ReportCommandTest {

    private EquipmentList equipments;
    private Ui ui;
    private Storage storage;

    // Used to capture terminal output for verification
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        equipments = new EquipmentList();
        ui = new Ui();
        storage = null;
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut); // Restore original System.out after each test
    }

    @Test
    public void parse_validAgingReport_success() throws EquipmentMasterException {
        // Without target semester
        ReportCommand cmd1 = ReportCommand.parse("report aging");
        assertNotNull(cmd1);

        // With target semester
        ReportCommand cmd2 = ReportCommand.parse("report aging AY2028/29 Sem1");
        assertNotNull(cmd2);
    }

    @Test
    public void parse_missingReportType_throwsException() {
        assertThrows(EquipmentMasterException.class, () -> ReportCommand.parse("report"));
    }

    @Test
    public void execute_invalidReportType_showsError() {
        ModuleList moduleList = new ModuleList();
        ReportCommand command = new ReportCommand("broken", "");
        command.execute(equipments, moduleList, ui, storage);

        assertTrue(outContent.toString().contains("Invalid report type"));
    }

    @Test
    public void execute_agingReport_identifiesExpiredAndSkipsLegacy() throws EquipmentMasterException {
        AcademicSemester purchaseSem = new AcademicSemester("AY2024/25 Sem1");
        ModuleList moduleList = new ModuleList();

        // 1. Expired Equipment (Lifespan: 2 years. Age in AY28/29 Sem1 will be 4 years) -> SHOULD BE REPORTED
        Equipment expiredEq = new Equipment("STM32", 10, 10, 0, purchaseSem, 2.0, 0);
        equipments.addEquipment(expiredEq);

        // 2. Healthy Equipment (Lifespan: 10 years. Age will be 4 years) -> SHOULD BE SKIPPED
        Equipment healthyEq = new Equipment("Oscilloscope", 5, 5, 0, purchaseSem, 10.0, 0);
        equipments.addEquipment(healthyEq);

        // 3. Legacy Equipment (No purchaseSem or lifespan) -> SHOULD BE SKIPPED
        Equipment legacyEq = new Equipment("Old Multimeter", 20);
        equipments.addEquipment(legacyEq);

        // Execute report using a future semester to simulate time passing
        ReportCommand command = new ReportCommand("aging", "AY2028/29 Sem1");
        command.execute(equipments, moduleList, ui, storage);

        String output = outContent.toString();

        // Verify the report header is printed
        assertTrue(output.contains("Aging Equipment Report"));
        assertTrue(output.contains("AY2028/29 Sem1"));

        // Verify the expired equipment IS in the report
        assertTrue(output.contains("1. STM32"));
        assertTrue(output.contains("REPLACE SOON"));

        // Verify the healthy and legacy equipments ARE NOT in the report
        assertFalse(output.contains("Oscilloscope"));
        assertFalse(output.contains("Old Multimeter"));
    }

    @Test
    public void execute_noExpiredEquipment_showsGreatNews() throws EquipmentMasterException {
        AcademicSemester purchaseSem = new AcademicSemester("AY2025/26 Sem1");
        ModuleList moduleList = new ModuleList();

        // Add a brand new equipment with a 5-year lifespan
        Equipment newEq = new Equipment("3D Printer", 2, 2, 0, purchaseSem, 5.0, 0);
        equipments.addEquipment(newEq);

        // Check report in the SAME semester it was bought
        ReportCommand command = new ReportCommand("aging", "AY2025/26 Sem1");
        command.execute(equipments, moduleList, ui, storage);

        String output = outContent.toString();
        assertTrue(output.contains("Great news! No equipment needs replacement"));
    }
}
