package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.module.Module;
import seedu.equipmentmaster.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit tests for the {@code UpdateModCommand} class.
 */
public class UpdateModCommandTest {

    private ModuleList moduleList;

    @BeforeEach
    public void setUp() {
        Ui ui = new Ui();
        moduleList = new ModuleList();
        try {
            // Add a dummy module for execution tests
            moduleList.addModule(new Module("CG2271", 100));
        } catch (EquipmentMasterException e) {
            ui.showMessage(e.getMessage());
        }
    }

    @Test
    public void parse_validInput_success() throws EquipmentMasterException {
        String input = "updatemod n/CG2271 pax/200";
        UpdateModCommand command = UpdateModCommand.parse(input);
        assertEquals(UpdateModCommand.class, command.getClass());
    }

    @Test
    public void parse_missingNamePrefix_throwsException() {
        // Missing the "n/" prefix
        String input = "updatemod CG2271 pax/200";
        assertThrows(EquipmentMasterException.class, () -> {
            UpdateModCommand.parse(input);
        });
    }

    @Test
    public void parse_paxWithDecimals_throwsException() {
        // Pax cannot be a decimal number
        String input = "updatemod n/CG2271 pax/150.5";
        assertThrows(EquipmentMasterException.class, () -> {
            UpdateModCommand.parse(input);
        });
    }
}
