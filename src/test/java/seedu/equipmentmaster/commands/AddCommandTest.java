package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.ArrayList;

public class AddCommandTest {
    private static final String TEST_FILE_PATH = "test_equipment.txt";

    @Test
    public void execute_validEquipment_addsToList() {
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);

        AddCommand command = new AddCommand("STM32", 5);

        command.execute(equipments, ui, storage);

        assertEquals(1, equipments.getSize());

        Equipment added = equipments.getEquipment(0);
        assertEquals("STM32", added.getName());
        assertEquals(5, added.getQuantity());
        assertTrue(added.getModuleCodes().isEmpty());
    }

    @Test
    public void execute_validEquipmentWithModules_addsToList() {
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = new Storage(TEST_FILE_PATH, ui);

        ArrayList<String> modules = new ArrayList<>();
        modules.add("EE2026");
        modules.add("CG2028");

        AddCommand command = new AddCommand("FPGA", 40, modules);

        command.execute(equipments, ui, storage);

        assertEquals(1, equipments.getSize());

        Equipment added = equipments.getEquipment(0);
        assertEquals("FPGA", added.getName());
        assertEquals(40, added.getQuantity());
        assertEquals(2, added.getModuleCodes().size());
        assertTrue(added.getModuleCodes().contains("EE2026"));
        assertTrue(added.getModuleCodes().contains("CG2028"));
    }

}
