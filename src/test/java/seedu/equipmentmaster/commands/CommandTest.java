//@@author Hongyu1231
package seedu.equipmentmaster.commands;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.context.Context;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class CommandTest {

    /**
     * A simple concrete implementation of Command to test the base class logic.
     */
    private static class StubCommand extends Command {
        @Override
        public void execute(Context context) {
            // Do nothing for testing base class
        }
    }

    @Test
    public void isExit_default_returnsFalse() {
        // Test that the default implementation returns false
        Command command = new StubCommand();
        assertFalse(command.isExit(), "Default isExit() should be false.");
    }

    @Test
    public void logExecution_anyName_doesNotThrowException() {
        // Test the helper method to ensure it doesn't crash during logging
        Command command = new StubCommand();
        assertDoesNotThrow(() -> command.logExecution("TestCommand"),
                "logExecution should execute successfully without throwing exceptions.");
    }
}
