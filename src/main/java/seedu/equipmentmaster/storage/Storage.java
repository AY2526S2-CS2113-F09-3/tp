package seedu.equipmentmaster.storage;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.module.Module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that handles the storage of the equipment list.
 * It reads data from and writes data to a specified .txt file.
 */
public class Storage {
    private static final Logger logger = Logger.getLogger(Storage.class.getName());

    private final String equipmentFilePath;
    private final Ui ui;
    private final String settingFilePath;
    private final String moduleFilePath;

    /**
     * Constructor.
     *
     * @param equipmentFilePath The relative path to the data.txt storage file.
     * @param settingFilePath   The relative path to the setting.txt storage file.
     * @param moduleFilePath    The relative path to the module.txt storage file.
     */
    public Storage(String equipmentFilePath, Ui ui, String settingFilePath, String moduleFilePath) {
        this.equipmentFilePath = equipmentFilePath;
        this.ui = ui;
        this.settingFilePath = settingFilePath;
        this.moduleFilePath = moduleFilePath;
    }

    /**
     * Ensures the parent directory for a given file exists.
     */
    private void ensureDirectoryExists(File file) {
        File directory = file.getParentFile();
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Saves the current system semester to the settings file.
     *
     * @param currentSem The semester to be saved.
     * @throws EquipmentMasterException If file writing fails. (Fixes the SetSemCommand compilation error)
     */
    public void saveSettings(AcademicSemester currentSem) throws EquipmentMasterException {
        try {
            File file = new File(settingFilePath);
            ensureDirectoryExists(file);

            try (FileWriter writer = new FileWriter(settingFilePath)) {
                writer.write(currentSem.toString());
            }
            logger.log(Level.INFO, "Successfully saved semester settings to file.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save settings", e);
            throw new EquipmentMasterException("Failed to save settings: " + e.getMessage());
        }
    }

    public String loadSettings() {
        File file = new File(settingFilePath);
        String defaultSem = "AY2024/25 Sem1";

        if (!file.exists()) {
            return defaultSem;
        }

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                return scanner.nextLine().trim();
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "Settings file not found, using default.");
        }
        return defaultSem;
    }

    /**
     * Saves the current list of equipment to the .txt file.
     *
     * @param equipments The current list of equipment.
     * @throws EquipmentMasterException If file writing fails.
     */
    public void save(ArrayList<Equipment> equipments) throws EquipmentMasterException {
        try {
            File file = new File(equipmentFilePath);
            ensureDirectoryExists(file);

            try (FileWriter writer = new FileWriter(equipmentFilePath)) {
                for (Equipment equipment : equipments) {
                    writer.write(equipment.toFileString() + System.lineSeparator());
                }
            }
            logger.log(Level.INFO, "Equipment data successfully saved.");
        } catch (IOException e) {
            throw new EquipmentMasterException("Error saving equipment data: " + e.getMessage());
        }
    }

    /**
     * Loads the equipment list stored in the .txt file.
     *
     * @return The list of equipment from the file. Returns an empty list if the file is not found.
     */
    public ArrayList<Equipment> load() {
        ArrayList<Equipment> equipments = new ArrayList<>();
        File file = new File(equipmentFilePath);

        if (!file.exists()) {
            return equipments;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Equipment eq = parseEquipment(line);
                if (eq != null) {
                    equipments.add(eq);
                }
            }
        } catch (Exception e) {
            ui.showMessage("Error loading equipment data: " + e.getMessage());
        }
        return equipments;
    }

    /**
     * Converts a formatted string from the .txt file into an Equipment object.
     *
     * @param line A single line of text from the save file.
     * @return An Equipment object, or null if the string format is corrupted.
     */
    private Equipment parseEquipment(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        try {
            String[] parts = line.split(" \\| ", -1);
            int totalParts = parts.length;

            String lastPart = parts[totalParts - 1].trim();
            boolean hasBuffer = false;
            double bufferPercentage = 0.0;

            try {
                if (!lastPart.isEmpty()) {
                    bufferPercentage = Double.parseDouble(lastPart);
                    hasBuffer = true;
                }
            } catch (NumberFormatException e) {
                hasBuffer = false;
            }

            int offset = hasBuffer ? 1 : 0;

            // Peel from back: Modules(1), Life(2), Sem(3), Min(4), Loan(5), Avail(6), Qty(7)
            String modulesStr = parts[totalParts - 1 - offset].trim();
            String lifeStr = parts[totalParts - 2 - offset].trim();
            double life = lifeStr.isEmpty() ? 0.0 : Double.parseDouble(lifeStr);
            String semStr = parts[totalParts - 3 - offset].trim();
            AcademicSemester sem = semStr.isEmpty() ? null : new AcademicSemester(semStr);

            int min = Integer.parseInt(parts[totalParts - 4 - offset].trim());
            int l = Integer.parseInt(parts[totalParts - 5 - offset].trim());
            int a = Integer.parseInt(parts[totalParts - 6 - offset].trim());
            int q = Integer.parseInt(parts[totalParts - 7 - offset].trim());

            StringBuilder nameBuilder = new StringBuilder();
            int nameEndIndex = totalParts - 7 - offset;
            for (int i = 0; i < nameEndIndex; i++) {
                if (i > 0) {
                    nameBuilder.append(" | ");
                }
                nameBuilder.append(parts[i]);
            }
            String name = nameBuilder.toString().trim();

            ArrayList<String> modules = new ArrayList<>();
            if (!modulesStr.isEmpty()) {
                for (String m : modulesStr.split(",")) {
                    modules.add(m.trim());
                }
            }

            return new Equipment(name, q, a, l, sem, life, modules, min, bufferPercentage);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Saves the current collection of modules to the designated text file.
     * Each module is saved on a new line in the format: "ModuleName | Pax".
     * Creates the necessary directories and file if they do not already exist.
     *
     * @param moduleList The {@code ModuleList} containing the modules to be saved.
     * @throws EquipmentMasterException If an I/O error occurs while writing to the file.
     */
    public void saveModules(ModuleList moduleList) throws EquipmentMasterException {
        File file = new File(moduleFilePath);
        ensureDirectoryExists(file);

        try (FileWriter fw = new FileWriter(file)) {
            for (Module m : moduleList.getModules()) {
                fw.write(formatModuleForSave(m) + System.lineSeparator());
            }
            logger.log(Level.INFO, "Module data successfully saved.");
        } catch (IOException e) {
            throw new EquipmentMasterException("Error saving modules: " + e.getMessage());
        }
    }

    private String formatModuleForSave(Module m) {
        HashMap<String, Double> reqs = m.getEquipmentRequirements();
        if (reqs == null || reqs.isEmpty()) {
            return m.getName() + " | " + m.getPax();
        }

        StringBuilder reqsBuilder = new StringBuilder();
        TreeMap<String, Double> sortedReqs = new TreeMap<>(reqs);
        for (String eqName : sortedReqs.keySet()) {
            reqsBuilder.append(eqName).append("=").append(reqs.get(eqName)).append(",");
        }
        reqsBuilder.setLength(reqsBuilder.length() - 1); // Remove trailing comma

        return m.getName() + " | " + m.getPax() + " | " + reqsBuilder.toString();
    }

    /**
     * Loads module data from the storage file into a new {@code ModuleList}.
     * If the specified file does not exist, it creates a new empty file.
     * Errors during file creation or data parsing are printed directly to the console.
     *
     * @return A {@code ModuleList} populated with the saved modules or an empty list if
     *     errors occur or a new file is created.
     */
    public ModuleList loadModules() {
        ModuleList loadedList = new ModuleList();
        File file = new File(moduleFilePath);

        if (!file.exists()) {
            return loadedList;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.trim().isEmpty()) {
                    continue;
                }

                parseAndAddModule(line, loadedList); // SLAP: Extracted complex parsing
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load module data", e);
            ui.showMessage("Error loading module data. File might be corrupted.");
        }
        return loadedList;
    }

    /**
     * Parses a single line from the module text file and adds it to the loaded list.
     */
    private void parseAndAddModule(String line, ModuleList loadedList) {
        String[] parts = line.split(" \\| ", 3);

        if (parts.length < 2) {
            return;
        }

        try {
            String name = parts[0].trim();
            int pax = Integer.parseInt(parts[1].trim());

            if (name.isEmpty()) {
                throw new EquipmentMasterException("Module name is missing in line: " + line);
            }

            Module newModule = new Module(name, pax);

            if (parts.length == 3 && !parts[2].trim().isEmpty()) {
                loadModuleRequirements(newModule, parts[2].trim(), name);
                // SLAP: Extracted requirement loading
            }
            loadedList.addModule(newModule);

        } catch (NumberFormatException e) {
            ui.showMessage("Data corruption detected: Module pax is not" +
                    " a valid integer in line: " + line);
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
        }
    }

    /**
     * Parses and adds equipment requirements to a newly loaded module.
     */
    private void loadModuleRequirements(Module newModule,
                                        String requirementsStr, String moduleName) {
        String[] requirements = requirementsStr.split(",");
        for (String req : requirements) {
            String[] pair = req.split("=");
            if (pair.length == 2) {
                String eqName = pair[0].trim();
                try {
                    double ratio = Double.parseDouble(pair[1].trim());
                    if (ratio <= 0) {
                        ui.showMessage("Skipping invalid ratio (" + ratio + ") for equipment '"
                                + eqName + "' in module '" + moduleName + "'.");
                        continue;
                    }
                    newModule.addEquipmentRequirement(eqName, ratio);
                } catch (NumberFormatException | EquipmentMasterException e) {
                    ui.showMessage("Skipping invalid equipment requirement '" + req.trim()
                            + "' for module '" + moduleName + "'.");
                }
            }
        }
    }
}
