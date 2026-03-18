package seedu.equipmentmaster.ui;

import org.junit.jupiter.api.Test;
import java.util.List;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.semester.AcademicSemester;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UiTableTest {
    @Test
    public void toString_stringRows_printsFormattedTable() {
        UiTable uiTable = new UiTable();
        uiTable.addRow(new UiTableRow("STM32 Development Board", "Total: 50", "Available: 45", "Loaned: 5"));
        uiTable.addRow(new UiTableRow("Basys3 FPGA", "Total: 20", "Available: 20", "Loaned: 0"));
        uiTable.addRow(new UiTableRow("HDMI Cable", "Total: 100", "Available: <N/A>", "Loaned: <N/A>"));

        String expectedOutput = """
                1. STM32 Development Board | Total: 50  | Available: 45    | Loaned: 5   \s
                2. Basys3 FPGA             | Total: 20  | Available: 20    | Loaned: 0   \s
                3. HDMI Cable              | Total: 100 | Available: <N/A> | Loaned: <N/A>
                """.trim();

        assertEquals(expectedOutput, uiTable.toString().trim());
    }

    @Test
    public void toString_equipmentRows_printsFormattedTable() throws EquipmentMasterException {
        UiTable uiTable = new UiTable();
        AcademicSemester testSem = new AcademicSemester("AY2025/26 Sem2");
        uiTable.addRow(new UiTableRow(new Equipment("STM32 Development Board", 50, 45, 5, testSem, 5.0)));
        uiTable.addRow(new UiTableRow(new Equipment("Basys3 FPGA", 20, 20, 0, testSem, 5.0)));
        uiTable.addRow(new UiTableRow(new Equipment("HDMI Cable", 100, 100, 0, testSem, 5.0)));

        String expectedOutput = """
                1. STM32 Development Board | Total: 50  | Available: 45  | Loaned: 5 | \
                Purchase: AY2025/26 Sem2 | Life: 5.0 years
                2. Basys3 FPGA             | Total: 20  | Available: 20  | Loaned: 0 | \
                Purchase: AY2025/26 Sem2 | Life: 5.0 years
                3. HDMI Cable              | Total: 100 | Available: 100 | Loaned: 0 | \
                Purchase: AY2025/26 Sem2 | Life: 5.0 years
                """.trim();

        assertEquals(expectedOutput, uiTable.toString().trim());
    }

    @Test
    public void toString_withHeader_printsFormattedTable() throws EquipmentMasterException {
        UiTable uiTable = new UiTable(true);
        // Assuming your AcademicSemester toString returns "AY2025/26 Sem2"
        AcademicSemester testSem = new AcademicSemester("AY2025/26 Sem2");

        // Header row must match the column count (6 columns)
        uiTable.addRow(new UiTableRow("Name", "Total", "Available", "Loaned", "Purchase", "Life"));

        // Add rows using the Equipment objects
        uiTable.addRow(new UiTableRow(new Equipment("STM32 Development Board", 50, 45, 5, testSem, 5.0)));
        uiTable.addRow(new UiTableRow(new Equipment("Basys3 FPGA", 20, 20, 0, testSem, 5.0)));

        // Test a "Legacy" case to ensure your <N/A> logic works!
        uiTable.addRow(new UiTableRow(new Equipment("HDMI Cable", 100, 100, 0, null, 0.0)));

        String expectedOutput = """
            #  Name                    | Total      | Available      |\
             Loaned    | Purchase                 | Life
            1. STM32 Development Board | Total: 50  | Available: 45  |\
             Loaned: 5 | Purchase: AY2025/26 Sem2 | Life: 5.0 years
            2. Basys3 FPGA             | Total: 20  | Available: 20  |\
             Loaned: 0 | Purchase: AY2025/26 Sem2 | Life: 5.0 years
            3. HDMI Cable              | Total: 100 | Available: 100 |\
             Loaned: 0 | Purchase: <N/A>          | Life: <N/A>
            """.trim();

        // Convert both strings to a list of trimmed lines and compare those
        List<String> expectedLines = expectedOutput.lines().map(String::stripTrailing).toList();
        List<String> actualLines = uiTable.toString().lines().map(String::stripTrailing).toList();

        assertEquals(expectedLines, actualLines);
    }

    @Test
    public void toString_manyRows_formatsIndicesCorrectly() {
        UiTable uiTable = new UiTable();
        for (int i = 0; i < 100; i++) {
            uiTable.addRow(new UiTableRow(new Equipment("STM32 Development Board", 50, 45, 5)));
        }

        String finalString = uiTable.toString();
        assertTrue(finalString.contains("1.   STM32 Development Board | Total: 50 | Available: 45 | Loaned: 5"));
        assertTrue(finalString.contains("14.  STM32 Development Board | Total: 50 | Available: 45 | Loaned: 5"));
        assertTrue(finalString.contains("100. STM32 Development Board | Total: 50 | Available: 45 | Loaned: 5"));
    }

    @Test
    public void toString_emptyTable_printsEmptyMessage() {
        UiTable uiTable = new UiTable();
        String expectedOutput = "<empty table>";
        assertEquals(expectedOutput, uiTable.toString());
    }
}
