package seedu.duke;

import org.junit.jupiter.api.Test;
import seedu.duke.equipment.Equipment;
import seedu.duke.ui.UiTable;
import seedu.duke.ui.UiTableRow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UiTableTest {
    @Test
    public void uiTableTest(){
        UiTable uiTable = new UiTable();
        uiTable.addRow(new UiTableRow("STM32 Development Board","Total: 50","Available: 45","Loaned: 5"));
        uiTable.addRow(new UiTableRow("Basys3 FPGA","Total: 20","Available: 20","Loaned: 0"));
        uiTable.addRow(new UiTableRow("HDMI Cable","Total: 100","Available: <N/A>","Loaned: <N/A>"));

        String expectedOutput = """
1. STM32 Development Board | Total: 50  | Available: 45    | Loaned: 5   \s
2. Basys3 FPGA             | Total: 20  | Available: 20    | Loaned: 0   \s
3. HDMI Cable              | Total: 100 | Available: <N/A> | Loaned: <N/A>
                """.trim();

        assertEquals(expectedOutput, uiTable.toString().trim());
    }

    @Test
    public void uiTableTestEquipment(){
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
    public void uiTableTestLong() {
        UiTable uiTable = new UiTable();
        for(int i=0;i<100;i++){
            uiTable.addRow(new UiTableRow(new Equipment("STM32 Development Board", 50, 45, 5)));
        }

        String finalString = uiTable.toString();
        System.out.println(finalString);
        assertTrue(finalString.contains("1.   STM32 Development Board | Total: 50 | Available: 45 | Loaned: 5"));
        assertTrue(finalString.contains("14.  STM32 Development Board | Total: 50 | Available: 45 | Loaned: 5"));
        assertTrue(finalString.contains("100. STM32 Development Board | Total: 50 | Available: 45 | Loaned: 5"));
    }
}
