package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_SETBUFFER_FORMAT;

/**
 * Represents a command to set the safety buffer percentage for existing equipment.
 */
public class SetBufferCommand extends Command {
    private static final Logger logger = Logger.getLogger(SetBufferCommand.class.getName());

    private final String name;
    private final double percentage;

    /**
     * Constructs a SetBufferCommand with the specified equipment name and buffer percentage.
     *
     * @param name       Name of the equipment to update.
     * @param percentage Buffer percentage to set.
     */
    public SetBufferCommand(String name, double percentage) {
        this.name = name;
        this.percentage = percentage;
        assert percentage >= 0 : "Buffer percentage should be non-negative";
    }

    /**
     * Parses the arguments for the 'setbuffer' command and creates a SetBufferCommand object.
     *
     * @param fullCommand The complete input string containing the 'setbuffer' command and its arguments.
     * @return A SetBufferCommand object.
     * @throws EquipmentMasterException If the format is incorrect.
     */
    public static SetBufferCommand parse(String fullCommand) throws EquipmentMasterException {
        logger.log(Level.INFO, "Starting to parse setbuffer command input.");

        if (!fullCommand.contains("n/") || !fullCommand.contains("b/")) {
            logger.log(Level.WARNING, "Missing compulsory flags (n/ or b/) in user input.");
            throw new EquipmentMasterException(MESSAGE_INVALID_SETBUFFER_FORMAT);
        }

        // Extract name and buffer percentage
        String name = extractArgument(fullCommand, "n/");
        String percentageStr = extractArgument(fullCommand, "b/");

        if (name.isEmpty() || percentageStr.isEmpty()) {
            throw new EquipmentMasterException(MESSAGE_INVALID_SETBUFFER_FORMAT);
        }

        // Strip % symbol if present (e.g., "10%" -> "10")
        percentageStr = percentageStr.replace("%", "").trim();

        // Parse percentage
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

        logger.log(Level.INFO, "Successfully parsed SetBufferCommand for equipment: " + name
                + " with buffer: " + percentage + "%");
        return new SetBufferCommand(name, percentage);
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
        String[] allPrefixes = {" n/", " b/", " q/", " sem/", " bought/", " life/", " m/", " min/"};
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
     * @param equipments The current list of equipment.
     * @param ui         The user interface to display messages.
     * @param storage    The storage utility to save changes.
     */
    @Override
    public void execute(EquipmentList equipments, ModuleList moduleList, Ui ui, Storage storage) {
        assert equipments != null : "EquipmentList dependency cannot be null";
        assert ui != null : "Ui dependency cannot be null";
        assert storage != null : "Storage dependency cannot be null";
        assert percentage >= 0 : "Buffer percentage should be non-negative";

        Equipment target = equipments.findByName(name);

        if (target == null) {
            ui.showMessage("Equipment '" + name + "' not found.");
            return;
        }

        target.setBufferPercentage(percentage);
        storage.save(equipments.getAllEquipments());

        ui.showMessage("Buffer Updated:\n" + name + " | Safety Buffer: " + percentage + "%");
        logger.log(Level.INFO, "Updated buffer for " + name + " to " + percentage + "%");
    }
}
