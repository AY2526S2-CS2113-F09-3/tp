package seedu.equipmentmaster.equipmentlist;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.semester.AcademicSemester;

class EquipmentListTest {

    @Test
    public void addEquipment_exactBatchMatch_mergesQuantities() throws Exception {
        EquipmentList equipmentList = new EquipmentList();
        AcademicSemester sem1 = new AcademicSemester("AY2024/25 Sem1");

        // 1. Add the first batch
        Equipment eq1 = new Equipment("Oscilloscope", 10, 10, 0,
                sem1, 5.0, new ArrayList<>(), 0, 0.0);
        equipmentList.addEquipment(eq1);

        // 2. Add an exact matching batch (same name, same sem, same lifespan)
        Equipment eq2 = new Equipment("Oscilloscope", 5, 5, 0,
                sem1, 5.0, new ArrayList<>(), 0, 0.0);
        equipmentList.addEquipment(eq2);

        // 3. Verify it merged successfully (Size is still 1, Quantity is now 15)
        assertEquals(1, equipmentList.getSize());
        assertEquals(15, equipmentList.findByName("Oscilloscope").getQuantity());
        assertEquals(15, equipmentList.findByName("Oscilloscope").getAvailable());
    }

    @Test
    public void addEquipment_conflictingBatch_throwsException() throws Exception {
        EquipmentList equipmentList = new EquipmentList();
        AcademicSemester sem1 = new AcademicSemester("AY2024/25 Sem1");
        AcademicSemester sem2 = new AcademicSemester("AY2024/25 Sem2"); // Different Sem

        // 1. Add the first batch
        Equipment eq1 = new Equipment("Oscilloscope", 10, 10, 0,
                sem1, 5.0, new ArrayList<>(), 0, 0.0);
        equipmentList.addEquipment(eq1);

        // 2. Create a conflicting batch (same name, DIFFERENT semester)
        Equipment conflictingEq = new Equipment("Oscilloscope", 5, 5, 0,
                sem2, 5.0, new ArrayList<>(), 0, 0.0);

        // 3. Assert that attempting to add it throws the EquipmentMasterException
        assertThrows(EquipmentMasterException.class, () -> {
            equipmentList.addEquipment(conflictingEq);
        });

        // 4. Verify the original item was not corrupted
        assertEquals(1, equipmentList.getSize());
        assertEquals(10, equipmentList.findByName("Oscilloscope").getQuantity());
    }

    @Test
    public void addEquipment_legacyDuplicateNames_findsCorrectBatchToMerge() throws Exception {
        EquipmentList equipmentList = new EquipmentList();
        AcademicSemester sem1 = new AcademicSemester("AY2024/25 Sem1");
        AcademicSemester sem2 = new AcademicSemester("AY2024/25 Sem2");

        // 1. Simulate legacy data where two batches of "Oscilloscope" already exist
        Equipment legacyBatch1 = new Equipment("Oscilloscope", 10, 10, 0,
                sem1, 5.0, new ArrayList<>(), 0, 0.0);
        Equipment legacyBatch2 = new Equipment("Oscilloscope", 20, 20, 0,
                sem2, 5.0, new ArrayList<>(), 0, 0.0);

        equipmentList.addEquipment(legacyBatch1);
        // Temporarily bypass the addEquipment validation to insert the legacy duplicate
        equipmentList.getAllEquipments().add(legacyBatch2);

        // 2. Add a new batch that matches the SECOND legacy batch
        Equipment newStock = new Equipment("Oscilloscope", 5, 5, 0,
                sem2, 5.0, new ArrayList<>(), 0, 0.0);

        equipmentList.addEquipment(newStock);

        // 3. Verify it skipped batch1 and successfully merged into batch2
        assertEquals(10, equipmentList.getEquipment(0).getQuantity()); // Batch 1 unchanged
        assertEquals(25, equipmentList.getEquipment(1).getQuantity()); // Batch 2 merged (20 + 5)
    }

    @Test
    public void addEquipment_exactBatchMatch_mergesModuleCodes() throws Exception {
        EquipmentList equipmentList = new EquipmentList();
        AcademicSemester sem = new AcademicSemester("AY2024/25 Sem1");

        // 1. Add item tagged to CG2111A
        ArrayList<String> mods1 = new ArrayList<>();
        mods1.add("CG2111A");
        Equipment eq1 = new Equipment("STM32", 10, 10, 0, sem, 5.0, mods1, 0, 0.0);
        equipmentList.addEquipment(eq1);

        // 2. Add identical item but tagged to EE2026
        ArrayList<String> mods2 = new ArrayList<>();
        mods2.add("EE2026");
        Equipment eq2 = new Equipment("STM32", 5, 5, 0, sem, 5.0, mods2, 0, 0.0);
        equipmentList.addEquipment(eq2);

        // 3. Verify the merged item has BOTH module codes
        Equipment mergedItem = equipmentList.findByName("STM32");
        assertEquals(1, equipmentList.getSize());
        assertTrue(mergedItem.getModuleCodes().contains("CG2111A"));
        assertTrue(mergedItem.getModuleCodes().contains("EE2026"));
    }

    @Test
    public void addEquipment_nullBatchDetails_mergesSuccessfully() throws Exception {
        EquipmentList equipmentList = new EquipmentList();

        // 1. Add basic item (purchaseSem is null, lifespan is 0.0)
        Equipment eq1 = new Equipment("Resistor", 100, 100, 0, null, 0.0, new ArrayList<>(), 0, 0.0);
        equipmentList.addEquipment(eq1);

        // 2. Add another basic item
        Equipment eq2 = new Equipment("Resistor", 50, 50, 0, null, 0.0, new ArrayList<>(), 0, 0.0);
        equipmentList.addEquipment(eq2);

        // 3. Verify Objects.equals(null, null) worked and merged them
        assertEquals(1, equipmentList.getSize());
        assertEquals(150, equipmentList.findByName("Resistor").getQuantity());
    }

    @Test
    public void findByName_and_hasEquipment_caseInsensitive_success() throws Exception {
        EquipmentList equipmentList = new EquipmentList();
        Equipment eq1 = new Equipment("Raspberry Pi", 10, 10, 0, null, 0.0, new ArrayList<>(), 0, 0.0);
        equipmentList.addEquipment(eq1);

        // Verify hasEquipment works regardless of case
        assertTrue(equipmentList.hasEquipment("raspberry pi"));
        assertTrue(equipmentList.hasEquipment("RASPBERRY PI"));
        assertFalse(equipmentList.hasEquipment("Arduino"));

        // Verify findByName returns the correct object
        Equipment foundItem = equipmentList.findByName("rAsPbErRy pI");
        assertEquals("Raspberry Pi", foundItem.getName());

        // Verify findByName returns null for missing items
        assertNull(equipmentList.findByName("NonExistentItem"));
    }

    @Test
    public void removeEquipment_updatesSizeAndList() throws Exception {
        EquipmentList equipmentList = new EquipmentList();
        Equipment eq1 = new Equipment("Oscilloscope", 10, 10, 0, null, 0.0, new ArrayList<>(), 0, 0.0);
        Equipment eq2 = new Equipment("Multimeter", 5, 5, 0, null, 0.0, new ArrayList<>(), 0, 0.0);

        equipmentList.addEquipment(eq1);
        equipmentList.addEquipment(eq2);
        assertEquals(2, equipmentList.getSize());

        // Test remove by object
        equipmentList.removeEquipment(eq1);
        assertEquals(1, equipmentList.getSize());
        assertFalse(equipmentList.hasEquipment("Oscilloscope"));

        // Test remove by index
        equipmentList.removeEquipment(0);
        assertEquals(0, equipmentList.getSize());
        assertTrue(equipmentList.isEmpty());
    }
}