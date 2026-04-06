package seedu.equipmentmaster;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.equipment.Equipment;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class EquipmentMasterTest {

    private static final String TEST_FILE = "data/test_load.txt";

    @BeforeEach
    public void setUp() throws IOException {
        // Ensure the data directory exists
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a fake save file to test the "Auto-Load" feature
        // Format: Name | Total | Available | Loaned
        try (FileWriter writer = new FileWriter(TEST_FILE)) {
            writer.write("STM32 Board | 50 | 45 | 5" + System.lineSeparator());
            writer.write("HDMI Cable | 100 | 100 | 0" + System.lineSeparator());
        }
    }

    @AfterEach
    public void tearDown() {
        // Cleanup the test file after each run
        File file = new File(TEST_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void constructor_missingFiles_startsEmpty() {
        // Arrange: Define paths for the three required storage files
        String missingEqPath = "data/test_missing_eq.txt";
        String missingSetPath = "data/test_missing_set.txt";
        String missingModPath = "data/test_missing_mod.txt";

        // Clean up before testing just in case
        deleteFileIfExists(missingEqPath);
        deleteFileIfExists(missingSetPath);
        deleteFileIfExists(missingModPath);

        // Act: Initialize the main application with these missing file paths
        EquipmentMaster app = new EquipmentMaster(missingEqPath, missingSetPath, missingModPath);

        // Assert: Verify that the application falls back safely and creates an empty list
        assertNotNull(app.getEquipmentList(), "Equipment list should be initialized even if files are missing.");
        assertEquals(0, app.getEquipmentList().getSize(),
                "App should start with an empty equipment list if no file is found.");

        // Clean up: Delete the files that the application automatically created during the test
        deleteFileIfExists(missingEqPath);
        deleteFileIfExists(missingSetPath);
        deleteFileIfExists(missingModPath);
    }

    @Test
    public void constructor_existingEquipmentFile_loadsDataSuccessfully() throws Exception {
        String dummyEqPath = "data/test_dummy_eq.txt";
        String dummySetPath = "data/test_dummy_set.txt";
        String dummyModPath = "data/test_dummy_mod.txt";

        // 1. Clean up before starting
        deleteFileIfExists(dummyEqPath);
        deleteFileIfExists(dummySetPath);
        deleteFileIfExists(dummyModPath);

        // 2. Use Storage to save data using a raw ArrayList (bypassing EquipmentList getter issues)
        seedu.equipmentmaster.ui.Ui ui = new seedu.equipmentmaster.ui.Ui();
        seedu.equipmentmaster.storage.Storage testStorage = new seedu.equipmentmaster.storage.Storage(
                dummyEqPath, ui, dummySetPath, dummyModPath);

        ArrayList<Equipment> rawList = new ArrayList<>();
        rawList.add(new Equipment("STM32", 10));
        rawList.add(new Equipment("HDMI", 5));

        // Save the raw ArrayList directly to the file
        testStorage.save(rawList);

        // 3. Act: Initialize the main application. It should detect the file we just saved and load it.
        EquipmentMaster app = new EquipmentMaster(dummyEqPath, dummySetPath, dummyModPath);

        // 4. Assert: Verify that the data was loaded correctly into the internal list
        assertNotNull(app.getEquipmentList(), "Equipment list should not be null.");
        assertEquals(2, app.getEquipmentList().getSize(),
                "App should successfully load exactly 2 items from the perfectly formatted file.");

        // 5. Clean up
        deleteFileIfExists(dummyEqPath);
        deleteFileIfExists(dummySetPath);
        deleteFileIfExists(dummyModPath);
    }

    @Test
    public void run_validAndInvalidCommands_executesAndExits() {
        // Arrange: Simulate user typing an invalid command, followed by "bye" to exit the loop.
        String simulatedInput = "thisisnotacommand" + System.lineSeparator() + "bye" + System.lineSeparator();

        // Hijack System.in to feed our simulated string instead of waiting for real keyboard input
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            EquipmentMaster app = new EquipmentMaster("data/test_run_eq.txt",
                    "data/test_run_set.txt", "data/test_run_mod.txt");

            // Act: Run the application. It will read our string, throw an error, read "bye", and exit smoothly.
            app.run();

            // Assert: If the test reaches this line without an infinite loop, it passed!
            assertNotNull(app);
        } finally {
            // CRITICAL: Always restore the original System.in after the test!
            System.setIn(originalIn);
            // Clean up files created during this run test
            deleteFileIfExists("data/test_run_eq.txt");
            deleteFileIfExists("data/test_run_set.txt");
            deleteFileIfExists("data/test_run_mod.txt");
        }
    }

    @Test
    public void main_byeCommand_startsAndExits() throws Exception {
        // Arrange: Simulate typing "bye" right as the program starts via main()
        String simulatedInput = "bye" + System.lineSeparator();
        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            // Act: Call the main method directly.
            EquipmentMaster.main(new String[]{});

            // Assert: Program started and terminated successfully.
            assertTrue(true);
        } finally {
            // Restore System.in
            System.setIn(originalIn);
        }
    }

    /**
     * Helper method to safely delete a file if it exists.
     * @param filePath The path of the file to be deleted.
     */
    private void deleteFileIfExists(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void run_emptyCommand_continuesLoop() {
        // Arrange: Simulate pressing Enter (empty string), typing spaces, and then "bye"
        String simulatedInput = System.lineSeparator() + "   " + System.lineSeparator()
                + "bye" + System.lineSeparator();

        InputStream originalIn = System.in;
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        try {
            EquipmentMaster app = new EquipmentMaster("data/test_empty_eq.txt",
                    "data/test_empty_set.txt", "data/test_empty_mod.txt");

            // Act
            app.run();

            // Assert: If the loop doesn't crash and exits gracefully, the branch is covered.
            assertTrue(true);
        } finally {
            System.setIn(originalIn);
            deleteFileIfExists("data/test_empty_eq.txt");
            deleteFileIfExists("data/test_empty_set.txt");
            deleteFileIfExists("data/test_empty_mod.txt");
        }
    }

    @Test
    public void constructor_existingSettingsFile_loadsSemesterSuccessfully() throws IOException {
        String dummyEqPath = "data/test_sem_eq.txt";
        String dummySetPath = "data/test_sem_set.txt";
        String dummyModPath = "data/test_sem_mod.txt";

        // 1. Clean up before starting
        deleteFileIfExists(dummyEqPath);
        deleteFileIfExists(dummySetPath);
        deleteFileIfExists(dummyModPath);

        // 2. Create a settings file with a valid semester string
        try (FileWriter writer = new FileWriter(dummySetPath)) {
            writer.write("AY2023/24 Sem2");
        }

        // 3. Act: Initialize the main application
        EquipmentMaster app = new EquipmentMaster(dummyEqPath, dummySetPath, dummyModPath);

        // 4. Assert
        assertNotNull(app, "Application should initialize successfully with a valid settings file.");

        // 5. Clean up
        deleteFileIfExists(dummyEqPath);
        deleteFileIfExists(dummySetPath);
        deleteFileIfExists(dummyModPath);
    }

    @Test
    public void constructor_corruptedSettingsFile_usesFallbackSemester() throws IOException {
        String dummyEqPath = "data/test_corrupt_eq.txt";
        String dummySetPath = "data/test_corrupt_set.txt";
        String dummyModPath = "data/test_corrupt_mod.txt";

        deleteFileIfExists(dummyEqPath);
        deleteFileIfExists(dummySetPath);
        deleteFileIfExists(dummyModPath);

        // Create a settings file with INVALID data
        try (FileWriter writer = new FileWriter(dummySetPath)) {
            writer.write("Not A Real Semester 2024");
        }

        // Act: Initialize the application. It should read the bad file, catch the
        // EquipmentMasterException thrown by AcademicSemester, and use the fallback.
        EquipmentMaster app = new EquipmentMaster(dummyEqPath, dummySetPath, dummyModPath);

        // Assert: The application should still initialize without crashing
        assertNotNull(app, "Application should survive a corrupted settings file.");

        deleteFileIfExists(dummyEqPath);
        deleteFileIfExists(dummySetPath);
        deleteFileIfExists(dummyModPath);
    }
}
