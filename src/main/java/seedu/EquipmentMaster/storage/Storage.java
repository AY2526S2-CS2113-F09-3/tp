package seedu.EquipmentMaster.storage;

import seedu.EquipmentMaster.equipment.Equipment;
import seedu.EquipmentMaster.exception.EquipmentMasterException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A class that handles the storage of the equipment list.
 * It reads data from and writes data to a specified .txt file.
 */
public class Storage {
    private String filePath;

    /**
     * Constructor.
     * @param filePath The relative path to the .txt storage file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the current list of equipment to the .txt file.
     * @param equipments The current list of equipment.
     */
    public void save(ArrayList<Equipment> equipments) throws EquipmentMasterException{
        try {
            File file = new File(filePath);
            File directory = file.getParentFile();

            // Create directory if it doesn't exist (e.g., creating the "data" folder)
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                for (Equipment equipment : equipments) {
                    // Make sure your Equipment class has a toFileString() method!
                    writer.write(equipment.toFileString() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            throw new EquipmentMasterException("Failed to save data to " + filePath + ": " + e.getMessage());
        }
    }

    /**
     * Loads the equipment list stored in the .txt file.
     * @return The list of equipment from the file. Returns an empty list if the file is not found.
     */
    public ArrayList<Equipment> load() throws EquipmentMasterException{
        ArrayList<Equipment> equipments = new ArrayList<>();
        File file = new File(filePath);

        // If file doesn't exist, return an empty list to start fresh
        if (!file.exists()) {
            return equipments;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Equipment equipment = parseEquipment(line);

                // Only add if the line was not corrupted
                if (equipment != null) {
                    equipments.add(equipment);
                }
            }
        } catch (Exception e) {
            throw new EquipmentMasterException("Failed to save data to " + filePath + ": " + e.getMessage());
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
        int secondSep = line.lastIndexOf(delimiter, lastSep - 1);
        if (secondSep == -1) {
            return null;
        }

        int firstSep = line.lastIndexOf(delimiter, secondSep - 1);
        if (firstSep == -1) {
            return null;
        }

        String name = line.substring(0, firstSep);
        String totalStr = line.substring(firstSep + delimiter.length(), secondSep);
        String availableStr = line.substring(secondSep + delimiter.length(), lastSep);
        String loanedStr = line.substring(lastSep + delimiter.length());
        try {
            int totalQuantity = Integer.parseInt(totalStr.trim());
            int availableQuantity = Integer.parseInt(availableStr.trim());
            int loanedQuantity = Integer.parseInt(loanedStr.trim());
            return new Equipment(name, totalQuantity, availableQuantity, loanedQuantity);

        } catch (NumberFormatException e) {
            // Ignore corrupted lines
            return null;
        }
    }
}