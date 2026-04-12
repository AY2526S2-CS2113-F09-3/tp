package seedu.equipmentmaster.ui;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UiTableRowTest {

    @Test
    public void constructor_arrayList_createsRowSuccessfully() {
        ArrayList<String> cols = new ArrayList<>();
        cols.add("Col1");
        cols.add("Col2");
        UiTableRow row = new UiTableRow(cols);
        assertEquals(2, row.size());
        assertEquals("Col1 | Col2", row.toString());
    }

    @Test
    public void length_invalidIndex_throwsIndexOutOfBoundsException() {
        UiTableRow row = new UiTableRow("A", "B", "C");
        assertThrows(IndexOutOfBoundsException.class, () -> row.length(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> row.length(3));
    }

    @Test
    public void constructor_equipmentOneYearLifespan_formatsCorrectly() throws EquipmentMasterException {
        Equipment eq = new Equipment("Oscilloscope", 10, 10, 0, null, 1.0, 0);
        UiTableRow row = new UiTableRow(eq);
        // "Lifespan: 1.0 year" instead of "years"
        assertEquals("Lifespan: 1.0 year", row.toString().split(" \\| ")[6]);
    }

    @Test
    public void constructor_equipmentZeroLifespan_formatsCorrectly() throws EquipmentMasterException {
        Equipment eq = new Equipment("Oscilloscope", 10, 10, 0, null, 0.0, 0);
        UiTableRow row = new UiTableRow(eq);
        // "Lifespan: <N/A>"
        assertEquals("Lifespan: <N/A>", row.toString().split(" \\| ")[6]);
    }

    @Test
    public void constructor_equipmentWithModules_formatsCorrectly() throws EquipmentMasterException {
        Equipment eq = new Equipment("Oscilloscope", 10, 10, 0, null, 1.0, 0);
        eq.addModuleCode("CS2113");
        UiTableRow row = new UiTableRow(eq);
        // "Modules: CS2113"
        assertEquals("Modules: [CS2113]", row.toString().split(" \\| ")[7]);
    }
}

