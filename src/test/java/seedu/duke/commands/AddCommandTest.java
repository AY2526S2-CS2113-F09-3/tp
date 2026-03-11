package seedu.duke.commands;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import seedu.duke.equipment.Equipment;
import seedu.duke.equipmentlist.EquipmentList;
import seedu.duke.storage.Storage;
import seedu.duke.ui.Ui;

public class AddCommandTest {

    @Test
    public void execute_validEquipment_addsToList() {
        EquipmentList equipments = new EquipmentList();
        Ui ui = new Ui();
        Storage storage = null;

        AddCommand command = new AddCommand("STM32", 5);

        command.execute(equipments, ui, storage);

        assertEquals(1, equipments.getSize());

        Equipment added = equipments.getEquipment(0);
        assertEquals("STM32", added.getName());
        assertEquals(5, added.getQuantity());
    }
}