package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.parser.Parser;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HelpCommandTest {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private Ui ui;
    private Storage storage;
    private EquipmentList equipmentList;

    @BeforeEach
    public void setUp() {
        ui = new Ui(System.in, new PrintStream(outputStreamCaptor));
        storage = new Storage("dummy/path.txt", ui);
        equipmentList = new EquipmentList();
    }

    @Test
    public void execute_helpCommand_displaysAllCommands() {
        HelpCommand helpCommand = new HelpCommand();
        helpCommand.execute(equipmentList, ui, storage);

        String output = outputStreamCaptor.toString();

        // Check standard header
        assertTrue(output.contains("Command"));
        assertTrue(output.contains("Format"));
        assertTrue(output.contains("Here are the available commands:"));

        // Check for presence of all registered commands
        for (Parser.CommandSpec spec : Parser.getCommandSpecs()) {
            assertTrue(output.contains(spec.getKeyword()), "Output should contain command keyword: "
                    + spec.getKeyword());
            assertTrue(output.contains(spec.getFormat()), "Output should contain command format: "
                    + spec.getFormat());
        }
    }
}

