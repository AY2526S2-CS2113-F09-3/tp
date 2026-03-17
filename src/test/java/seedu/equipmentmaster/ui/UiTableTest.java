package seedu.equipmentmaster.ui;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.equipment.Equipment;

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
    public void toString_equipmentRows_printsFormattedTable() {
        UiTable uiTable = new UiTable();
        uiTable.addRow(new UiTableRow(new Equipment("STM32 Development Board", 50, 45, 5)));
        uiTable.addRow(new UiTableRow(new Equipment("Basys3 FPGA", 20)));
        uiTable.addRow(new UiTableRow(new Equipment("HDMI Cable", 100)));

        String expectedOutput = """
                1. STM32 Development Board | Total: 50  | Available: 45  | Loaned: 5
                2. Basys3 FPGA             | Total: 20  | Available: 20  | Loaned: 0
                3. HDMI Cable              | Total: 100 | Available: 100 | Loaned: 0
                """.trim();

        assertEquals(expectedOutput, uiTable.toString().trim());
    }

    @Test
    public void toString_withHeader_printsFormattedTable() {
        UiTable uiTable = new UiTable(true);
        uiTable.addRow(new UiTableRow("Name","Total","Available","Loaned"));

        uiTable.addRow(new UiTableRow(new Equipment("STM32 Development Board", 50, 45, 5)));
        uiTable.addRow(new UiTableRow(new Equipment("Basys3 FPGA", 20)));
        uiTable.addRow(new UiTableRow(new Equipment("HDMI Cable", 100)));

        String expectedOutput = """
                #  Name                    | Total      | Available      | Loaned\s\s\s
                1. STM32 Development Board | Total: 50  | Available: 45  | Loaned: 5
                2. Basys3 FPGA             | Total: 20  | Available: 20  | Loaned: 0
                3. HDMI Cable              | Total: 100 | Available: 100 | Loaned: 0
                """.trim();

        assertEquals(expectedOutput, uiTable.toString().trim());
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
