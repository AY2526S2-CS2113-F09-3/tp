package seedu.equipmentmaster.semester;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Advanced tests for AcademicSemester to handle edge cases in date logic.
 */
public class AcademicSemesterTest {

    @Test
    public void calculateAge_sameSemester_returnsZero() throws EquipmentMasterException {
        AcademicSemester start = new AcademicSemester("AY2024/25 Sem1");
        AcademicSemester end = new AcademicSemester("AY2024/25 Sem1");
        // Age should be 0.0 if the semesters are the same
        assertEquals(0.0, start.calculateAgeInYears(end));
    }

    @Test
    public void calculateAge_oneSemesterDifference_returnsHalfYear() throws EquipmentMasterException {
        AcademicSemester sem1 = new AcademicSemester("AY2024/25 Sem1");
        AcademicSemester sem2 = new AcademicSemester("AY2024/25 Sem2");
        // One semester gap is exactly 0.5 years
        assertEquals(0.5, sem1.calculateAgeInYears(sem2));
    }

    @Test
    public void equals_differentSemesters_returnsFalse() throws EquipmentMasterException {
        AcademicSemester sem1 = new AcademicSemester("AY2024/25 Sem1");
        AcademicSemester sem2 = new AcademicSemester("AY2024/25 Sem2");
        assertNotEquals(sem1, sem2);
    }

    @Test
    public void constructor_wrongYearFormat_throwsException() {
        // Test with 2-digit start year (Invalid, we require 4 digits)
        assertThrows(EquipmentMasterException.class, () -> new AcademicSemester("AY24/25 Sem1"));

        // Test with non-numeric years
        assertThrows(EquipmentMasterException.class, () -> new AcademicSemester("AYabcd/ef Sem1"));
    }
}
