package seedu.equipmentmaster.equipment;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.semester.AcademicSemester;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EquipmentTest {

    @Test
    public void constructor_basicInit_setsDefaultValuesCorrectly() {
        Equipment eq = new Equipment("Oscilloscope", 10);

        assertEquals("Oscilloscope", eq.getName());
        assertEquals(10, eq.getQuantity());
        assertEquals(10, eq.getAvailable());
        assertEquals(0, eq.getLoaned());
        assertEquals(0, eq.getMinQuantity());
        assertEquals(0.0, eq.getBufferPercentage());
        assertNotNull(eq.getModuleCodes());
        assertTrue(eq.getModuleCodes().isEmpty());
    }

    @Test
    public void constructor_nullModuleList_initializesEmptyList() {
        Equipment eq = new Equipment("Multimeter", 5, 5, 0, null, 0.0, null, 0, 0.0);

        assertNotNull(eq.getModuleCodes());
        assertTrue(eq.getModuleCodes().isEmpty());
    }

    @Test
    public void setBufferPercentage_negativeValue_throwsIllegalArgumentException() {
        Equipment eq = new Equipment("Resistor", 100);

        assertThrows(IllegalArgumentException.class, () -> {
            eq.setBufferPercentage(-10.0);
        });
    }

    @Test
    public void addModuleCode_validCodes_addsUppercaseAndTrims() {
        Equipment eq = new Equipment("FPGA Board", 5);

        eq.addModuleCode(" cg2023 ");
        eq.addModuleCode("CS2113");
        eq.addModuleCode("cg2023"); // Duplicate with different case

        ArrayList<String> modules = eq.getModuleCodes();
        assertEquals(2, modules.size());
        assertTrue(modules.contains("CG2023"));
        assertTrue(modules.contains("CS2113"));
    }

    @Test
    public void addModuleCode_nullOrEmptyInput_ignoresInput() {
        Equipment eq = new Equipment("Breadboard", 20);

        eq.addModuleCode(null);
        eq.addModuleCode("");
        eq.addModuleCode("   ");

        assertTrue(eq.getModuleCodes().isEmpty());
    }

    @Test
    public void toString_partialData_formatsWithoutNulls() {
        Equipment eq = new Equipment("Wire Stripper", 15);
        String expected = "Wire Stripper | Total: 15 | Available: 15 | loaned: 0 | Min: 0";

        assertEquals(expected, eq.toString());
    }

    @Test
    public void toFileString_fullData_formatsCorrectly() throws Exception {
        AcademicSemester sem = new AcademicSemester("AY2024/25 Sem1");
        ArrayList<String> modules = new ArrayList<>();
        modules.add("EE2211");
        modules.add("CG2023");

        Equipment eq = new Equipment("Soldering Iron", 8, 6, 2, sem, 5.0, modules, 2, 15.0);

        String expected = "Soldering Iron | 8 | 6 | 2 | 2 | AY2024/25 Sem1 | 5.0 | EE2211,CG2023 | 15.0";
        assertEquals(expected, eq.toFileString());
    }
}
