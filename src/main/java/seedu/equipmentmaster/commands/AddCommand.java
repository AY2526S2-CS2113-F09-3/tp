package seedu.equipmentmaster.commands;


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

    /**
     * Constructs an {@code AddCommand} with the specified equipment name and quantity.
     *
     * @param name          Name of the equipment to add.
     * @param quantity      Number of items to add.
     * @param purchaseSem   Sem that the item was bought
     * @param lifespanYears Lifespan of the item in year
     */
    public AddCommand(String name, int quantity, AcademicSemester purchaseSem, double lifespanYears) {
        this.name = name;
        this.quantity = quantity;
        this.purchaseSem = purchaseSem;
        this.lifespanYears = lifespanYears;
        this.moduleCodes = new ArrayList<>();
    }

    /**
     * Constructs an {@code AddCommand} with the specified equipment name, quantity, and module codes.
     *
     * @param name        Name of the equipment to add.
     * @param quantity    Number of items to add.
     * @param moduleCodes List of module codes associated with this equipment.
     */
    public AddCommand(String name, int quantity, AcademicSemester purchaseSem,
                      double lifespanYears, ArrayList<String> moduleCodes) {
        this.name = name;
        this.quantity = quantity;
        this.purchaseSem = purchaseSem;
        this.lifespanYears = lifespanYears;
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
        String qtString = extractArgument(fullCommand, "q/");

        if (name.isEmpty() || qtString.isEmpty()) {
            logger.log(Level.WARNING, "Name or quantity is empty.");
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
            throw new EquipmentMasterException("Please enter a valid whole number for quantity");
        }

        // Parse optional semester and lifespan
        AcademicSemester purchaseSem = null;
        double lifespanYears = 0.0;

        if (fullCommand.contains("bought/") && fullCommand.contains("life/")) {
            String purchaseSemStr = extractArgument(fullCommand, "bought/");
            String lifespanYearsStr = extractArgument(fullCommand, "life/");

            if (!purchaseSemStr.isEmpty() && !lifespanYearsStr.isEmpty()) {
                purchaseSem = new AcademicSemester(purchaseSemStr.trim());
                try {
                    lifespanYears = Double.parseDouble(lifespanYearsStr.trim());
                } catch (NumberFormatException e) {
                    logger.log(Level.WARNING, "Failed to parse lifespan: " + lifespanYearsStr, e);
                    throw new EquipmentMasterException("Please enter a valid number for lifespan in years");
                }
            }
        }

        // Parse optional module codes
        ArrayList<String> moduleCodes = new ArrayList<>();
        String[] parts = fullCommand.split(" ");
        for (String part : parts) {
            if (part.startsWith("m/")) {
                String moduleCode = part.substring(2).toUpperCase().trim();
                if (!moduleCode.isEmpty() && !moduleCodes.contains(moduleCode)) {
                    moduleCodes.add(moduleCode);
                }
            }
        }

        logger.log(Level.INFO, "Successfully parsed AddCommand for equipment: " + name);

        // Choose appropriate constructor
        if (purchaseSem != null && !moduleCodes.isEmpty()) {
            return new AddCommand(name, quantity, purchaseSem, lifespanYears, moduleCodes);
        } else if (purchaseSem != null) {
            return new AddCommand(name, quantity, purchaseSem, lifespanYears);
        } else if (!moduleCodes.isEmpty()) {
            return new AddCommand(name, quantity, moduleCodes);
        } else {
            return new AddCommand(name, quantity);
        }
    }

    /**
     * Extracts the argument value following the given prefix, up to the next known prefix or end of string.
     */
    private static String extractArgument(String fullCommand, String prefix) throws EquipmentMasterException {
        int prefixIndex = fullCommand.indexOf(prefix);
        if (prefixIndex < 0) {
            return ""; // Return empty for optional fields
        }
        int valueStart = prefixIndex + prefix.length();
        int valueEnd = fullCommand.length();
        String[] allPrefixes = {"n/", "q/", "bought/", "life/", "m/"};
        for (String otherPrefix : allPrefixes) {
            if (otherPrefix.equals(prefix)) {
                continue;
            }
            int idx = fullCommand.indexOf(otherPrefix, valueStart);
            if (idx != -1 && idx < valueEnd) {
                valueEnd = idx;
            }
        }
        if (valueStart >= valueEnd) {
            return "";
        }
        return fullCommand.substring(valueStart, valueEnd).trim();
    }

    /**
     * Executes the add command by creating the equipment,
     * adding it to the equipment list, saving the updated list,
     * and displaying a message to the user.
     */
    @Override
    public void execute(EquipmentList equipments, Ui ui, Storage storage) {
        Equipment equipment;

        if (purchaseSem != null) {
            // Has semester and lifespan
            equipment = new Equipment(name, quantity, quantity, 0, purchaseSem, lifespanYears, moduleCodes);
        } else {
            // No semester/lifespan
            equipment = new Equipment(name, quantity);
            for (String moduleCode : moduleCodes) {
                equipment.addModuleCode(moduleCode);
            }
        }

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

        ui.showMessage(message.toString());
    }
}
