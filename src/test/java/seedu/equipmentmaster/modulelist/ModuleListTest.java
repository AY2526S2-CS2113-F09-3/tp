package seedu.equipmentmaster.modulelist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.module.Module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit tests for the {@code ModuleList} class.
 * Tests adding, updating, deleting, and retrieving modules, along with exception handling.
 */
public class ModuleListTest {

    private ModuleList moduleList;

    @BeforeEach
    public void setUp() {
        moduleList = new ModuleList();
    }

    @Test
    public void constructor_initializesEmptyList() {
        assertTrue(moduleList.getModules().isEmpty());
    }

    @Test
    public void addModule_validModule_success() throws EquipmentMasterException {
        Module module = new Module("CG2111A", 150);
        moduleList.addModule(module);

        assertEquals(1, moduleList.getModules().size());
        assertEquals(module, moduleList.getModules().get(0));
    }

    @Test
    public void addModule_nullModule_ignores() {
        // Covers the `if (module == null)` branch
        moduleList.addModule(null);
        assertTrue(moduleList.getModules().isEmpty());
    }

    @Test
    public void addModule_duplicateName_ignores() throws EquipmentMasterException {
        // Covers the `if (findModule(...) != null)` branch
        Module firstModule = new Module("CG2111A", 150);
        Module duplicateModule = new Module("cg2111a", 200); // Lowercase to test case-insensitivity

        moduleList.addModule(firstModule);
        moduleList.addModule(duplicateModule); // Should be ignored

        assertEquals(1, moduleList.getModules().size());
        assertEquals(150, moduleList.getModule("CG2111A").getPax()); // Keeps the original pax
    }

    @Test
    public void updateModule_existingModule_success() throws EquipmentMasterException {
        moduleList.addModule(new Module("CG2111A", 150));

        // Use lowercase to ensure `findModule` helper works correctly
        moduleList.updateModule("cg2111a", 180);

        assertEquals(180, moduleList.getModule("CG2111A").getPax());
    }

    @Test
    public void updateModule_nonExistingModule_throwsException() {
        // Covers the exception throw when module is not found
        assertThrows(EquipmentMasterException.class, () -> {
            moduleList.updateModule("UNKNOWN_MOD", 100);
        });
    }

    @Test
    public void deleteModule_existingModule_success() throws EquipmentMasterException {
        moduleList.addModule(new Module("CG2111A", 150));

        // Use lowercase to test case-insensitivity during deletion
        moduleList.deleteModule("cg2111a");

        assertTrue(moduleList.getModules().isEmpty());
    }

    @Test
    public void deleteModule_nonExistingModule_throwsException() {
        // Covers the exception throw when module to delete is not found
        assertThrows(EquipmentMasterException.class, () -> {
            moduleList.deleteModule("UNKNOWN_MOD");
        });
    }

    @Test
    public void hasModule_existingAndNonExisting_returnsCorrectBoolean() throws EquipmentMasterException {
        moduleList.addModule(new Module("CG2111A", 150));

        assertTrue(moduleList.hasModule("CG2111A"));
        assertTrue(moduleList.hasModule("cg2111a")); // Case-insensitive check
        assertFalse(moduleList.hasModule("EE2026")); // Non-existent module
    }

    @Test
    public void getModule_existingAndNonExisting_returnsModuleOrNull() throws EquipmentMasterException {
        Module module = new Module("CG2111A", 150);
        moduleList.addModule(module);

        assertEquals(module, moduleList.getModule("CG2111A"));
        assertNull(moduleList.getModule("EE2026")); // Covers the `return null` in findModule
    }
}
