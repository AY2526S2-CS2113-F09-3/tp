package seedu.equipmentmaster.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * JUnit tests for the {@code Messages} class.
 * Ensures all system constants are correctly defined and accessible.
 */
public class MessagesTest {

    @Test
    public void testMessageConstants() {
        // Verify that the constants are not null and contain the expected strings
        assertNotNull(Messages.MESSAGE_WELCOME);
        assertEquals("Welcome to Equipment Master! How can I help you today?", Messages.MESSAGE_WELCOME);

        assertNotNull(Messages.MESSAGE_GOODBYE);
        assertEquals("Goodbye! See you back in the lab soon.", Messages.MESSAGE_GOODBYE);

        assertNotNull(Messages.MESSAGE_INVALID_INPUT);
        assertEquals("OOPS! Sorry I don't understand this :(", Messages.MESSAGE_INVALID_INPUT);

        assertNotNull(Messages.MESSAGE_INVALID_ADD_FORMAT);
        assertEquals("Please enter with the right format (with n/ and q/)", Messages.MESSAGE_INVALID_ADD_FORMAT);

        assertNotNull(Messages.MESSAGE_INVALID_FIND_FORMAT);
        assertEquals("Please provide a keyword to find. Example: find STM32", Messages.MESSAGE_INVALID_FIND_FORMAT);

        assertNotNull(Messages.MESSAGE_DIVIDER);
        assertEquals("===================================================", Messages.MESSAGE_DIVIDER);
    }

    @Test
    public void testNewMessages() {
        // Verify the newer status and buffer related messages
        assertEquals("Invalid setstatus format. Use: setstatus n/NAME q/QUANTITY s/STATUS",
                Messages.MESSAGE_INVALID_SET_STATUS_FORMAT);

        assertEquals("Invalid setbuffer format. Usage: setbuffer n/NAME b/PERCENTAGE or setbuffer i/INDEX b/PERCENTAGE",
                Messages.MESSAGE_INVALID_SETBUFFER_FORMAT);
    }

    @Test
    public void instantiateMessages_success() {
        // JaCoCo often marks the class header red if the constructor is never called.
        // This dummy instantiation ensures the class coverage hits 100%.
        Messages messages = new Messages();
        assertNotNull(messages);
    }
}