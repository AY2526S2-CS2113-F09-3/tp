// @@author Hongyu1231
package seedu.equipmentmaster.context;

import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

/**
 * Encapsulates the global state and dependencies required for executing commands.
 * This class acts as a single data container, preventing methods from having excessively
 * long parameter lists and avoiding the use of global static variables.
 */
public class Context {
    private final EquipmentList equipments;
    private final ModuleList moduleList;
    private final Ui ui;
    private final Storage storage;
    private AcademicSemester currentSemester;

    /**
     * Constructs a {@code Context} object with the specified application states.
     *
     * @param equipments      The current list of equipment in the system.
     * @param moduleList      The current list of course modules in the system.
     * @param ui              The user interface component for displaying messages.
     * @param storage         The storage component handling file read/write operations.
     * @param currentSemester The current academic semester of the system.
     */
    public Context(EquipmentList equipments, ModuleList moduleList, Ui ui,
                   Storage storage, AcademicSemester currentSemester) {
        this.equipments = equipments;
        this.moduleList = moduleList;
        this.ui = ui;
        this.storage = storage;
        this.currentSemester = currentSemester;
    }

    /**
     * Retrieves the equipment list from the context.
     *
     * @return The {@code EquipmentList} instance.
     */
    public EquipmentList getEquipments() {
        return equipments;
    }

    /**
     * Retrieves the module list from the context.
     *
     * @return The {@code ModuleList} instance.
     */
    public ModuleList getModuleList() {
        return moduleList;
    }

    /**
     * Retrieves the UI component from the context.
     *
     * @return The {@code Ui} instance.
     */
    public Ui getUi() {
        return ui;
    }

    /**
     * Retrieves the storage component from the context.
     *
     * @return The {@code Storage} instance.
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Retrieves the current academic semester from the context.
     *
     * @return The {@code AcademicSemester} instance representing the current system time.
     */
    public AcademicSemester getCurrentSemester() {
        return currentSemester;
    }

    /**
     * Updates the current academic semester within the context.
     *
     * @param currentSemester The new {@code AcademicSemester} to be set as the current system time.
     */
    public void setCurrentSemester(AcademicSemester currentSemester) {
        this.currentSemester = currentSemester;
    }
}
