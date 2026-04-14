package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipment.Equipment;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SetMinCommandTest {

    @TempDir
    Path tempDir;

    private EquipmentList equipments;
    private Ui ui;
    private Storage storage;
    private Context context;

    @BeforeEach
    public void setUp() {
        equipments = new EquipmentList();
        ui = new Ui();
        storage = new Storage(tempDir.resolve("test_equipment.txt").toString(), ui,
                tempDir.resolve("test_settings.txt").toString(),
                tempDir.resolve("test_modules.txt").toString());
        context = new Context(equipments, null, ui, storage, null);
    }

    @Test
    public void execute_validName_setsMinQuantity() throws EquipmentMasterException {
        // Arrange
        equipments.addEquipment(new Equipment("Resistor", 10));

        // Act
        SetMinCommand command = new SetMinCommand("Resistor", 5);
        command.execute(context);

        // Assert
        assertEquals(5, equipments.getEquipment(0).getMinQuantity());
    }

    @Test
    public void execute_validIndex_setsMinQuantity() throws EquipmentMasterException {
        // Arrange
        equipments.addEquipment(new Equipment("Resistor", 10));

        // Act
        SetMinCommand command = new SetMinCommand(1, 5);
        command.execute(context);

        // Assert
        assertEquals(5, equipments.getEquipment(0).getMinQuantity());
    }

    @Test
    public void execute_invalidIndex_throwsException() throws EquipmentMasterException {
        // Arrange
        equipments.addEquipment(new Equipment("Resistor", 10));

        // Act & Assert
        SetMinCommand command = new SetMinCommand(99, 5);
        assertThrows(EquipmentMasterException.class, () -> command.execute(context));
    }

    @Test
    public void execute_nameNotFound_throwsException() throws EquipmentMasterException {
        // Arrange
        equipments.addEquipment(new Equipment("Resistor", 10));

        // Act & Assert
        SetMinCommand command = new SetMinCommand("NonExistent", 5);
        assertThrows(EquipmentMasterException.class, () -> command.execute(context));
    }

    @Test
    public void execute_belowThreshold_showsWarning() throws EquipmentMasterException {
        equipments.addEquipment(new Equipment("Resistor", 3));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Ui ui = new Ui(System.in, new PrintStream(outputStream));
        Context context = new Context(equipments, null, ui, storage, null);

        // Act
        SetMinCommand command = new SetMinCommand("Resistor", 5);
        command.execute(context);

        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Warning: Item"));
    }

    @Test
    public void parse_validNameFormat_returnsSetMinCommand() throws EquipmentMasterException {
        Command command = SetMinCommand.parse("setmin n/Resistor min/5");
        assertTrue(command instanceof SetMinCommand);
    }

    @Test
    public void parse_validIndexFormat_returnsSetMinCommand() throws EquipmentMasterException {
        Command command = SetMinCommand.parse("setmin 1 min/5");
        assertTrue(command instanceof SetMinCommand);
    }

    @Test
    public void parse_negativeMin_throwsException() {
        assertThrows(EquipmentMasterException.class,
                () -> SetMinCommand.parse("setmin n/Resistor min/-1"));
    }

    @Test
    public void parse_missingMin_throwsException() {
        assertThrows(EquipmentMasterException.class,
                () -> SetMinCommand.parse("setmin n/Resistor"));
    }

    @Test
    public void parse_invalidFormat_throwsException() {
        assertThrows(EquipmentMasterException.class,
                () -> SetMinCommand.parse("setmin"));
    }
}
