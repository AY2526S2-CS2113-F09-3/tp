package seedu.equipmentmaster.commands;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.module.Module;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a command to delete a specific quantity of equipment.
 * Supports targeting equipment by its index in the list or by its exact name.
 */
public class DeleteCommand extends Command {
    private static final Logger logger = Logger.getLogger(DeleteCommand.class.getName());

    private static final String STATUS_AVAILABLE = "available";
    private static final String STATUS_LOANED = "loaned";
    private static final String FLAG_NAME = "n/";
    private static final String FLAG_QUANTITY = " q/";
    private static final String FLAG_STATUS = " s/";

    private String name = null;
    private int index = -1;
    private final int quantity;
    private final String status;

    /**
     * Constructor for name-based deletion.
     *
     * @param name     The exact name of the equipment.
     * @param quantity The amount to be removed.
     * @param status   The specific status of the equipment.
     */
    public DeleteCommand(String name, int quantity, String status) {
        assert name != null && !name.isEmpty() : "Name cannot be null or empty";

        this.name = name;
        this.quantity = quantity;
        this.status = status;
    }

    /**
     * Constructor for index-based deletion.
     *
     * @param index    The 1-based index of the equipment in the list.
     * @param quantity The amount to be removed.
     * @param status   The specific status of the equipment.
     */
    public DeleteCommand(int index, int quantity, String status) {
        assert index > 0 : "Index must be positive";

        this.index = index;
        this.quantity = quantity;
        this.status = status;
    }

    /**
     * Parses the arguments for the 'delete' command.
     * @param fullCommand The complete input string.
     * @return A DeleteCommand object.
     * @throws EquipmentMasterException If the format is invalid.
     */
    public static Command parse(String fullCommand) throws EquipmentMasterException {
        logger.log(Level.INFO, "Parsing DeleteCommand parameters");
        String paddedCommand = " " + fullCommand.replaceFirst("(?i)delete", "").trim() + " ";

        int qIndex = paddedCommand.indexOf(FLAG_QUANTITY);
        int sIndex = paddedCommand.indexOf(FLAG_STATUS);

        if (qIndex == -1 || sIndex == -1) {
            throw new EquipmentMasterException("Invalid format. Use: delete [INDEX|n/NAME] q/QUANTITY s/STATUS");
        }

        String identifierPart = paddedCommand.substring(0, Math.min(qIndex, sIndex)).trim();
        String qtString = extractValue(paddedCommand, qIndex, sIndex, FLAG_QUANTITY);
        String statusStr = extractValue(paddedCommand, sIndex, qIndex, FLAG_STATUS).toLowerCase();

        validateStatus(statusStr);
        int quantity = parseQuantity(qtString);

        return createDeleteCommand(identifierPart, quantity, statusStr);
    }

    /**
     * Executes the delete equipment command.
     * Reduces the specified quantity of an equipment or removes it entirely if the quantity reaches zero,
     * then updates the storage file.
     *
     * @param context The application context containing the equipment list, UI, and storage.
     * @throws EquipmentMasterException If the equipment is not found, quantity is invalid, or saving fails.
     */
    @Override
    public void execute(Context context) throws EquipmentMasterException {
        assert context != null : "Context should not be null";
        logExecution("DeleteCommand");

        EquipmentList equipments = context.getEquipments();
        Ui ui = context.getUi();
        ModuleList moduleList = context.getModuleList();

        Equipment target = findTarget(equipments);

        updateInternalQuantities(target);

        // Track if Safe Dereferencing actually modified any modules
        boolean isModuleModified = processDeletionResult(target, equipments, ui, moduleList);

        // Pass the flag and the moduleList to the storage method
        saveToStorage(context.getStorage(), equipments, moduleList, isModuleModified, ui);
    }

    /**
     * Helper method to locate the equipment based on name or index.
     */
    private Equipment findTarget(EquipmentList equipments) throws EquipmentMasterException {
        if (name != null) {
            for (Equipment e : equipments.getAllEquipments()) {
                if (e.getName().equalsIgnoreCase(name)) {
                    return e;
                }
            }
            throw new EquipmentMasterException("Equipment '" + name + "' not found.");
        }

        if (index < 1 || index > equipments.getSize()) {
            throw new EquipmentMasterException("Invalid index. Please check the list again.");
        }
        return equipments.getEquipment(index - 1);
    }

    private void updateInternalQuantities(Equipment target) throws EquipmentMasterException {
        int currentAmount = status.equals(STATUS_AVAILABLE) ? target.getAvailable() : target.getLoaned();

        if (quantity > currentAmount) {
            String statusDescription = status.equals(STATUS_AVAILABLE) ? "available" : "currently loaned out";
            throw new EquipmentMasterException("Only " + currentAmount + " unit(s) are " + statusDescription
                    + ". Cannot delete " + quantity + ".");
        }

        if (status.equals(STATUS_AVAILABLE)) {
            target.setAvailable(target.getAvailable() - quantity);
        } else {
            target.setLoaned(target.getLoaned() - quantity);
        }
        target.setQuantity(target.getQuantity() - quantity);
    }

    private boolean processDeletionResult(Equipment target, EquipmentList list, Ui ui, ModuleList moduleList) {
        boolean isModuleModified = false;
        ui.showMessage("Deleted " + quantity + " " + status + " unit(s) of " + target.getName() + ".");

        if (target.getQuantity() == 0) {
            list.removeEquipment(target);
            ui.showMessage("Notice: Item completely removed (Total reached 0).");

            if (moduleList != null) {
                // Iterate through every module in the system
                for (Module module : moduleList.getModules()) {
                    // Safe Dereferencing: Attempt to remove the equipment from the module.
                    boolean removed = module.removeEquipmentRequirement(target.getName());
                    if (removed) {
                        isModuleModified = true; // Mark as modified if a tag was actually removed
                    }
                }
            }
        } else if (target.getMinQuantity() > 0
                && target.getAvailable() <= target.getMinQuantity()) {
            ui.showMessage("!!! LOW STOCK ALERT: " + target.getName() + " is at or below threshold!");
        }

        return isModuleModified;
    }

    private static String extractValue(String cmd, int curIdx, int otherIdx, String flag) {
        if (curIdx < otherIdx) {
            return cmd.substring(curIdx + flag.length(), otherIdx).trim();
        }
        return cmd.substring(curIdx + flag.length()).trim();
    }

    private static void validateStatus(String s) throws EquipmentMasterException {
        if (!s.equals(STATUS_AVAILABLE) && !s.equals(STATUS_LOANED)) {
            throw new EquipmentMasterException("Status must be 'available' or 'loaned'.");
        }
    }

    private static int parseQuantity(String qStr) throws EquipmentMasterException {
        try {
            int q = Integer.parseInt(qStr);

            if (q <= 0) {
                throw new EquipmentMasterException("Quantity must be > 0.");
            }

            return q;
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "User provided invalid quantity string: " + qStr);
            throw new EquipmentMasterException("Quantity must be a valid whole number.");
        }
    }

    private static DeleteCommand createDeleteCommand(String id, int q, String s) throws EquipmentMasterException {
        if (id.startsWith(FLAG_NAME)) {
            String name = id.substring(FLAG_NAME.length()).trim();

            if (name.isEmpty()) {
                throw new EquipmentMasterException("Name cannot be empty.");
            }

            return new DeleteCommand(name, q, s);
        }
        try {
            return new DeleteCommand(Integer.parseInt(id), q, s);
        } catch (NumberFormatException e) {
            throw new EquipmentMasterException("Provide a valid name (n/) or index.");
        }
    }

    private void saveToStorage(Storage storage, EquipmentList list, ModuleList moduleList,
                               boolean isModuleModified, Ui ui) {
        try {
            if (storage != null) {
                // Always save the equipment list because quantities were changed
                storage.save(list.getAllEquipments());

                // CRITICAL FIX: Save module list ONLY if safe dereferencing modified it
                if (isModuleModified && moduleList != null) {
                    storage.saveModules(moduleList);
                }

                logger.log(Level.INFO, "Data successfully saved to disk after deletion.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to save data during DeleteCommand", e);
            ui.showMessage("Warning: Deletion successful in memory, but failed to save to disk.");
        }
    }
}
