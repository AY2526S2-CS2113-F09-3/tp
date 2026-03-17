package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

public class AddCommandTest {
    private static final String TEST_FILE_PATH = "test_equipment.txt";

    @Test
    public void execute_validEquipment_addsToList() throws EquipmentMasterException {

        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);

        // 1. Create dummy data for your new fields
        AcademicSemester testSem = new AcademicSemester("AY2023/24 Sem1");
        double testLifespan = 5.5;

        // 2. Pass all 4 arguments into the AddCommand constructor
        AddCommand command = new AddCommand("STM32", 5, testSem, testLifespan);

        command.execute(equipments, ui, storage);

        // Verify the list size increased
        assertEquals(1, equipments.getSize());

        // Verify the equipment was created with the correct data
        Equipment added = equipments.getEquipment(0);
        assertEquals("STM32", added.getName());
        assertEquals(5, added.getQuantity());

        // 3. Assert the new fields are correct
        assertEquals(testSem, added.getPurchaseSem());
        assertEquals(testLifespan, added.getLifespanYears(), 0.0001);
    }
}
