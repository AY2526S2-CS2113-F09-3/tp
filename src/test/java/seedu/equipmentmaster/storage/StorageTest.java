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

    // Define a specific file path just for testing purposes
    private static final String TEST_FILE_PATH = "test_equipment.txt";
    private static final Ui ui = new Ui();

    @TempDir
    Path tempDir; // JUnit creates a temporary directory for file tests

    private Storage createStorage() {
        return new Storage(tempDir.resolve("test.txt").toString(), new Ui());
    }

    @Test
    public void saveAndLoadSettings_validSemester_success() throws EquipmentMasterException {
        // Use a temporary file path to avoid messing up real data
        Storage storage = createStorage();

        // We need to manually point settingsPath to temp for testing
        // (Assuming you added a way to set settingsPath or it uses the same data dir)

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

        // Using your newly created 6-argument constructor!
        originalList.add(new Equipment("STM32 Board", 50, 45, 5, sem1, 5.0,
                0));
        originalList.add(new Equipment("HDMI Cable", 100, 100, 0, sem2, 2.5,
                0));

        // Act: Save the list to the text file, then immediately load it back
        storage.save(originalList);
        ArrayList<Equipment> loadedList = storage.load();

        // Assert: Verify that the loaded list has the exact same data
        assertEquals(2, loadedList.size());

        // Check the attributes of the first equipment, including the new lifecycle fields
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
        // Arrange: Ensure the test file definitely does not exist
        Storage storage = new Storage(tempDir.resolve("nonexistent.txt").toString(), new Ui());

        // Act: Attempt to load from the non-existent file
        ArrayList<Equipment> loadedList = storage.load();

        // Assert: The returned list should be empty, not null, preventing NullPointerExceptions
        assertTrue(loadedList.isEmpty());
    }

    @Test
    public void parseEquipment_nameWithDelimiters_success() {
        Path testFile = tempDir.resolve("test.txt");
        Storage storage = new Storage(testFile.toString(), new Ui());

        String trickyLine = "Special | Adapter | 50 | 45 | 5 | 0 | AY2025/26 Sem1 | 3.5 | ";

        try (FileWriter writer = new FileWriter(testFile.toFile())) {
            writer.write(trickyLine + System.lineSeparator());
        } catch (IOException e) {
            fail("Setup failed: " + e.getMessage());
        }

        ArrayList<Equipment> loaded = storage.load();

        assertEquals(1, loaded.size());

        // Assert the name correctly includes the first "|" and fields parsed properly
        Equipment loadedEquipment = loaded.get(0);
        assertEquals("Special | Adapter", loadedEquipment.getName());
        assertEquals(50, loadedEquipment.getQuantity());
        assertEquals("AY2025/26 Sem1", loadedEquipment.getPurchaseSem().toString());
        assertEquals(3.5, loadedEquipment.getLifespanYears());
        assertTrue(loadedEquipment.getModuleCodes().isEmpty());
    }

    @Test
    public void loadModules_fileDoesNotExist_returnsEmptyList() {
        // Path points to a non-existent file in the temporary directory
        File tempFile = tempDir.resolve("missing_modules.txt").toFile();
        Storage storage = new Storage("dummy_equipment.txt", ui);

        // Since it handles the error internally and prints, it should return an empty list
        ModuleList loadedList = storage.loadModules();

        assertTrue(loadedList.getModules().isEmpty(), "Loaded list should be empty when file is missing.");
    }

    @Test
    public void loadModules_corruptedData_returnsEmptyList() throws IOException {
        // Create a temporary file and write corrupted data into it
        File tempFile = tempDir.resolve("corrupted_modules.txt").toFile();
        FileWriter fw = new FileWriter(tempFile);
        fw.write("CG2111A | oneHundredAndFifty\n"); // "oneHundredAndFifty" is not an integer
        fw.close();

        Storage storage = new Storage("dummy_equipment.txt", ui);

        // Because we catch NumberFormatException and print it, it should return an empty list instead of throwing
        ModuleList loadedList = storage.loadModules();

        assertTrue(loadedList.getModules().isEmpty(), "Loaded list should be empty when data is corrupted.");
    }

    @Test
    public void saveAndLoadModules_validData_success() throws EquipmentMasterException {
        File tempFile = tempDir.resolve("valid_modules.txt").toFile();
        Storage storage = new Storage("dummy_equipment.txt", ui);

        // 1. Create a ModuleList and add a module
        ModuleList originalList = new ModuleList();

        originalList.addModule(new Module("CG2028", 120));

        // 2. Save to the temporary file
        storage.saveModules(originalList);

        // 3. Load it back into a new ModuleList
        ModuleList loadedList = storage.loadModules();

        // 4. Verify the data remains intact
        assertEquals(1, loadedList.getModules().size());
        assertEquals("CG2028", loadedList.getModules().get(0).getName());
        assertEquals(120, loadedList.getModules().get(0).getPax());
    }
}
