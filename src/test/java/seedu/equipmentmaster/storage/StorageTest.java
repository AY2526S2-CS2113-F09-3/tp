// @@author Hongyu1231
package seedu.equipmentmaster.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.module.Module;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


public class StorageTest {

    private static final Ui ui = new Ui();

    @TempDir
    Path tempDir; // JUnit creates a temporary directory for file tests

    /**
     * Helper method to create a Storage instance with isolated temporary files for all paths.
     */
    private Storage createStorage() {
        return new Storage(
                tempDir.resolve("test_eq.txt").toString(),
                ui,
                tempDir.resolve("test_set.txt").toString(),
                tempDir.resolve("test_mod.txt").toString()
        );
    }

    @Test
    public void saveAndLoadSettings_validSemester_success() throws EquipmentMasterException {
        Storage storage = createStorage();

        AcademicSemester originalSem = new AcademicSemester("AY2025/26 Sem2");
        storage.saveSettings(originalSem);

        String loadedSemStr = storage.loadSettings();
        assertEquals("AY2025/26 Sem2", loadedSemStr);
    }

    @Test
    public void saveAndLoad_validEquipmentList_success() throws EquipmentMasterException {
        Storage storage = createStorage();
        ArrayList<Equipment> originalList = new ArrayList<>();

        AcademicSemester sem1 = new AcademicSemester("AY2025/26 Sem1");
        AcademicSemester sem2 = new AcademicSemester("AY2025/26 Sem2");

        originalList.add(new Equipment("STM32 Board", 50, 45, 5, sem1, 5.0, 0));
        originalList.add(new Equipment("HDMI Cable", 100, 100, 0, sem2, 2.5, 0));

        // Act: Save the list to the text file, then immediately load it back
        storage.save(originalList);
        ArrayList<Equipment> loadedList = storage.load();

        // Assert: Verify that the loaded list has the exact same data
        assertEquals(2, loadedList.size());

        Equipment firstEquipment = loadedList.get(0);
        assertEquals("STM32 Board", firstEquipment.getName());
        assertEquals(50, firstEquipment.getQuantity());
        assertEquals(45, firstEquipment.getAvailable());
        assertEquals(5, firstEquipment.getLoaned());
        assertEquals(sem1.toString(), firstEquipment.getPurchaseSem().toString());
        assertEquals(5.0, firstEquipment.getLifespanYears());
    }

    @Test
    public void load_noExistingFile_returnsEmptyList() {
        // Arrange: Pass the nonexistent file as the first argument (equipment path)
        Storage storage = new Storage(
                tempDir.resolve("nonexistent.txt").toString(),
                ui,
                "dummy_set.txt",
                "dummy_mod.txt"
        );

        // Act: Attempt to load from the non-existent file
        ArrayList<Equipment> loadedList = storage.load();

        // Assert: The returned list should be empty, not null
        assertTrue(loadedList.isEmpty());
    }

    @Test
    public void parseEquipment_nameWithDelimiters_success() {
        Path testFile = tempDir.resolve("test.txt");
        Storage storage = new Storage(
                testFile.toString(),
                ui,
                "dummy_set.txt",
                "dummy_mod.txt"
        );

        String trickyLine = "Special | Adapter | 50 | 45 | 5 | 0 | AY2025/26 Sem1 | 3.5 | ";

        try (FileWriter writer = new FileWriter(testFile.toFile())) {
            writer.write(trickyLine + System.lineSeparator());
        } catch (IOException e) {
            fail("Setup failed: " + e.getMessage());
        }

        ArrayList<Equipment> loaded = storage.load();

        assertEquals(1, loaded.size());

        Equipment loadedEquipment = loaded.get(0);
        assertEquals("Special | Adapter", loadedEquipment.getName());
        assertEquals(50, loadedEquipment.getQuantity());
        assertEquals("AY2025/26 Sem1", loadedEquipment.getPurchaseSem().toString());
        assertEquals(3.5, loadedEquipment.getLifespanYears());
        assertTrue(loadedEquipment.getModuleCodes().isEmpty());
    }

    @Test
    public void loadModules_fileDoesNotExist_returnsEmptyList() {
        File tempFile = tempDir.resolve("missing_modules.txt").toFile();

        // Pass tempFile.getAbsolutePath() as the 4th argument so it reads the temp file
        Storage storage = new Storage("dummy_eq.txt", ui, "dummy_set.txt", tempFile.getAbsolutePath());

        ModuleList loadedList = storage.loadModules();

        assertTrue(loadedList.getModules().isEmpty(), "Loaded list should be empty when file is missing.");
    }

    @Test
    public void loadModules_corruptedData_returnsEmptyList() throws IOException {
        File tempFile = tempDir.resolve("corrupted_modules.txt").toFile();
        FileWriter fw = new FileWriter(tempFile);
        fw.write("CG2111A | oneHundredAndFifty\n");
        fw.close();

        // Pass tempFile.getAbsolutePath() as the 4th argument
        Storage storage = new Storage("dummy_eq.txt", ui, "dummy_set.txt", tempFile.getAbsolutePath());

        ModuleList loadedList = storage.loadModules();

        assertTrue(loadedList.getModules().isEmpty(), "Loaded list should be empty when data is corrupted.");
    }

    @Test
    public void saveAndLoadModules_validData_success() throws EquipmentMasterException {
        File tempFile = tempDir.resolve("valid_modules.txt").toFile();

        // Pass tempFile.getAbsolutePath() as the 4th argument
        Storage storage = new Storage("dummy_eq.txt", ui, "dummy_set.txt", tempFile.getAbsolutePath());

        ModuleList originalList = new ModuleList();
        originalList.addModule(new Module("CG2028", 120));

        storage.saveModules(originalList);
        ModuleList loadedList = storage.loadModules();

        assertEquals(1, loadedList.getModules().size());
        assertEquals("CG2028", loadedList.getModules().get(0).getName());
        assertEquals(120, loadedList.getModules().get(0).getPax());
    }
}
