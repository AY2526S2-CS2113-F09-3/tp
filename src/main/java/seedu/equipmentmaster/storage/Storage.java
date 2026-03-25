// @@author Hongyu1231
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that handles the storage of the equipment list.
 * It reads data from and writes data to a specified .txt file.
 */
public class Storage {
    private String equipmentFilePath;
    private Ui ui;
    private String settingFilePath;
    private String moduleFilePath;

    /**
     * Constructor.
     * @param equipmentFilePath The relative path to the data.txt storage file.
     * @param settingFilePath The relative path to the setting.txt storage file.
     * @param moduleFilePath The relative path to the module.txt storage file.
     */
    public Storage(String equipmentFilePath, Ui ui, String settingFilePath, String moduleFilePath) {
        this.equipmentFilePath = equipmentFilePath;
        this.ui = ui;
        this.settingFilePath = settingFilePath;
        this. moduleFilePath = moduleFilePath;
    }

    /**
     * Saves the current list of equipment to the .txt file.
     * @param equipments The current list of equipment.
     */
    public void save(ArrayList<Equipment> equipments){
        try {
            File file = new File(equipmentFilePath);
            File directory = file.getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }

            try (FileWriter writer = new FileWriter(equipmentFilePath)) {
                for (Equipment equipment : equipments) {
                    writer.write(equipment.toFileString() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            ui.showMessage("Error saving equipment data: " + e.getMessage());
        }
    }

    /**
     * Loads the equipment list stored in the .txt file.
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
     * Saves the current system semester to the settings file.
     * @param currentSem The semester to be saved.
     */
    public void saveSettings(AcademicSemester currentSem) {
        Logger storageLogger = Logger.getLogger(Storage.class.getName());
        try {
            File file = new File(settingFilePath);
            File directory = file.getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }

            try (FileWriter writer = new FileWriter(settingFilePath)) {
                writer.write(currentSem.toString());
            }
            storageLogger.log(Level.INFO, "Successfully saved semester settings to file.");
        } catch (IOException e) {
            ui.showMessage("Error saving settings: " + e.getMessage());
            storageLogger.log(Level.SEVERE, "Failed to save settings: " + e.getMessage());
        }
    }

    /**
     * Loads the system semester from the settings file.
     * @return The saved semester as a String, or a default value if not found.
     */
    public String loadSettings() {
        File file = new File(settingFilePath);
        if (!file.exists()) {
            return "AY2024/25 Sem1"; // Default value
        }

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                return scanner.nextLine().trim();
            }
        } catch (FileNotFoundException e) {
            // Fallback to default
        }
        return "AY2024/25 Sem1";
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
        File parentDirectory = file.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }
        try (FileWriter fw = new FileWriter(file)) {
            for (Module m : moduleList.getModules()) {
                StringBuilder reqsBuilder = new StringBuilder();
                HashMap<String, Double> reqs = m.getEquipmentRequirements();

                if (reqs != null && !reqs.isEmpty()) {
                    for (String eqName : reqs.keySet()) {
                        reqsBuilder.append(eqName).append("=").append(reqs.get(eqName)).append(",");
                    }
                    reqsBuilder.setLength(reqsBuilder.length() - 1);
                    fw.write(m.getName() + " | " + m.getPax() + " | " +
                            reqsBuilder.toString() + System.lineSeparator());
                } else {
                    fw.write(m.getName() + " | " + m.getPax() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new EquipmentMasterException("Error saving modules to file: " + moduleFilePath
                    + " - " + e.getMessage());
        }
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

        try {
            // Check if the file exists. If not, create it.
            if (!file.exists()) {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();

                // Return the empty list since there is no data to read yet
                return loadedList;
            }

            // Read and parse the data
            try (Scanner scanner = new Scanner(file);){
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split(" \\| ");

                    if (parts.length >= 2) {
                        String name = parts[0].trim();
                        int pax = Integer.parseInt(parts[1].trim());

                        try {
                            Module newModule = new Module(name, pax);
                            //Check if there is a 3rd part containing equipment tags
                            if (parts.length == 3 && !parts[2].trim().isEmpty()) {
                                String[] requirements = parts[2].split(",");
                                for (String req : requirements) {
                                    String[] pair = req.split("=");
                                    if (pair.length == 2) {
                                        String eqName = pair[0].trim();
                                        double ratio = Double.parseDouble(pair[1].trim());
                                        newModule.addEquipmentRequirement(eqName, ratio);
                                    }
                                }
                            }
                            // Add the reconstructed module to the list
                            loadedList.addModule(newModule);
                        } catch (EquipmentMasterException e) {
                            ui.showMessage(e.getMessage());
                        }
                    }
                }
            }

        } catch (IOException e) {
            // Print the I/O error directly to the console instead of throwing an exception
            ui.showMessage("Error creating a new module data file: " + e.getMessage());
            // Return an empty list to adhere to the method contract on errors
            return new ModuleList();
        } catch (NumberFormatException e) {
            // Print the parsing error directly to the console
            ui.showMessage("Data corruption detected: Module pax is not a valid integer.");
            // Return an empty list to adhere to the method contract on errors
            return new ModuleList();
        }
        // Return the fully loaded list when no errors have occurred
        return loadedList;
    }
}
