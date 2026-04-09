//@@author Hongyu1231
package seedu.equipmentmaster.semester;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    @Test
    public void constructor_yearMismatch_throwsException() {
        // Triggers the 'if ((startYear + 1) % 100 != endYearShort)' branch
        assertThrows(EquipmentMasterException.class, () -> {
            new AcademicSemester("AY2024/26 Sem1"); // 2024 and 26 are not consecutive
        });
    }

    @Test
    public void calculateAge_multipleYearsDifference_returnsCorrectAge() throws EquipmentMasterException {
        // Covers calculation across multiple years
        AcademicSemester oldSem = new AcademicSemester("AY2020/21 Sem1");
        AcademicSemester currentSem = new AcademicSemester("AY2024/25 Sem2");
        // Difference: 4 full years (8 semesters) + 1 semester difference = 9 semesters = 4.5 years
        assertEquals(4.5, oldSem.calculateAgeInYears(currentSem));
    }

    @Test
    public void toString_centuryRollover_formatsCorrectly() throws EquipmentMasterException {
        // Covers the %02d string formatting in toString()
        AcademicSemester sem = new AcademicSemester("AY2099/00 Sem2");
        assertEquals("AY2099/00 Sem2", sem.toString());
    }

    @Test
    public void equals_sameValues_returnsTrue() throws EquipmentMasterException {
        // Covers the 'return true' branch in equals
        AcademicSemester sem1 = new AcademicSemester("AY2024/25 Sem1");
        AcademicSemester sem2 = new AcademicSemester("AY2024/25 Sem1");
        assertTrue(sem1.equals(sem2));
    }

    @Test
    public void equals_nullOrDifferentClass_returnsFalse() throws EquipmentMasterException {
        // Covers the 'return false' path when obj is null or a different type
        AcademicSemester sem = new AcademicSemester("AY2024/25 Sem1");
        assertFalse(sem.equals(null));
        assertFalse(sem.equals("AY2024/25 Sem1")); // Comparing to a String
    }

    @Test
    public void hashCode_sameValues_returnsSameHashCode() throws EquipmentMasterException {
        // Covers the hashCode method
        AcademicSemester sem1 = new AcademicSemester("AY2024/25 Sem1");
        AcademicSemester sem2 = new AcademicSemester("AY2024/25 Sem1");
        assertEquals(sem1.hashCode(), sem2.hashCode());
    }

    @Test
    public void equals_exhaustiveBranchCoverage_coversAllYellowLines() throws EquipmentMasterException {
        AcademicSemester base = new AcademicSemester("AY2024/25 Sem1");

        // Scenario 1: True && True (Same year, same semester)
        AcademicSemester exactlySame = new AcademicSemester("AY2024/25 Sem1");
        assertTrue(base.equals(exactlySame));

        // Scenario 2: True && False (Same year, but different semester)
        AcademicSemester diffSem = new AcademicSemester("AY2024/25 Sem2");
        assertFalse(base.equals(diffSem));

        // Scenario 3: False && ? (Different year. Triggers the short-circuit of the '&&' operator,
        // which completely skips checking the semester, satisfying JaCoCo's branch requirements)
        AcademicSemester diffYear = new AcademicSemester("AY2025/26 Sem1");
        assertFalse(base.equals(diffYear));
    }

    @Test
    public void calculateAge_futureSemester_throwsException() throws EquipmentMasterException {
        // Covers the branch where elapsedSemesters < 0
        AcademicSemester newerSem = new AcademicSemester("AY2025/26 Sem1");
        AcademicSemester olderSem = new AcademicSemester("AY2024/25 Sem1");

        // Since the source code uses the 'assert' keyword, it throws an EquipmentMasterException
        assertThrows(EquipmentMasterException.class, () -> {
            newerSem.calculateAgeInYears(olderSem);
        });
    }

    /**
     * Targets the assertion in the constructor.
     */
    @Test
    public void constructor_nullInput_assertionFails() {
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            new AcademicSemester(null);
        });
        assertTrue(thrown.getMessage().contains("Raw semester input cannot be null"));
    }

    /**
     * Targets the assertion in calculateAgeInYears.
     */
    @Test
    public void calculateAge_nullCurrentSemester_assertionFails() throws EquipmentMasterException {
        AcademicSemester sem = new AcademicSemester("AY2024/25 Sem1");
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            sem.calculateAgeInYears(null);
        });
        assertTrue(thrown.getMessage().contains("Current reference semester cannot be null"));
    }

    /**
     * Targets the 'if (this == obj)' optimization branch in equals().
     */
    @Test
    public void equals_sameObjectReference_returnsTrue() throws EquipmentMasterException {
        AcademicSemester sem = new AcademicSemester("AY2024/25 Sem1");
        // Comparing the exact same memory reference
        assertTrue(sem.equals(sem));
    }
}
