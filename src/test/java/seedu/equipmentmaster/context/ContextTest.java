package seedu.equipmentmaster.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit tests for the {@code Context} class.
 * Verifies that the global state container correctly holds and retrieves dependencies.
 */
public class ContextTest {

    private EquipmentList equipments;
    private ModuleList moduleList;
    private Ui ui;
    private Storage storage;
    private AcademicSemester semester;

    @BeforeEach
    public void setUp() throws EquipmentMasterException {
        equipments = new EquipmentList();
        moduleList = new ModuleList();
        ui = new Ui();
        // Using the 4-parameter constructor identified in your Storage class
        storage = new Storage("e.txt", ui, "s.txt", "m.txt");
        semester = new AcademicSemester("AY2024/25 Sem1");
    }

    @Test
    public void constructorAndGetters_validInput_returnsCorrectObjects() {
        // Initialize context with the prepared objects
        Context context = new Context(equipments, moduleList, ui, storage, semester);

        // Verify that the retrieved objects are the exact same instances passed in
        assertSame(equipments, context.getEquipments(), "EquipmentList should be the same instance");
        assertSame(moduleList, context.getModuleList(), "ModuleList should be the same instance");
        assertSame(ui, context.getUi(), "Ui should be the same instance");
        assertSame(storage, context.getStorage(), "Storage should be the same instance");
        assertSame(semester, context.getCurrentSemester(), "Semester should be the same instance");
    }

    @Test
    public void setCurrentSemester_newSemester_updatesSuccessfully() throws EquipmentMasterException {
        Context context = new Context(equipments, moduleList, ui, storage, semester);

        // Prepare a new semester object
        AcademicSemester newSemester = new AcademicSemester("AY2025/26 Sem2");

        // Act: update the semester
        context.setCurrentSemester(newSemester);

        // Assert: verify the update took effect
        assertSame(newSemester, context.getCurrentSemester(), "Semester should be updated to the new instance");
        assertEquals("AY2025/26 Sem2", context.getCurrentSemester().toString());
    }
}