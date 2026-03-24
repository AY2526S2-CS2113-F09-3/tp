// @@author Hongyu1231
package seedu.equipmentmaster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
    // @@author

    @Test
    public void constructor_missingFiles_startsEmpty() {
        // Arrange: Define paths for the three required storage files
        String missingEqPath = "data/test_missing_eq.txt";
        String missingSetPath = "data/test_missing_set.txt";
        String missingModPath = "data/test_missing_mod.txt";

        // Helper function (defined below) to clean up before testing
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
}
