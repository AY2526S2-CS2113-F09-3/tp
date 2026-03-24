// @@author Hongyu1231
package seedu.equipmentmaster.module;

import seedu.equipmentmaster.exception.EquipmentMasterException;

/**
 * Represents a course module in the equipment management system.
 * Tracks the module's name and its associated student enrollment (pax).
 */
public class Module {
    private String name;
    private int pax;

    /**
     * Constructs a {@code Module} with the specified name and enrollment number.
     *
     * @param name The name of the module (e.g., "CG2111A").
     * @param pax  The number of students enrolled. Must be 0 or a positive integer.
     * @throws EquipmentMasterException If the provided pax is negative.
     */
    public Module(String name, int pax) throws EquipmentMasterException {
        if (pax < 0) {
            throw new EquipmentMasterException("Pax (enrollment number) cannot be negative.");
        }
        this.name = name;
        this.pax = pax;
    }

    /**
     * Returns the name of the module.
     *
     * @return The module name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current student enrollment number for this module.
     *
     * @return The enrollment number (pax).
     */
    public int getPax() {
        return pax;
    }

    /**
     * Updates the student enrollment number for this module.
     *
     * @param pax The new enrollment number.
     */
    public void setPax(int pax) {
        this.pax = pax;
    }

    /**
     * Returns a string representation of the module, suitable for UI display.
     *
     * @return A formatted string displaying the module name and enrollment.
     */
    @Override
    public String toString() {
        return name + " | Enrollment: " + pax + " students";
    }
}
