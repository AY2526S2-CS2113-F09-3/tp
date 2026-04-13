package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import static seedu.equipmentmaster.common.Messages.MESSAGE_INVALID_SET_STATUS_FORMAT;
import static seedu.equipmentmaster.common.Messages.MESSAGE_NAME_CONTAINS_RESERVED_CHARS;

//@@author JovianJosh
/**
 * Represents a command to update the status (loaned/available) of equipment.
 * This command supports identifying equipment by either its index in the list or its name.
 * It features a flexible parser that allows flags (n/, q/, s/) to be in any order,
 * and supports a bare numeric INDEX (without any prefix) for index-based targeting.
 */
public class SetStatusCommand extends Command {
    private static final Logger logger = Logger.getLogger(SetStatusCommand.class.getName());

    private final Integer index;
    private final String name;
    private final int quantity;
    private final String status;

    /**
     * Constructs a SetStatusCommand using an index-based identifier.
     *
     * @param index    The 1-based index of the equipment in the list.
     * @param quantity The number of units to be updated.
     * @param status   The target status ("loaned" or "available").
     */
    public SetStatusCommand(Integer index, int quantity, String status) {
        this.index = index;
        this.name = null;
        this.quantity = quantity;
        this.status = status;
    }

    /**
     * Constructs a SetStatusCommand using a name-based identifier.
     *
     * @param name     The exact name of the equipment.
     * @param quantity The number of units to be updated.
     * @param status   The target status ("loaned" or "available").
     */
    public SetStatusCommand(String name, int quantity, String status) {
        this.index = null;
        this.name = name;
        this.quantity = quantity;
        this.status = status;
    }

    /**
     * Parses the raw command string into a SetStatusCommand.
     * Supports both name-based (n/) and index-based (bare INDEX) identification.
     *
     * @param fullCommand The complete user input.
     * @return A ready-to-execute SetStatusCommand.
     * @throws EquipmentMasterException If the format is invalid or values are illegal.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        logger.log(Level.INFO, "Starting to parse setstatus command input.");

        String paddedCommand = " " + fullCommand.trim() + " ";

        // Check for required flags
        if (!paddedCommand.contains(" q/") || !paddedCommand.contains(" s/")) {
            logger.log(Level.WARNING, "Missing compulsory flag (q/ or s/) in user input.");
            throw new EquipmentMasterException(MESSAGE_INVALID_SET_STATUS_FORMAT);
        }

        // Check if using name (n/ flag)
        boolean hasName = paddedCommand.contains(" n/");

        // Detect bare index: no name flag, and the first argument after command is a number
        boolean hasIndex = false;
        int bareIndex = -1;
        if (!hasName) {
            String trimmed = fullCommand.trim();
            String[] parts = trimmed.split("\\s+", 2);
            if (parts.length >= 2) {
                String firstToken = parts[1].split("\\s+")[0];
                if (firstToken.matches("\\d+")) {
                    hasIndex = true;
                    bareIndex = Integer.parseInt(firstToken);
                }
            }
        }

        if (!hasName && !hasIndex) {
            logger.log(Level.WARNING, "Missing compulsory flag (n/) or index number in user input.");
            throw new EquipmentMasterException(MESSAGE_INVALID_SET_STATUS_FORMAT);
        }

        // Extract and validate status
        String sStr = extractArgument(fullCommand, "s/").toLowerCase();
        if (sStr.isEmpty()) {
            throw new EquipmentMasterException(MESSAGE_INVALID_SET_STATUS_FORMAT);
        }
        if (!sStr.equals("loaned") && !sStr.equals("available")) {
            throw new EquipmentMasterException("Status must be either 'loaned' or 'available'.");
        }

        // Extract and validate quantity
        String qStr = extractArgument(fullCommand, "q/");
        if (qStr.isEmpty()) {
            throw new EquipmentMasterException(MESSAGE_INVALID_SET_STATUS_FORMAT);
        }
        int quantity = parseQuantity(qStr);

        // Handle name-based identification
        if (hasName) {
            String name = extractArgument(fullCommand, "n/");
            if (name.isEmpty()) {
                throw new EquipmentMasterException("Equipment name cannot be empty.");
            }
            if (name.contains("|") || name.contains(",") || name.contains("=")) {
                throw new EquipmentMasterException(MESSAGE_NAME_CONTAINS_RESERVED_CHARS);
            }
            logger.log(Level.INFO, "Successfully parsed SetStatusCommand for equipment: " + name
                    + " with status: " + sStr + ", quantity: " + quantity);
            return new SetStatusCommand(name, quantity, sStr);
        }

        // Handle index-based identification (bare index)
        if (bareIndex <= 0) {
            throw new EquipmentMasterException("Index must be a positive number.");
        }
        logger.log(Level.INFO, "Successfully parsed SetStatusCommand for index: " + bareIndex
                + " with status: " + sStr + ", quantity: " + quantity);
        return new SetStatusCommand(bareIndex, quantity, sStr);
    }

    /**
     * Extracts a single argument value for a given prefix.
     *
     * @param fullCommand The raw input string from the user.
     * @param prefix      The parameter flag to search for (e.g., "n/", "s/").
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
        String[] allPrefixes = {" n/", " q/", " s/"};
        for (String p : allPrefixes) {
            int pIdx = paddedCommand.indexOf(p, startIdx);
            if (pIdx != -1 && pIdx < endIdx) {
                endIdx = pIdx;
            }
        }
        return paddedCommand.substring(startIdx, endIdx).trim();
    }

    private static int parseQuantity(String qStr) throws EquipmentMasterException {
        try {
            int q = Integer.parseInt(qStr);
            if (q <= 0) {
                throw new NumberFormatException();
            }
            return q;
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Quantity must be a positive whole number.");
        }
    }

    /**
     * Executes the status update logic.
     * Updates available and loaned counts in memory and immediately synchronizes with Storage.
     *
     * @param context The application context containing lists and storage.
     * @throws EquipmentMasterException If stock is insufficient or saving fails.
     */
    @Override
    public void execute(Context context) throws EquipmentMasterException {
        EquipmentList equipments = context.getEquipments();
        Ui ui = context.getUi();
        Storage storage = context.getStorage();

        assert equipments != null : "EquipmentList dependency cannot be null";
        assert ui != null : "Ui dependency cannot be null";
        assert storage != null : "Storage dependency cannot be null";

        Equipment target;
        try {
            target = findTarget(equipments);
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
            return;
        }

        int currentAvail = target.getAvailable();
        int currentLoaned = target.getLoaned();

        try {
            if (status.equals("loaned")) {
                processLoan(target, currentAvail, currentLoaned, ui);
            } else {
                processReturn(target, currentAvail, currentLoaned, ui);
            }
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
            return;
        }

        // Persist – rollback if fails
        try {
            storage.save(equipments.getAllEquipments());
            logger.log(Level.INFO, "Successfully saved equipment status to storage.");
        } catch (EquipmentMasterException e) {
            // Rollback to original state
            target.setAvailable(currentAvail);
            target.setLoaned(currentLoaned);
            ui.showMessage("Error: Failed to save to disk. Changes reverted. " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to save status update for " + target.getName(), e);
            // Do NOT re-throw
        }
    }

    private Equipment findTarget(EquipmentList list) throws EquipmentMasterException {
        if (index != null) {
            if (index < 1 || index > list.getSize()) {
                throw new EquipmentMasterException("Invalid index. List size is " + list.getSize());
            }
            return list.getEquipment(index - 1);
        }
        Equipment e = list.findByName(name);
        if (e == null) {
            throw new EquipmentMasterException("Equipment '" + name + "' not found.");
        }
        return e;
    }

    private void processLoan(Equipment target, int avail, int loan, Ui ui) throws EquipmentMasterException {
        if (quantity > avail) {
            throw new EquipmentMasterException("Insufficient stock. Only " + avail + " units available.");
        }
        target.setAvailable(avail - quantity);
        target.setLoaned(loan + quantity);
        ui.showMessage("Successfully LOANED " + quantity + " units of " + target.getName() + ".");

        if (target.getMinQuantity() > 0 && target.getAvailable() <= target.getMinQuantity()) {
            ui.showMessage("!!! LOW STOCK ALERT: " + target.getName()
                    + " is at or below minimum threshold!");
        }
    }

    private void processReturn(Equipment target, int avail, int loan, Ui ui) throws EquipmentMasterException {
        if (quantity > loan) {
            throw new EquipmentMasterException("Return error. Only " + loan + " units are currently on loan.");
        }
        target.setAvailable(avail + quantity);
        target.setLoaned(loan - quantity);
        ui.showMessage("Successfully returned " + quantity + " units of " + target.getName() + " to AVAILABLE.");
    }
}
