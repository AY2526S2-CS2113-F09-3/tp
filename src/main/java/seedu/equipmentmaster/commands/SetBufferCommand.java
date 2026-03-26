package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.context.Context;

import java.util.logging.Level;
import java.util.logging.Logger;

import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_SETBUFFER_FORMAT;

/**
 * Represents a command to set the safety buffer percentage for existing equipment.
 */
public class SetBufferCommand extends Command {
    private static final Logger logger = Logger.getLogger(SetBufferCommand.class.getName());

    private final String name;
    private final int index; // -1 if using name
    private final double percentage;

    /**
     * Constructs a SetBufferCommand with the specified equipment name and buffer percentage.
     *
     * @param name       Name of the equipment to update.
     * @param percentage Buffer percentage to set.
     */
    public SetBufferCommand(String name, double percentage) {
        this.name = name;
        this.index = -1;
        this.percentage = percentage;
        assert percentage >= 0 : "Buffer percentage should be non-negative";
    }

    /**
     * Constructs a SetBufferCommand using equipment index.
     *
     * @param index      Index of the equipment to update (1-based).
     * @param percentage Buffer percentage to set.
     */
    public SetBufferCommand(int index, double percentage) {
        if (index <= 0) {
            throw new IllegalArgumentException("Index must be positive. Received: " + index);
        }

        if (percentage < 0) {
            throw new IllegalArgumentException("Buffer percentage cannot be negative.");
        }

        this.name = null;
        this.index = index;
        this.percentage = percentage;
    }

    /**
     * Parses the arguments for the 'setbuffer' command and creates a SetBufferCommand object.
     * Supports either name format (n/NAME b/VALUE) or index format (i/INDEX b/VALUE).
     *
     * @param fullCommand The complete input string containing the 'setbuffer' command and its arguments.
     * @return A SetBufferCommand object.
     * @throws EquipmentMasterException If the format is incorrect.
     */
    public static SetBufferCommand parse(String fullCommand) throws EquipmentMasterException {
        logger.log(Level.INFO, "Starting to parse setbuffer command input.");

        // Check for required flags
        if (!fullCommand.contains("b/")) {
            logger.log(Level.WARNING, "Missing compulsory flag (b/) in user input.");
            throw new EquipmentMasterException(MESSAGE_INVALID_SETBUFFER_FORMAT);
        }

        // Check if using name or index
        boolean hasName = fullCommand.contains("n/");
        boolean hasIndex = fullCommand.contains("i/");

        if (!hasName && !hasIndex) {
            logger.log(Level.WARNING, "Missing compulsory flag (n/ or i/) in user input.");
            throw new EquipmentMasterException(MESSAGE_INVALID_SETBUFFER_FORMAT);
        }

        if (hasName && hasIndex) {
            throw new EquipmentMasterException("Please specify either name (n/) OR index (i/), not both.");
        }

        // Extract buffer percentage
        String percentageStr = extractArgument(fullCommand, "b/");
        if (percentageStr.isEmpty()) {
            throw new EquipmentMasterException(MESSAGE_INVALID_SETBUFFER_FORMAT);
        }

        // Strip % symbol if present
        percentageStr = percentageStr.replace("%", "").trim();

        double percentage;
        try {
            percentage = Double.parseDouble(percentageStr);
            if (percentage < 0) {
                logger.log(Level.WARNING, "Parsed buffer percentage is negative: " + percentage);
                throw new EquipmentMasterException("Buffer percentage cannot be negative.");
            }
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Failed to parse buffer percentage: " + percentageStr, e);
            throw new EquipmentMasterException("Please enter a valid number for buffer percentage.");
        }

        // Handle name-based identification
        if (hasName) {
            String name = extractArgument(fullCommand, "n/");
            if (name.isEmpty()) {
                throw new EquipmentMasterException("Equipment name cannot be empty.");
            }
            logger.log(Level.INFO, "Successfully parsed SetBufferCommand for equipment: " + name
                    + " with buffer: " + percentage + "%");
            return new SetBufferCommand(name, percentage);
        }

        // Handle index-based identification
        String indexStr = extractArgument(fullCommand, "i/");
        if (indexStr.isEmpty()) {
            throw new EquipmentMasterException("Equipment index cannot be empty.");
        }

        int index;
        try {
            index = Integer.parseInt(indexStr);
            if (index <= 0) {
                throw new EquipmentMasterException("Index must be a positive number.");
            }
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Please enter a valid positive integer for index.");
        }

        logger.log(Level.INFO, "Successfully parsed SetBufferCommand for index: " + index
                + " with buffer: " + percentage + "%");
        return new SetBufferCommand(index, percentage);
    }

    /**
     * Extracts a single argument value for a given prefix.
     *
     * @param fullCommand The raw input string from the user.
     * @param prefix      The parameter flag to search for (e.g., "n/", "b/").
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
        String[] allPrefixes = {" n/", " i/", " b/", " q/", " sem/", " bought/", " life/", " m/", " min/"};
        for (String p : allPrefixes) {
            int pIdx = paddedCommand.indexOf(p, startIdx);
            if (pIdx != -1 && pIdx < endIdx) {
                endIdx = pIdx;
            }
        }
        return paddedCommand.substring(startIdx, endIdx).trim();
    }

    /**
     * Executes the setbuffer command.
     *
     * @param context The application context containing the equipment list, UI, and current system semester.
     */
    @Override
    public void execute(Context context) {
        EquipmentList equipments = context.getEquipments();
        Ui ui = context.getUi();
        Storage storage = context.getStorage();

        assert equipments != null : "EquipmentList dependency cannot be null";
        assert ui != null : "Ui dependency cannot be null";
        assert storage != null : "Storage dependency cannot be null";
        assert percentage >= 0 : "Buffer percentage should be non-negative";

        Equipment target;

        // Find by index or name
        if (index > 0) {
            // Check if index is within bounds
            if (index < 1 || index > equipments.getSize()) {
                ui.showMessage("Equipment at index " + index + " not found. (Total: "
                        + equipments.getSize() + " equipment(s))");
                return;
            }
            // Convert from 1-based user index to 0-based internal index
            target = equipments.getEquipment(index - 1);
        } else {
            target = equipments.findByName(name);
            if (target == null) {
                ui.showMessage("Equipment '" + name + "' not found.");
                return;
            }
        }

        target.setBufferPercentage(percentage);
        storage.save(equipments.getAllEquipments());

        ui.showMessage("Buffer Updated:\n" + target.getName() + " | Safety Buffer: " + percentage + "%");
        logger.log(Level.INFO, "Updated buffer for " + target.getName() + " to " + percentage + "%");
    }
}
