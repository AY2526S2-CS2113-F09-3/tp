package seedu.equipmentmaster.module;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import seedu.equipmentmaster.exception.EquipmentMasterException;

/**
 * Represents a course module in the equipment management system.
 * Tracks the module's name and its associated student enrollment (pax).
 */
public class Module {
    private String name;
    private int pax;
    private final HashMap<String, Double> equipmentRequirements;

    /**
     * Constructs a {@code Module} with the specified name and enrollment number.
     *
     * @param name The name of the module (e.g., "CG2111A").
     * @param pax  The number of students enrolled. Must be 0 or a positive integer.
     * @throws EquipmentMasterException If the provided pax is negative.
     */
    public Module(String name, int pax) throws EquipmentMasterException {
        // ASSERTION: Defensive programming for data integrity
        assert name != null && !name.trim().isEmpty() : "Module name cannot be null or empty";

        if (pax < 0) {
            throw new EquipmentMasterException("Pax (enrollment number) cannot be negative.");
        }
        this.name = name;
        this.pax = pax;
        this.equipmentRequirements = new HashMap<>();
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
        assert pax >= 0 : "Pax cannot be negative";
        this.pax = pax;
    }

    /**
     * Adds or updates the requirement ratio for a specific equipment (Upsert logic).
     *
     * @param equipmentName The name of the equipment (e.g., "STM32").
     * @param ratio         The fractional requirement per student (e.g., 0.2).
     * @throws EquipmentMasterException If the ratio is non-finite, zero or negative.
     */
    public void addEquipmentRequirement(String equipmentName, double ratio) throws EquipmentMasterException {
        // Edge Case Handling: Non-finite Requirement
        if (!Double.isFinite(ratio)) {
            throw new EquipmentMasterException("Requirement ratio must be a finite number.");
        }
        // Edge Case Handling: Negative/Zero Requirement
        if (ratio <= 0.0) {
            throw new EquipmentMasterException("Requirement ratio must be strictly greater than 0.0.");
        }
        this.equipmentRequirements.put(equipmentName, ratio);
    }

    /**
     * Removes the specified equipment from the module's requirements (Untag logic).
     *
     * @param equipmentName The name of the equipment to untag.
     * @return true if the equipment was successfully removed, false if it wasn't found.
     */
    public boolean removeEquipmentRequirement(String equipmentName) {
        // .remove() returns the previous value associated with the key, or null if there was no mapping.
        return this.equipmentRequirements.remove(equipmentName) != null;
    }

    /**
     * Retrieves the map of equipment requirements.
     * Useful for calculating total demand later.
     *
     * @return A copy of the HashMap of equipment names and their requirement ratios.
     */
    public HashMap<String, Double> getEquipmentRequirements() {
        return new HashMap<>(this.equipmentRequirements);
    }

    /**
     * Returns a string representation of the module, suitable for UI display.
     *
     * @return A formatted string displaying the module name and enrollment.
     */
    @Override
    public String toString() {
        String baseString = name + " | Enrollment: " + pax + " students";

        if (this.equipmentRequirements.isEmpty()) {
            return baseString;
        }

        return baseString + formatRequirements();
    }

    /**
     * Helper method to format the equipment requirements into an alphabetically sorted string.
     */
    private String formatRequirements() {
        // TreeMap automatically sorts keys alphabetically
        TreeMap<String, Double> sortedReqs = new TreeMap<>(this.equipmentRequirements);
        StringBuilder tagsBuilder = new StringBuilder(" | Required: ");

        int count = 0;
        for (Map.Entry<String, Double> entry : sortedReqs.entrySet()) {
            tagsBuilder.append(entry.getKey()).append(" (").append(entry.getValue()).append(")");

            count++;
            if (count < sortedReqs.size()) {
                tagsBuilder.append(", ");
            }
        }
        return tagsBuilder.toString();
    }
}
