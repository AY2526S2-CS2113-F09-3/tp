package seedu.duke.ui;

import static seedu.duke.common.Messages.MESSAGE_GOODBYE;
import static seedu.duke.common.Messages.MESSAGE_WELCOME;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Ui {
    private static final String DIVIDER = "===================================================";
    private final Scanner in;
    private final PrintStream out;

    public Ui() {
        this(System.in, System.out);
    }

    public Ui(InputStream in, PrintStream out) {
        this.in = new Scanner(in);
        this.out = out;
    }

    public String readCommand() {
        return in.nextLine().trim();
    }

    public void showMessage(String message) {
        out.println(message);
    }

    public void showWelcomeMessage() {
        showMessage(MESSAGE_WELCOME);
    }

    public void showGoodByeMessage() {
        showMessage(MESSAGE_GOODBYE);
    }

    public void showLine() {
        showMessage(DIVIDER);
    }
}
