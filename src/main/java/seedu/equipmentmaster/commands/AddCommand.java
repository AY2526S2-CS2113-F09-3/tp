package seedu.equipmentmaster.commands;


import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.ArrayList;

import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_ADD_FORMAT;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a command that adds new equipment to the equipment list.
 * The command creates a new {@code Equipment} object with the specified
 * name and quantity, adds it to the equipment list, saves the updated
 * list to storage, and displays a confirmation message to the user.
 */
public class AddCommand extends Command {
    private static final Logger logger = Logger.getLogger(AddCommand.class.getName());

    private final String name;
    private final int quantity;
    private final AcademicSemester purchaseSem;
    private final double lifespanYears;
    private final ArrayList<String> moduleCodes;
    private final int minQuantity;


    /**
     * Constructs an {@code AddCommand} with the specified equipment name, quantity, and module codes.
     *
     * @param name        Name of the equipment to add.
     * @param quantity    Number of items to add.
     * @param quantity    Number of items to add.
     * @param moduleCodes List of module codes associated with this equipment.
     */
    public AddCommand(String name, int quantity, AcademicSemester purchaseSem,
                      double lifespanYears, int minQuantity, ArrayList<String> moduleCodes) {
        this.name = name;
        this.quantity = quantity;
        this.purchaseSem = purchaseSem;
        this.lifespanYears = lifespanYears;
        this.minQuantity = minQuantity;
        this.moduleCodes = moduleCodes != null ? moduleCodes : new ArrayList<>();
    }

    /**
     * Constructs an {@code AddCommand} with only the equipment name and quantity.
     * This constructor is used when adding basic equipment without any optional fields
     * (no purchase semester, no lifespan, no module codes).
     *
     * @param name     Name of the equipment to add.
     * @param quantity Number of items to add.
     */
    public AddCommand(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        this.purchaseSem = null;
        this.lifespanYears = 0.0;
        this.minQuantity = 0;
        this.moduleCodes = new ArrayList<>();
    }

    /**
     * Constructs an {@code AddCommand} with equipment name, quantity, and module codes.
     * This constructor is used when adding equipment associated with specific modules,
     * but without purchase semester or lifespan information.
     *
     * @param name        Name of the equipment to add.
     * @param quantity    Number of items to add.
     * @param moduleCodes List of module codes associated with this equipment.
     */
    public AddCommand(String name, int quantity, ArrayList<String> moduleCodes) {
        this.name = name;
        this.quantity = quantity;
        this.purchaseSem = null;
        this.lifespanYears = 0.0;
        this.minQuantity = 0;
        this.moduleCodes = moduleCodes != null ? moduleCodes : new ArrayList<>();
    }

    /**
     * Parses the arguments for the 'add' command and creates an AddCommand object.
     *
     * @param fullCommand The complete input string containing the 'add' command and its arguments.
     * @return An AddCommand object containing the parsed equipment name and quantity.
     * @throws EquipmentMasterException If the format is incorrect, quantity is missing/invalid, or negative.
     */
    public static AddCommand parse(String fullCommand) throws EquipmentMasterException {
        logger.log(Level.INFO, "Starting to parse add command input.");

        if (!fullCommand.contains("n/") || !fullCommand.contains("q/")) {
            logger.log(Level.WARNING, "Missing compulsory flags (n/ or q/) in user input.");
            throw new EquipmentMasterException(MESSAGE_INVALID_ADD_FORMAT);
        }

        // Extract name and quantity
        String name = extractArgument(fullCommand, "n/");
        if (name.contains("|") || name.contains(",") || name.contains("=")) {
            throw new EquipmentMasterException(
                    "Invalid name! Names cannot contain reserved storage characters: '|', ',', or '='"
            );
        }
        String qtString = extractArgument(fullCommand, "q/");
        String minQtyStr = extractArgument(fullCommand, "min/");
        String purchaseSemStr = extractArgument(fullCommand, "bought/");
        String lifespanYearsStr = extractArgument(fullCommand, "life/");

        if (name.isEmpty() || qtString.isEmpty()) {
            throw new EquipmentMasterException(MESSAGE_INVALID_ADD_FORMAT);
        }

        // Parse quantity
        int quantity;
        try {
            quantity = Integer.parseInt(qtString);
            if (quantity <= 0) {
                logger.log(Level.WARNING, "Parsed quantity is zero or negative: " + quantity);
                throw new EquipmentMasterException("Equipment quantity must be positive.");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Failed to parse quantity: " + qtString, e);
            throw new EquipmentMasterException("Please enter a valid whole number for quantity.");
        }
        AcademicSemester purchaseSem = null;
        double lifespanYear = 0.0;
        int minQuantity = 0;
        ArrayList<String> moduleCodes = new ArrayList<>();

        if (fullCommand.contains("bought/") && fullCommand.contains("life/")) {

            if (!purchaseSemStr.isEmpty() && !lifespanYearsStr.isEmpty()) {
                purchaseSem = new AcademicSemester(purchaseSemStr.trim());
                try {
                    lifespanYear = Double.parseDouble(lifespanYearsStr.trim());
                } catch (NumberFormatException e) {
                    throw new EquipmentMasterException("Please enter a valid number for lifespan in years");
                }
            }
        }

        minQtyStr = extractArgument(fullCommand, "min/");
        if (!minQtyStr.isEmpty()) {
            try {
                minQuantity = Integer.parseInt(minQtyStr);
            } catch (NumberFormatException e) {
                throw new EquipmentMasterException("Please enter a valid whole number for minimum threshold");
            }
        }
        moduleCodes = extractMultipleArguments(fullCommand, "m/");

        logger.log(Level.INFO, "Successfully parsed AddCommand for equipment: " + name);
        return new AddCommand(name, quantity, purchaseSem, lifespanYear, minQuantity, moduleCodes);
    }

    // @@author Hongyu1231
    /**
     * Extracts a single argument value for a given prefix.
     * Uses space-padding to prevent substring collisions and stops at the next valid flag.
     *
     * @param fullCommand The raw input string from the user.
     * @param prefix      The parameter flag to search for (e.g., "q/", "b/").
     * @return The extracted string value, or an empty string if not found.
     */
    private static String extractArgument(String fullCommand, String prefix) {
        String paddedCommand = " " + fullCommand.trim() + " ";
        String searchPrefix = " " + prefix;
        int startIdx = paddedCommand.indexOf(searchPrefix);
        if (startIdx == -1) {
            return "";
        }
        startIdx += searchPrefix.length();
        int endIdx = paddedCommand.length();
        String[] allPrefixes = {" n/", " q/", " sem/", " bought/", " life/", " m/", " min/"};
        for (String p : allPrefixes) {
            int pIdx = paddedCommand.indexOf(p, startIdx);
            if (pIdx != -1 && pIdx < endIdx) {
                endIdx = pIdx;
            }
        }
        return paddedCommand.substring(startIdx, endIdx).trim();
    }

    /**
     * Extracts multiple unique argument values for a repeating prefix.
     * It is case-insensitive and automatically filters out duplicate entries.
     *
     * @param fullCommand The raw input string from the user.
     * @param prefix      The repeating parameter flag to search for (e.g., "m/").
     * @return A list of extracted uppercase values, or an empty list if none found.
     */
    private static ArrayList<String> extractMultipleArguments(String fullCommand, String prefix) {
        ArrayList<String> results = new ArrayList<>();
        String paddedCommand = " " + fullCommand.trim() + " ";
        paddedCommand = paddedCommand.replaceAll(" (?i)" + prefix, " " + prefix.toLowerCase());
        String searchPrefix = " " + prefix.toLowerCase();
        String[] allPrefixes = {" n/", " q/", " sem/", " bought/", " life/", " m/", " min/"};
        int currentIndex = paddedCommand.indexOf(searchPrefix);
        while (currentIndex != -1) {
            int startIdx = currentIndex + searchPrefix.length();
            int endIdx = paddedCommand.length();
            for (String p : allPrefixes) {
                int pIdx = paddedCommand.indexOf(p, startIdx);
                if (pIdx != -1 && pIdx < endIdx) {
                    endIdx = pIdx;
                }
            }
            String value = paddedCommand.substring(startIdx, endIdx).trim().toUpperCase();
            if (!value.isEmpty() && !results.contains(value)) {
                results.add(value);
            }
            currentIndex = paddedCommand.indexOf(searchPrefix, endIdx);
        }
        return results;
    }
    // @@author

    /**
     * Executes the add equipment command.
     * Extracts necessary components from the context, adds the new equipment to the list,
     * and saves the updated list to storage.
     *
     * @param context The application context containing the equipment list, UI, and storage.
     */
    @Override
    public void execute(Context context) {
        EquipmentList equipments = context.getEquipments();
        Ui ui = context.getUi();
        Storage storage = context.getStorage();

        assert equipments != null : "EquipmentList dependency cannot be null";
        assert ui != null : "Ui dependency cannot be null";
        assert storage != null : "Storage dependency cannot be null";
        Equipment equipment = new Equipment(name, quantity, quantity, 0, purchaseSem, lifespanYears,
                moduleCodes, minQuantity, 0.0);
        equipments.addEquipment(equipment);
        storage.save(equipments.getAllEquipments());

        // Build message
        StringBuilder message = new StringBuilder();
        message.append("Added ").append(quantity).append(" of ").append(name);

        if (!moduleCodes.isEmpty()) {
            message.append(" with modules ").append(moduleCodes);
        }

        message.append(". (Total Available: ").append(equipment.getAvailable()).append(")");

        if (purchaseSem != null) {
            message.append(" Purchase: ").append(purchaseSem)
                    .append(" | Lifespan: ").append(lifespanYears)
                    .append(lifespanYears == 1.0 ? " year" : " years");
        }
        if (minQuantity > 0) {
            message.append(" | Min Threshold: ").append(minQuantity);
        }

        ui.showMessage(message.toString());
        //fix: alert if starting quantity is at or below minimum threshold
        if (minQuantity > 0 && quantity <= minQuantity) {
            ui.showMessage("!!! LOW STOCK ALERT: " + name +
                    " is at or below threshold! (Current: " + quantity +
                    ", Min: " + minQuantity + ")");
        }
    }
}
