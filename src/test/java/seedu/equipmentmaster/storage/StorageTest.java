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
    public void saveAndLoad_validEquipmentList_success() throws EquipmentMasterException {
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);
        ArrayList<Equipment> originalList = new ArrayList<>();

        AcademicSemester sem1 = new AcademicSemester("AY2025/26 Sem1");
        AcademicSemester sem2 = new AcademicSemester("AY2025/26 Sem2");

        // Using your newly created 6-argument constructor!
        originalList.add(new Equipment("STM32 Board", 50, 45, 5, sem1, 5.0));
        originalList.add(new Equipment("HDMI Cable", 100, 100, 0, sem2, 2.5));

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
        // Added the two new lifecycle fields to the end of the string
        String trickyLine = "Special | Adapter | 50 | 45 | 5 | AY2025/26 Sem1 | 3.5";

        try (FileWriter writer = new FileWriter(TEST_FILE_PATH)) {
            writer.write(trickyLine + System.lineSeparator());
        } catch (IOException e) {
            fail("Setup failed");
        }

        ArrayList<Equipment> loaded = storage.load();

        assertEquals(1, loaded.size());

        // Assert the name correctly includes the first "|" and fields parsed properly
        Equipment loadedEquipment = loaded.get(0);
        assertEquals("Special | Adapter", loadedEquipment.getName());
        assertEquals(50, loadedEquipment.getQuantity());
        assertEquals("AY2025/26 Sem1", loadedEquipment.getPurchaseSem().toString());
        assertEquals(3.5, loadedEquipment.getLifespanYears());
    }
}
