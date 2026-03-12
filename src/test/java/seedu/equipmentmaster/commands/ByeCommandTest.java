package seedu.equipmentmaster.commands;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ByeCommandTest {

    @Test
    void isExit_returnsTrue() {
        ByeCommand command = new ByeCommand();
        assertTrue(command.isExit());
    }
}