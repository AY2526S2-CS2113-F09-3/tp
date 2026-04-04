package seedu.equipmentmaster.module;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * JUnit tests for the {@code Module} class.
 * Tests enrollment validation, equipment requirement logic, and string formatting.
 */
public class ModuleTest {

    @Test
    public void constructor_validInput_success() throws EquipmentMasterException {
        Module module = new Module("CG2111A", 150);
        assertEquals("CG2111A", module.getName());
        assertEquals(150, module.getPax());
    }

    @Test
    public void constructor_negativePax_throwsException() {
        // Triggers the exception on line 24
        assertThrows(EquipmentMasterException.class, () -> {
            new Module("CG2111A", -1);
        });
    }

    @Test
    public void setPax_validUpdate_success() throws EquipmentMasterException {
        Module module = new Module("CG2111A", 150);
        module.setPax(200);
        assertEquals(200, module.getPax());
    }

    @Test
    public void addEquipmentRequirement_validInput_success() throws EquipmentMasterException {
        Module module = new Module("CG2111A", 150);
        module.addEquipmentRequirement("STM32", 0.5);

        HashMap<String, Double> reqs = module.getEquipmentRequirements();
        assertEquals(1, reqs.size());
        assertEquals(0.5, reqs.get("STM32"));
    }

    @Test
    public void addEquipmentRequirement_nonFiniteRatio_throwsException() throws EquipmentMasterException {
        // Triggers line 67
        Module module = new Module("CG2111A", 150);
        assertThrows(EquipmentMasterException.class, () -> {
            module.addEquipmentRequirement("STM32", Double.POSITIVE_INFINITY);
        });
    }

    @Test
    public void addEquipmentRequirement_nonPositiveRatio_throwsException() throws EquipmentMasterException {
        // Triggers line 71
        Module module = new Module("CG2111A", 150);

        // Test Zero
        assertThrows(EquipmentMasterException.class, () -> {
            module.addEquipmentRequirement("STM32", 0.0);
        });

        // Test Negative
        assertThrows(EquipmentMasterException.class, () -> {
            module.addEquipmentRequirement("STM32", -0.1);
        });
    }

    @Test
    public void removeEquipmentRequirement_existingAndNonExisting_returnsCorrectBoolean()
            throws EquipmentMasterException {
        Module module = new Module("CG2111A", 150);
        module.addEquipmentRequirement("STM32", 0.5);

        // Remove existing
        assertTrue(module.removeEquipmentRequirement("STM32"));

        // Remove non-existing (Line 85 returns false)
        assertFalse(module.removeEquipmentRequirement("STM32"));
    }

    @Test
    public void getEquipmentRequirements_returnsCopy() throws EquipmentMasterException {
        Module module = new Module("CG2111A", 150);
        module.addEquipmentRequirement("STM32", 0.5);

        HashMap<String, Double> reqs = module.getEquipmentRequirements();

        // Verify it is a copy (Line 95)
        assertNotSame(reqs, module.getEquipmentRequirements());

        // Modifying the copy should not affect the internal map
        reqs.put("NewItem", 1.0);
        assertEquals(1, module.getEquipmentRequirements().size());
    }

    @Test
    public void toString_noRequirements_returnsBaseString() throws EquipmentMasterException {
        // Triggers line 109
        Module module = new Module("CG2111A", 150);
        assertEquals("CG2111A | Enrollment: 150 students", module.toString());
    }

    @Test
    public void toString_withRequirements_returnsFormattedString() throws EquipmentMasterException {
        // Triggers the loop and comma logic (Lines 116-126)
        Module module = new Module("CG2111A", 150);
        module.addEquipmentRequirement("STM32", 1.0);
        module.addEquipmentRequirement("HDMI", 0.5);

        String output = module.toString();

        assertTrue(output.contains("CG2111A | Enrollment: 150 students | Required: "));
        assertTrue(output.contains("STM32 (1.0)"));
        assertTrue(output.contains("HDMI (0.5)"));
        // Check for the comma separator
        assertTrue(output.contains(", "));
    }

    @Test
    public void addEquipmentRequirement_exhaustiveBoundaries_coversAllBranches() throws EquipmentMasterException {
        Module module = new Module("CG2111A", 150);

        // 1. Trigger the !Double.isFinite() branch (handles NaN and Infinity)
        assertThrows(EquipmentMasterException.class, () -> module.addEquipmentRequirement("Eq1",
                Double.NaN));
        assertThrows(EquipmentMasterException.class, () -> module.addEquipmentRequirement("Eq1",
                Double.POSITIVE_INFINITY));
        assertThrows(EquipmentMasterException.class, () -> module.addEquipmentRequirement("Eq1",
                Double.NEGATIVE_INFINITY));

        // 2. Trigger the ratio <= 0.0 branch
        assertThrows(EquipmentMasterException.class, () -> module.addEquipmentRequirement("Eq1",
                0.0));
        assertThrows(EquipmentMasterException.class, () -> module.addEquipmentRequirement("Eq1",
                -1.5));

        // 3. Trigger the successful branch where both guard clauses are bypassed
        module.addEquipmentRequirement("Eq1", 1.0);
        assertEquals(1.0, module.getEquipmentRequirements().get("Eq1"));
    }

    @Test
    public void toString_varyingRequirements_coversCommaBranches() throws EquipmentMasterException {
        Module module = new Module("CG2111A", 150);

        // Scenario A: Empty map (triggers the isEmpty() branch on the right side of the ||)
        assertTrue(module.toString().endsWith("150 students"));

        // Scenario B: Exactly 1 requirement (size=1, count=1; '1 < 1' is false, so no comma is appended)
        module.addEquipmentRequirement("ItemA", 1.0);
        String singleOutput = module.toString();
        assertTrue(singleOutput.contains("ItemA (1.0)"));
        assertFalse(singleOutput.contains(",")); // Ensure no comma is present

        // Scenario C: 2 or more requirements (triggers '1 < 2' as true, appending a comma)
        module.addEquipmentRequirement("ItemB", 2.0);
        String multipleOutput = module.toString();
        assertTrue(multipleOutput.contains("ItemA (1.0)"));
        assertTrue(multipleOutput.contains("ItemB (2.0)"));
        assertTrue(multipleOutput.contains(", ")); // Ensure the comma separator is successfully added
    }
}
