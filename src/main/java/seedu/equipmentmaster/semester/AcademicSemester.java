package seedu.equipmentmaster.semester;

import seedu.equipmentmaster.exception.EquipmentMasterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a university academic semester (e.g., AY2024/25 Sem1).
 * Provides utility methods to parse semester strings and calculate time differences.
 */
public class AcademicSemester {
    private static final String SEM_REGEX = "^AY(\\d{4})/(\\d{2}) Sem([12])$";
    private final int startYear;
    private final int semester;

    /**
     * Constructs an AcademicSemester object by parsing a raw string.
     * @param rawInput The string representation of the semester (e.g., "AY2024/25 Sem1").
     * @throws EquipmentMasterException If the format is invalid or years are not consecutive.
     */
    public AcademicSemester(String rawInput) throws EquipmentMasterException {
        Pattern pattern = Pattern.compile(SEM_REGEX);
        Matcher matcher = pattern.matcher(rawInput.trim());

        if (!matcher.matches()) {
            throw new EquipmentMasterException("Invalid format! Use AY[YYYY]/[YY] Sem[1/2] (e.g., AY2024/25 Sem1)");
        }

        this.startYear = Integer.parseInt(matcher.group(1));
        int endYearShort = Integer.parseInt(matcher.group(2));
        this.semester = Integer.parseInt(matcher.group(3));

        // Validation: Ensure the start year and end year are consecutive (e.g., 2024 and 25)
        if ((startYear + 1) % 100 != endYearShort) {
            throw new EquipmentMasterException("Academic Year mismatch! The years must be consecutive (e.g., 2024/25).");
        }
    }

    /**
     * Calculates the age in years relative to a given "current" semester.
     * 1 Semester is treated as 0.5 years.
     * @param current The reference semester (usually the system's current time).
     * @return The age in years as a double.
     */
    public double calculateAgeInYears(AcademicSemester current) {
        int elapsedSemesters = (current.startYear - this.startYear) * 2 + (current.semester - this.semester);
        // Assertion: Age should not be negative in a logical system state.
        // This helps catch logic errors during development.
        assert elapsedSemesters >= 0 : "Calculated age cannot be negative!";
        return elapsedSemesters / 2.0;
    }

    /**
     * Returns the string representation of the academic semester.
     * @return Formatted string (e.g., "AY2024/25 Sem1").
     */
    @Override
    public String toString() {
        String endYearShort = String.format("%02d", (startYear + 1) % 100);
        return "AY" + startYear + "/" + endYearShort + " Sem" + semester;
    }

    /**
     * Checks if this semester is equal to another object.
     * @param obj The object to compare with.
     * @return true if the start year and semester number match.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AcademicSemester) {
            AcademicSemester other = (AcademicSemester) obj;
            return this.startYear == other.startYear && this.semester == other.semester;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(startYear);
        result = 31 * result + Integer.hashCode(semester);
        return result;
    }
}
