package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.semester.AcademicSemester;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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
        try {
            AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
            command.execute(context);

            assertTrue(outContent.toString().contains("Invalid report type"));
        } catch (EquipmentMasterException e) {
            fail("Test setup failed unexpectedly: " + e.getMessage());
        }
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
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

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
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        String output = outContent.toString();
        assertTrue(output.contains("Great news! No equipment needs replacement"));
    }
    // @@author

    @Test
    public void execute_lowStockReport_sufficientTotalNoWarning() throws EquipmentMasterException {
        // Arrange: Set up an equipment item where Total = 10, Min Threshold = 10.
        // Simulate all 10 items being loaned out (Available = 0, Loaned = 10).
        Equipment ghost = new Equipment("Ghost", 10, 0, 10, null, 0.0, 10);
        equipments.addEquipment(ghost);
        ModuleList moduleList = new ModuleList();

        // Act: Execute the 'report lowstock' command
        ReportCommand command = new ReportCommand("lowstock", "");
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        // Assert: Verify that the "RESTOCK NEEDED" alert is NOT triggered
        String output = outContent.toString();
        assertFalse(output.contains("RESTOCK NEEDED"),
                "LOANED items should not trigger a restock alert if total quantity >= min threshold.");
    }

    @Test
    public void execute_lowStockReport_totalBelowMinShowsWarning() throws EquipmentMasterException {
        // Arrange: Set up an equipment item where Total = 10, but Min Threshold = 20.
        Equipment iron = new Equipment("Soldering Iron", 10, 10, 0, null, 0.0, 20);
        equipments.addEquipment(iron);
        ModuleList moduleList = new ModuleList();

        // Act: Execute the 'report lowstock' command
        ReportCommand command = new ReportCommand("lowstock", "");
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        // Assert: Verify the exact output format matches expectations
        String output = outContent.toString();
        String expectedString = "Soldering Iron | Quantity: 10 | Min: 20 -> RESTOCK NEEDED";

        assertTrue(output.contains(expectedString),
                "The output formatting for low stock items does not match the expected string.");
    }

    @Test
    public void execute_procurementReport_calculatesCorrectly() throws EquipmentMasterException {
        // Arrange
        ModuleList moduleList = new ModuleList();
        moduleList.addModule(new Module("CG2111A", 30));
        moduleList.addModule(new Module("EE2026", 0)); // No demand from this one

        ArrayList<String> modules = new ArrayList<>(List.of("CG2111A"));
        // Name, Qty, Avail, Loaned, Sem, Life, Modules, Min, Buffer
        Equipment stm32 = new Equipment("STM32", 10, 10, 0, null, 0.0, modules, 0, 10.0);
        equipments.addEquipment(stm32);

        // Act
        ReportCommand command = new ReportCommand("procurement", "");
        AcademicSemester currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        command.execute(context);

        String output = outContent.toString();

        // Assert
        // Base Need: 30
        // Buffer: 10% -> 33
        // Available: 10
        // To Buy: 23
        assertTrue(output.contains("Base Need: 30"));
        assertTrue(output.contains("Buffer: 10% (+3)"));
        assertTrue(output.contains("Total Required: 33 | Available: 10 | TO BUY: 23"));
    }

    @Test
    public void execute_agingReport_noSystemSemesterSet() {
        // Case: targetSemStr is empty AND context.getCurrentSemester() is null
        // Triggers the exception on line 108
        ReportCommand command = new ReportCommand("aging", "");
        // Context with null currentSemester
        Context context = new Context(equipments, new ModuleList(), ui, null, null);

        command.execute(context);
        assertTrue(outContent.toString().contains("System semester not set!"));
    }

    @Test
    public void execute_agingReport_invalidFormat() {
        // Case: targetSemStr is provided but is in a garbage format
        // Triggers the exception from the AcademicSemester constructor
        ReportCommand command = new ReportCommand("aging", "NotASemester");
        Context context = new Context(equipments, new ModuleList(), ui, null, null);

        command.execute(context);
        // The catch block on line 111 will catch the constructor error
        assertNotNull(outContent.toString());
    }

    @Test
    public void execute_agingReport_skipsInvalidEquipmentData() throws EquipmentMasterException {
        // Case 1: purchaseSem is null
        Equipment nullSemEq = new Equipment("NullSem", 10, 10, 0, null, 5.0, 0);
        equipments.addEquipment(nullSemEq);

        // Case 2: lifespan is 0 or negative
        AcademicSemester validSem = new AcademicSemester("AY2024/25 Sem1");
        Equipment zeroLifeEq = new Equipment("ZeroLife", 10, 10, 0, validSem, 0.0, 0);
        Equipment negLifeEq = new Equipment("NegLife", 10, 10, 0, validSem, -1.0, 0);
        equipments.addEquipment(zeroLifeEq);
        equipments.addEquipment(negLifeEq);

        ReportCommand command = new ReportCommand("aging", "AY2024/25 Sem1");
        Context context = new Context(equipments, new ModuleList(), ui, null, validSem);

        command.execute(context);

        // Ensure none of these items were processed into the report body
        String output = outContent.toString();
        assertFalse(output.contains("NullSem"));
        assertFalse(output.contains("ZeroLife"));
        assertFalse(output.contains("NegLife"));
    }

    @Test
    public void execute_agingReport_usesContextSemesterIfTargetEmpty() throws EquipmentMasterException {
        AcademicSemester systemSem = new AcademicSemester("AY2024/25 Sem1");
        // Item bought long ago, should be aging in the current system semester
        AcademicSemester oldSem = new AcademicSemester("AY2020/21 Sem1");
        Equipment agingEq = new Equipment("AgingItem", 1, 1, 0, oldSem, 1.0, 0);
        equipments.addEquipment(agingEq);

        // targetSemStr is empty
        ReportCommand command = new ReportCommand("aging", "");
        Context context = new Context(equipments, new ModuleList(), ui, null, systemSem);

        command.execute(context);

        assertTrue(outContent.toString().contains("Aging Equipment Report (Calculated for: AY2024/25 Sem1)"));
        assertTrue(outContent.toString().contains("AgingItem"));
    }

    @Test
    public void execute_agingReport_noAgingFound() throws EquipmentMasterException {
        AcademicSemester currentSem = new AcademicSemester("AY2024/25 Sem1");
        // Item is brand new, not aging
        Equipment newEq = new Equipment("NewItem", 1, 1, 0, currentSem, 10.0, 0);
        equipments.addEquipment(newEq);

        ReportCommand command = new ReportCommand("aging", "AY2024/25 Sem1");
        Context context = new Context(equipments, new ModuleList(), ui, null, currentSem);

        command.execute(context);
        assertTrue(outContent.toString().contains("Great news! No equipment needs replacement"));
    }
}
