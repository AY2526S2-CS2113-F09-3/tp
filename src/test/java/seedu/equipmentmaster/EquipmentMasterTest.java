package seedu.equipmentmaster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
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

    @Test
    public void constructor_validFile_loadsCorrectSize() {
        // Arrange & Act
        EquipmentMaster app = new EquipmentMaster(TEST_FILE);
        EquipmentList list = app.getEquipmentList();

        // Assert
        assertNotNull(list);
        assertEquals(2, list.getSize(), "The app should have loaded 2 items from the test file.");
        assertEquals("STM32 Board", list.getEquipment(0).getName());
    }

    @Test
    public void constructor_missingFile_startsEmpty() {
        // Arrange
        String missingPath = "data/non_existent.txt";
        File file = new File(missingPath);
        if (file.exists()) {
            file.delete();
        }

        // Act
        EquipmentMaster app = new EquipmentMaster(missingPath);

        // Assert
        assertNotNull(app.getEquipmentList());
        assertEquals(0, app.getEquipmentList().getSize(), "App should start with an empty list if no file is found.");
    }
}
