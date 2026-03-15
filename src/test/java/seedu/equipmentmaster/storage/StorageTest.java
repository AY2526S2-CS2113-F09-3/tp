package seedu.equipmentmaster.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.io.TempDir;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.ui.Ui;

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

    @TempDir
    Path tempDir; // JUnit creates a temporary directory for file tests

    @Test
    public void saveAndLoadSettings_validSemester_success() throws EquipmentMasterException {
        // Use a temporary file path to avoid messing up real data
        String testDataPath = tempDir.resolve("equipment.txt").toString();
        Storage storage = new Storage(testDataPath, new Ui());

        // We need to manually point settingsPath to temp for testing
        // (Assuming you added a way to set settingsPath or it uses the same data dir)

        AcademicSemester originalSem = new AcademicSemester("AY2025/26 Sem2");
        storage.saveSettings(originalSem);

        String loadedSemStr = storage.loadSettings();
        assertEquals("AY2025/26 Sem2", loadedSemStr);
    }

    @AfterEach
    public void tearDown() {
        // Clean up: Delete the test file after each test to ensure a fresh environment
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void saveAndLoad_validEquipmentList_success() {
        Ui ui = new Ui();
        // Arrange: Create a Storage object and a dummy list of equipment
        Storage storage = new Storage(TEST_FILE_PATH, ui);
        ArrayList<Equipment> originalList = new ArrayList<>();

        // Using your newly created 4-argument constructor!
        originalList.add(new Equipment("STM32 Board", 50, 45, 5));
        originalList.add(new Equipment("HDMI Cable", 100, 100, 0));

        // Act: Save the list to the text file, then immediately load it back into a new list
        storage.save(originalList);
        ArrayList<Equipment> loadedList = storage.load();

        // Assert: Verify that the loaded list has the exact same data as the original list
        assertEquals(2, loadedList.size());

        // Check the attributes of the first equipment
        Equipment firstEquipment = loadedList.get(0);
        assertEquals("STM32 Board", firstEquipment.getName());
        assertEquals(50, firstEquipment.getQuantity());
        assertEquals(45, firstEquipment.getAvailable());
        assertEquals(5, firstEquipment.getLoaned());
    }

    @Test
    public void load_noExistingFile_returnsEmptyList() {
        Ui ui = new Ui();
        // Arrange: Ensure the test file definitely does not exist
        Storage storage = new Storage(TEST_FILE_PATH, ui);
        File file = new File(TEST_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

        // Act: Attempt to load from the non-existent file
        ArrayList<Equipment> loadedList = storage.load();

        // Assert: The returned list should be empty, not null, preventing NullPointerExceptions
        assertTrue(loadedList.isEmpty());
    }

    @Test
    public void parseEquipment_nameWithDelimiters_success() {
        Storage storage = new Storage(TEST_FILE_PATH, new Ui());

        // Simulating a tricky line where the name itself contains " | "
        String trickyLine = "Special | Adapter | 50 | 45 | 5";

        // We need to use reflection or make parseEquipment protected to test it directly,
        // OR just test it through the load() method:
        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write(trickyLine + System.lineSeparator());
        } catch (IOException e) {
            fail("Setup failed");
        }

        ArrayList<Equipment> loaded = storage.load();

        assertEquals(1, loaded.size());
        // The name should correctly include the first "|"
        assertEquals("Special | Adapter", loaded.get(0).getName());
        assertEquals(50, loaded.get(0).getQuantity());
    }
}
