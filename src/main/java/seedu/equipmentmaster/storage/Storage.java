package seedu.equipmentmaster.storage;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.ui.Ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that handles the storage of the equipment list.
 * It reads data from and writes data to a specified .txt file.
 */
public class Storage {
    private String filePath;
    private Ui ui;
    private String settingsPath = "data/settings.txt";

    /**
     * Constructor.
     * @param filePath The relative path to the .txt storage file.
     */
    public Storage(String filePath, Ui ui) {
        this.filePath = filePath;
        this.ui = ui;
    }

    /**
     * Saves the current list of equipment to the .txt file.
     * @param equipments The current list of equipment.
     */
    public void save(ArrayList<Equipment> equipments){
        try {
            File file = new File(filePath);
            File directory = file.getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }

            try (FileWriter writer = new FileWriter(filePath)) {
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
        File file = new File(filePath);

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
        // Expected format: Name | Total | Available | Loaned
        if (line == null) {
            return null;
        }

        final String delimiter = " | ";
        // Find separators from the end: Name [delim] Total [delim] Available [delim] Loaned
        int lastSep = line.lastIndexOf(delimiter);
        if (lastSep == -1) {
            return null;
        }

        int thirdSep = line.lastIndexOf(delimiter, lastSep - 1);
        if (thirdSep == -1) {
            return null;
        }

        int secondSep = line.lastIndexOf(delimiter, thirdSep - 1);
        if (secondSep == -1) {
            return null;
        }

        int firstSep = line.lastIndexOf(delimiter, secondSep - 1);
        if (firstSep == -1) {
            return null;
        }

        String name = line.substring(0, firstSep);
        String totalStr = line.substring(firstSep + delimiter.length(), secondSep);
        String availableStr = line.substring(secondSep + delimiter.length(), thirdSep);
        String loanedStr = line.substring(thirdSep + delimiter.length(), lastSep);
        String modulesStr = line.substring(lastSep + delimiter.length());
        try {
            int totalQuantity = Integer.parseInt(totalStr.trim());
            int availableQuantity = Integer.parseInt(availableStr.trim());
            int loanedQuantity = Integer.parseInt(loanedStr.trim());

            ArrayList<String> moduleCodes = new ArrayList<>();
            if (modulesStr != null && !modulesStr.trim().isEmpty()) {
                String[] modules = modulesStr.split(",");
                for (String module : modules) {
                    String trimmed = module.trim();
                    if (!trimmed.isEmpty()) {
                        moduleCodes.add(trimmed);
                    }
                }
            }
            return new Equipment(name, totalQuantity, availableQuantity, loanedQuantity,
                    null, 0, moduleCodes);

        } catch (NumberFormatException e) {
            // Ignore corrupted lines
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
            File file = new File(settingsPath);
            File directory = file.getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }

            try (FileWriter writer = new FileWriter(settingsPath)) {
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
        File file = new File(settingsPath);
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
}
