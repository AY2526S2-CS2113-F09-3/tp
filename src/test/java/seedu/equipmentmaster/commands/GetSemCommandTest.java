//@@author Hongyu1231
package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.ui.Ui;

public class GetSemCommandTest {

    private Ui ui;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
    }

    @Test
    public void execute_nullSemester_showsInitializationMessage() {
        // Triggers the 'if (current == null)' branch on line 23
        // Context parameters: equipments, moduleList, ui, storage, currentSemester
        Context context = new Context(null, null, ui, null, null);

        GetSemCommand command = new GetSemCommand();
        command.execute(context);
    }

    @Test
    public void execute_validSemester_showsSemesterMessage() throws EquipmentMasterException {
        // Adding 'throws EquipmentMasterException' to the method signature fixes the error
        AcademicSemester semester = new AcademicSemester("AY2025/26 Sem1");
        Context context = new Context(null, null, ui, null, semester);

        GetSemCommand command = new GetSemCommand();
        command.execute(context);
    }
}
