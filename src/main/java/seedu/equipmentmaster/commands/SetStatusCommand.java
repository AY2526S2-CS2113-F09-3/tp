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

/**
 * Represents a command to update the status (loaned/available) of equipment.
 * This command supports identifying equipment by either its index in the list or its name.
 * It features a flexible parser that allows flags (n/, q/, s/) to be in any order.
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
     * Supports both name-based (n/) and index-based identification.
     *
     * @param fullCommand The complete user input.
     * @return A ready-to-execute SetStatusCommand.
     * @throws EquipmentMasterException If the format is invalid or values are illegal.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        logger.log(Level.INFO, "Parsing SetStatusCommand arguments.");
        String args = " " + fullCommand.replaceFirst("(?i)^setstatus", "").trim() + " ";

        if (args.trim().isEmpty()) {
            throw new EquipmentMasterException(MESSAGE_INVALID_SET_STATUS_FORMAT);
        }

        if (args.contains("n/")) {
            return parseByName(args);
        }
        return parseByIndex(args);
    }

    private static Command parseByName(String args) throws EquipmentMasterException {
        String name = extractValue(args, "n/");
        String qStr = extractValue(args, "q/");
        String sStr = extractValue(args, "s/").toLowerCase();

        validateCommonInputs(name, qStr, sStr);
        int quantity = parseQuantity(qStr);

        return new SetStatusCommand(name, quantity, sStr);
    }

    private static Command parseByIndex(String args) throws EquipmentMasterException {
        String[] words = args.trim().split("\\s+");
        int index;
        try {
            index = Integer.parseInt(words[0]);
            if (index < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Please provide a valid 1-based index or use n/NAME.");
        }

        String qStr = extractValue(args, "q/");
        String sStr = extractValue(args, "s/").toLowerCase();

        validateCommonInputs("placeholder", qStr, sStr);
        int quantity = parseQuantity(qStr);

        return new SetStatusCommand(index, quantity, sStr);
    }

    /**
     * Safely extracts the value following a specific flag by finding the start of the next flag.
     * This allows parameters to be provided in any order as per the User Guide.
     */
    private static String extractValue(String input, String flag) {
        int start = input.indexOf(flag);
        if (start == -1) {
            return "";
        }
        start += flag.length();

        int end = input.length();
        String[] possibleNextFlags = {" n/", " q/", " s/"};
        for (String f : possibleNextFlags) {
            int nextIdx = input.indexOf(f, start);
            if (nextIdx != -1 && nextIdx < end) {
                end = nextIdx;
            }
        }
        return input.substring(start, end).trim();
    }

    private static void validateCommonInputs(String name, String qStr, String sStr) throws EquipmentMasterException {
        if (name.isEmpty() || qStr.isEmpty() || sStr.isEmpty()) {
            throw new EquipmentMasterException(MESSAGE_INVALID_SET_STATUS_FORMAT);
        }
        if (name.contains("|") || name.contains(",") || name.contains("=")) {
            throw new EquipmentMasterException(MESSAGE_NAME_CONTAINS_RESERVED_CHARS);
        }
        if (!sStr.equals("loaned") && !sStr.equals("available")) {
            throw new EquipmentMasterException("Status must be either 'loaned' or 'available'.");
        }
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
        assert context != null : "Context should not be null during execution";
        logExecution("SetStatusCommand");

        EquipmentList equipments = context.getEquipments();
        Equipment target = findTarget(equipments);

        int currentAvail = target.getAvailable();
        int currentLoaned = target.getLoaned();

        if (status.equals("loaned")) {
            processLoan(target, currentAvail, currentLoaned, context.getUi());
        } else {
            processReturn(target, currentAvail, currentLoaned, context.getUi());
        }

        // PERSISTENCE: Save to disk immediately to prevent data loss or desync
        try {
            Storage storage = context.getStorage();
            if (storage != null) {
                storage.save(equipments.getAllEquipments());
                logger.log(Level.INFO, "Successfully saved equipment status to storage.");
            }
        } catch (EquipmentMasterException e) {
            context.getUi().showMessage("Warning: Memory updated, but failed to save to disk: " + e.getMessage());
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

        if (target.getAvailable() < target.getMinQuantity()) {
            ui.showMessage("!!! LOW STOCK ALERT: " + target.getName() + " is below minimum threshold!");
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
