//@@author Hongyu1231
package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ListModCommandTest {
    private ModuleList moduleList;
    private Ui ui;

    @BeforeEach
    public void setUp() {
        moduleList = new ModuleList();
        ui = new Ui();
    }

    @Test
    public void execute_emptyModuleList_printsEmptyMessage() throws EquipmentMasterException {
        // Triggers the 'if (moduleList.getModules().isEmpty())' branch on line 38
        // Context parameters: equipments, moduleList, ui, storage, currentSemester
        Context context = new Context(null, moduleList, ui, null, null);

        ListModCommand command = new ListModCommand();
        command.execute(context);
    }

    @Test
    public void execute_populatedModuleList_printsModulesWithIndex() throws EquipmentMasterException {
        // Triggers the header printing and the loop starting at line 45
        moduleList.addModule(new Module("CG2111A", 150));
        moduleList.addModule(new Module("EE2026", 200));

        Context context = new Context(null, moduleList, ui, null, null);

        ListModCommand command = new ListModCommand();
        command.execute(context);
    }

    /**
     * Targets the assertion branch in the execute() method.
     * Evaluates the defensive programming check for a null context.
     */
    @Test
    public void execute_nullContext_assertionFails() {
        ListModCommand command = new ListModCommand();
        AssertionError thrown = assertThrows(AssertionError.class, () -> {
            command.execute(null);
        });
        assertTrue(thrown.getMessage().contains("Context should not be null during execution"));
    }
}
