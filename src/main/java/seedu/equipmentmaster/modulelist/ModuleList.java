// @@author Hongyu1231
package seedu.equipmentmaster.modulelist;

import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.module.Module;

import java.util.ArrayList;

/**
 * Manages the collection of all course modules in the system.
 * Provides operations to add, update, delete, and retrieve modules.
 */
public class ModuleList {
    private ArrayList<Module> modules;

    /**
     * Constructs an empty {@code ModuleList}.
     */
    public ModuleList() {
        this.modules = new ArrayList<>();
    }

    /**
     * Adds a new module to the list.
     * If a module with the same name (case-insensitive) already exists,
     * the new module is not added to avoid ambiguous duplicates.
     *
     * @param module The {@code Module} to be added.
     */
    public void addModule(Module module) {
        if (module == null) {
            return;
        }
        if (findModule(module.getName()) != null) {
            // A module with this name already exists; do not add a duplicate.
            return;
        }
        modules.add(module);
    }

    /**
     * Updates the pax of an existing module in the list.
     *
     * @param moduleName The name of the module to update.
     * @param newPax     The new student enrollment number.
     * @throws EquipmentMasterException If the specified module does not exist in the list.
     */
    public void updateModule(String moduleName, int newPax) throws EquipmentMasterException {
        Module module = findModule(moduleName);
        if (module == null) {
            throw new EquipmentMasterException("Module not found: " + moduleName);
        }
        module.setPax(newPax);
    }

    /**
     * Deletes a module from the list by its name.
     *
     * @param moduleName The name of the module to delete.
     * @throws EquipmentMasterException If the specified module does not exist in the list.
     */
    public void deleteModule(String moduleName) throws EquipmentMasterException {
        Module module = findModule(moduleName);
        if (module == null) {
            throw new EquipmentMasterException("Module not found: " + moduleName);
        }
        modules.remove(module);
    }

    /**
     * Helper method to find a module by its name.
     *
     * @param moduleName The name of the module to search for.
     * @return The {@code Module} object if found, or {@code null} if not found.
     */
    private Module findModule(String moduleName) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(moduleName)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Retrieves the entire list of modules.
     * This is primarily used by the Storage class to save data to the hard disk.
     *
     * @return An {@code ArrayList} containing all current modules.
     */
    public ArrayList<Module> getModules() {
        return modules;
    }

    public boolean hasModule(String moduleName) {
        // Reuses findModule so we guarantee the exact same search logic!
        return findModule(moduleName) != null;
    }

    /**
     * Retrieves a module from the list by its name (case-insensitive).
     *
     * @param moduleName The name of the module to find.
     * @return The Module object if found, or null if it doesn't exist.
     */
    public Module getModule(String moduleName) {
        // Wraps your private helper method so external commands can use it
        return findModule(moduleName);
    }
}
