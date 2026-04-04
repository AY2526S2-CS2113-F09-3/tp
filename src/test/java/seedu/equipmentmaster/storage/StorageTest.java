//@@author Hongyu1231
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
import java.util.Scanner;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;


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

    @Test
    public void loadSettings_missingOrEmptyFile_returnsDefault() throws IOException {
        // Covers the branch returning the default semester when the file is missing or empty
        Storage storage = createStorage(); // test_set.txt has not been created yet
        assertEquals("AY2024/25 Sem1", storage.loadSettings(), "Should return default if file is missing");

        // Manually create an empty setting file
        File emptyFile = tempDir.resolve("empty_set.txt").toFile();
        emptyFile.createNewFile();
        Storage storageEmpty = new Storage("eq", ui, emptyFile.getAbsolutePath(), "mod");
        assertEquals("AY2024/25 Sem1", storageEmpty.loadSettings(), "Should return default if file is empty");
    }

    @Test
    public void parseEquipment_fullFieldsWithBuffer_success() throws IOException {
        // Covers branches for parsing lines with buffer and modules,
        // as well as lines with completely empty fields (hasBuffer true/false logic)
        File eqFile = tempDir.resolve("eq_buffer.txt").toFile();
        try (FileWriter fw = new FileWriter(eqFile)) {
            // Complete data line (includes buffer 15.0 and tags)
            fw.write("Oscilloscope | 10 | 8 | 2 | 1 | AY2024/25 Sem1 | 10.0 | EE2026,CG2111A | 15.0\n");
            // Data line missing some optional fields (tests empty string fallback logic)
            fw.write("Multimeter | 5 | 5 | 0 | 0 | AY2024/25 Sem1 | 0.0 | \n");
        }
        Storage storage = new Storage(eqFile.getAbsolutePath(), ui, "set", "mod");
        ArrayList<Equipment> loaded = storage.load();

        assertEquals(2, loaded.size());

        // Verify first item: with buffer and tags
        assertEquals("Oscilloscope", loaded.get(0).getName());
        assertEquals(15.0, loaded.get(0).getBufferPercentage());
        assertEquals(2, loaded.get(0).getModuleCodes().size());

        // Verify second item: no buffer
        assertEquals("Multimeter", loaded.get(1).getName());
        assertEquals(0.0, loaded.get(1).getBufferPercentage());
    }

    @Test
    public void parseEquipment_corruptedLines_skipped() throws IOException {
        // Covers isBlank() empty lines, and the catch(Exception e) branch when parsing fails completely
        File eqFile = tempDir.resolve("eq_corrupted.txt").toFile();
        try (FileWriter fw = new FileWriter(eqFile)) {
            fw.write("   \n"); // Empty line with only spaces
            fw.write("Totally invalid line without pipes\n"); // Corrupted line that cannot be split properly
        }
        Storage storage = new Storage(eqFile.getAbsolutePath(), ui, "set", "mod");
        ArrayList<Equipment> loaded = storage.load();

        // Failed parsing lines are safely caught and skipped, returning an empty list
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void saveAndLoadModules_withRequirements_success() throws Exception {
        // Covers saving and loading Modules with specific equipmentRequirements (tags)
        File modFile = tempDir.resolve("mod_req.txt").toFile();
        Storage storage = new Storage("eq", ui, "set", modFile.getAbsolutePath());

        ModuleList originalList = new ModuleList();
        Module mod = new Module("CS2113", 100);
        mod.addEquipmentRequirement("Laptop", 1.0);
        mod.addEquipmentRequirement("Mouse", 0.5);
        originalList.addModule(mod);

        storage.saveModules(originalList); // Tests the save branch with tags

        ModuleList loadedList = storage.loadModules(); // Tests the load branch with tags
        assertEquals(1, loadedList.getModules().size());
        Module loadedMod = loadedList.getModule("CS2113");
        assertEquals(1.0, loadedMod.getEquipmentRequirements().get("Laptop"));
        assertEquals(0.5, loadedMod.getEquipmentRequirements().get("Mouse"));
    }

    @Test
    public void loadModules_corruptedTagsAndLines_handlesGracefully() throws IOException {
        // Covers internal error handling during Module loading:
        // empty lines, lines with < 2 parts, negative ratios, and non-numeric ratios
        File modFile = tempDir.resolve("mod_corrupted_tags.txt").toFile();
        try (FileWriter fw = new FileWriter(modFile)) {
            fw.write("\n"); // Empty line
            fw.write("MissingPax\n"); // Line with less than two parts (skipped)
            fw.write("Mod1 | 10 | Eq1=-1.0,Eq2=abc,Eq3\n"); // Ratios are negative, non-numeric, or malformed
        }
        Storage storage = new Storage("eq", ui, "set", modFile.getAbsolutePath());
        ModuleList loaded = storage.loadModules();

        assertEquals(1, loaded.getModules().size());
        Module mod = loaded.getModule("Mod1");

        // Since all tags are invalid, they are caught and skipped, resulting in an empty requirements map
        assertTrue(mod.getEquipmentRequirements().isEmpty());
    }

    @Test
    public void saveMethods_ioException_caught() {
        Ui dummyUi = new Ui();
        String badPath = tempDir.resolve("bad_dir").toString();
        File badDir = new File(badPath);
        badDir.mkdir();

        Storage badStorage = new Storage(badPath, dummyUi, "dummy_set", "dummy_mod");

        EquipmentMasterException thrown = assertThrows(
                EquipmentMasterException.class,
                () -> badStorage.save(new java.util.ArrayList<>())
        );

        assertTrue(
                thrown.getMessage().contains("Error saving equipment data")
        );
    }

    @Test
    public void storage_nullParentDirectory_handledSafely() throws Exception {
        // Covers the `directory != null` (or `getParentFile() != null`) yellow/red lines.
        // By using flat filenames without any slashes, the parent directory evaluates to null.
        String flatEq = "flat_eq.txt";
        String flatSet = "flat_set.txt";
        String flatMod = "flat_mod.txt";

        File eqFile = new File(flatEq);
        File setFile = new File(flatSet);
        File modFile = new File(flatMod);

        // Clean up before test just in case
        eqFile.delete();
        setFile.delete();
        modFile.delete();

        Storage flatStorage = new Storage(flatEq, ui, flatSet, flatMod);

        try {
            // 1. Triggers directory != null check in save()
            flatStorage.save(new ArrayList<>());

            // 2. Triggers directory != null check in saveSettings()
            flatStorage.saveSettings(new AcademicSemester("AY2024/25 Sem1"));

            // 3. Triggers parentDirectory != null check in loadModules()
            // (since the file doesn't exist, it will try to create it)
            flatStorage.loadModules();

            // 4. Triggers parentDirectory != null check in saveModules()
            flatStorage.saveModules(new ModuleList());

            // Assert that the files were successfully created in the root directory
            assertTrue(eqFile.exists());
            assertTrue(setFile.exists());
            assertTrue(modFile.exists());
        } finally {
            // CRITICAL: Clean up root files so we don't pollute the project workspace
            eqFile.delete();
            setFile.delete();
            modFile.delete();
        }
    }

    @Test
    public void loadMethods_directoryAsFile_throwsAndCatchesExceptions() {
        // Covers the red catch (Exception e) and catch (IOException e) blocks.
        // We force exceptions by passing a DIRECTORY path when the program expects a FILE.
        File fakeEqFile = tempDir.resolve("fake_eq_dir").toFile();
        fakeEqFile.mkdir(); // Creates a folder, not a text file!

        File fakeModFile = tempDir.resolve("fake_mod_dir").toFile();
        fakeModFile.mkdir();

        Storage storage = new Storage(fakeEqFile.getAbsolutePath(), ui, "dummy_set.txt", fakeModFile.getAbsolutePath());

        // 1. load() tries to read the directory using Scanner -> Access Denied Exception
        // -> hits the catch (Exception e) block on line 90.
        ArrayList<Equipment> loadedEq = storage.load();
        assertTrue(loadedEq.isEmpty());

        // 2. loadModules() tries to check/create/read the directory -> Access Denied IOException
        // -> hits the catch (IOException e) block on line 317.
        ModuleList loadedMod = storage.loadModules();
        assertTrue(loadedMod.getModules().isEmpty());
    }

    @Test
    public void loadModules_emptyTagsAndInvalidModule_handledSafely() throws IOException {
        // Covers the yellow `!parts[2].trim().isEmpty()` check and
        // the red `catch (EquipmentMasterException e)` block.
        File modFile = tempDir.resolve("mod_edge_cases.txt").toFile();
        try (FileWriter fw = new FileWriter(modFile)) {
            // 1. Valid module, but the tags column is just empty spaces.
            // This tests the false branch of `!parts[2].trim().isEmpty()`.
            fw.write("CS2113 | 100 |    \n");

            // 2. Invalid module to trigger EquipmentMasterException in the Module constructor.
            // A blank name or negative pax usually violates constructor validation.
            fw.write(" | -5 \n");
        }

        Storage storage = new Storage("dummy_eq.txt", ui, "dummy_set.txt", modFile.getAbsolutePath());
        ModuleList loaded = storage.loadModules();

        // The valid module with empty tags should be loaded successfully (size = 1).
        // The invalid module should trigger the Exception, print the message, and be skipped.
        assertEquals(1, loaded.getModules().size());
        assertEquals("CS2113", loaded.getModules().get(0).getName());
    }

    @Test
    public void parseEquipment_nullLine_returnsNull() throws IOException {
        Path testFile = tempDir.resolve("null_test.txt");

        try (FileWriter writer = new FileWriter(testFile.toFile())) {
            writer.write("\n"); // Empty line
        }

        Storage storage = new Storage(
                testFile.toString(),
                ui,
                tempDir.resolve("test_set.txt").toString(),
                tempDir.resolve("test_mod.txt").toString()
        );

        ArrayList<Equipment> loaded = storage.load();
        assertEquals(0, loaded.size());
    }

    @Test
    public void parseEquipment_missingFields_returnsNull() throws IOException {
        Path testFile = tempDir.resolve("missing_fields.txt");

        // Line missing some fields (should cause parse to fail and return null)
        try (FileWriter writer = new FileWriter(testFile.toFile())) {
            writer.write("STM32 | 50 | 45\n"); // Missing many fields
        }

        Storage storage = new Storage(
                testFile.toString(),
                ui,
                tempDir.resolve("test_set.txt").toString(),
                tempDir.resolve("test_mod.txt").toString()
        );

        ArrayList<Equipment> loaded = storage.load();

        // Should skip corrupted line
        assertEquals(0, loaded.size());
    }

    @Test
    public void loadSettings_fileExistsWithEmptyContent_returnsDefault() throws IOException {
        Path settingFile = tempDir.resolve("empty_setting.txt");

        try (FileWriter writer = new FileWriter(settingFile.toFile())) {
            writer.write(""); // Empty file
        }

        Storage storage = new Storage(
                tempDir.resolve("test_eq.txt").toString(),
                ui,
                settingFile.toString(),
                tempDir.resolve("test_mod.txt").toString()
        );

        String loaded = storage.loadSettings();
        assertEquals("AY2024/25 Sem1", loaded);
    }

    @Test
    public void loadSettings_fileExistsWithWhitespace_returnsTrimmed() throws IOException {
        Path settingFile = tempDir.resolve("whitespace_setting.txt");

        try (FileWriter writer = new FileWriter(settingFile.toFile())) {
            writer.write("  AY2025/26 Sem2  \n");
        }

        Storage storage = new Storage(
                tempDir.resolve("test_eq.txt").toString(),
                ui,
                settingFile.toString(),
                tempDir.resolve("test_mod.txt").toString()
        );

        String loaded = storage.loadSettings();
        assertEquals("AY2025/26 Sem2", loaded);
    }

    @Test
    public void loadModules_fileWithEmptyLines_skipsEmptyLines() throws IOException, EquipmentMasterException {
        Path moduleFile = tempDir.resolve("empty_lines_modules.txt");

        try (FileWriter writer = new FileWriter(moduleFile.toFile())) {
            writer.write("\n");
            writer.write("CG2111A | 150\n");
            writer.write("\n");
            writer.write("EE2026 | 200\n");
            writer.write("\n");
        }

        Storage storage = new Storage(
                tempDir.resolve("test_eq.txt").toString(),
                ui,
                tempDir.resolve("test_set.txt").toString(),
                moduleFile.toString()
        );

        ModuleList loaded = storage.loadModules();

        assertEquals(2, loaded.getModules().size());
    }

    @Test
    public void loadModules_withRequirements_success() throws IOException, EquipmentMasterException {
        Path moduleFile = tempDir.resolve("modules_with_reqs.txt");

        try (FileWriter writer = new FileWriter(moduleFile.toFile())) {
            writer.write("CG2111A | 150 | STM32=1.0,HDMI=0.5\n");
        }

        Storage storage = new Storage(
                tempDir.resolve("test_eq.txt").toString(),
                ui,
                tempDir.resolve("test_set.txt").toString(),
                moduleFile.toString()
        );

        ModuleList loaded = storage.loadModules();

        assertEquals(1, loaded.getModules().size());
        Module module = loaded.getModules().get(0);
        assertEquals("CG2111A", module.getName());
        assertEquals(150, module.getPax());
        assertEquals(2, module.getEquipmentRequirements().size());
    }

    @Test
    public void loadModules_withInvalidRatio_skipsThatRequirement() throws IOException, EquipmentMasterException {
        Path moduleFile = tempDir.resolve("invalid_ratio_modules.txt");

        try (FileWriter writer = new FileWriter(moduleFile.toFile())) {
            writer.write("CG2111A | 150 | STM32=1.0,HDMI=-0.5,OSC=2.0\n");
        }

        Storage storage = new Storage(
                tempDir.resolve("test_eq.txt").toString(),
                ui,
                tempDir.resolve("test_set.txt").toString(),
                moduleFile.toString()
        );

        ModuleList loaded = storage.loadModules();

        Module module = loaded.getModules().get(0);
        // Should only have valid ratios (STM32 and OSC, not HDMI with negative)
        assertEquals(2, module.getEquipmentRequirements().size());
    }

    @Test
    public void loadModules_withMalformedRequirement_skipsIt() throws IOException, EquipmentMasterException {
        Path moduleFile = tempDir.resolve("malformed_req_modules.txt");

        try (FileWriter writer = new FileWriter(moduleFile.toFile())) {
            writer.write("CG2111A | 150 | STM32=1.0,HDMI=abc,OSC=2.0\n");
        }

        Storage storage = new Storage(
                tempDir.resolve("test_eq.txt").toString(),
                ui,
                tempDir.resolve("test_set.txt").toString(),
                moduleFile.toString()
        );

        ModuleList loaded = storage.loadModules();

        Module module = loaded.getModules().get(0);
        // Should only have valid numeric ratios
        assertEquals(2, module.getEquipmentRequirements().size());
    }

    @Test
    public void saveModules_withRequirements_success() throws EquipmentMasterException, IOException {
        Path moduleFile = tempDir.resolve("save_with_reqs.txt");

        Storage storage = new Storage(
                tempDir.resolve("test_eq.txt").toString(),
                ui,
                tempDir.resolve("test_set.txt").toString(),
                moduleFile.toString()
        );

        ModuleList moduleList = new ModuleList();
        Module module = new Module("CG2111A", 150);
        module.addEquipmentRequirement("STM32", 1.0);
        module.addEquipmentRequirement("HDMI", 0.5);
        moduleList.addModule(module);

        storage.saveModules(moduleList);

        // Verify file content
        try (Scanner scanner = new Scanner(moduleFile)) {
            String line = scanner.nextLine();
            assertTrue(line.contains("CG2111A | 150 |"));
            assertTrue(line.contains("STM32=1.0"));
            assertTrue(line.contains("HDMI=0.5"));
        }
    }
}
