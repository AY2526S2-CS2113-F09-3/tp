package seedu.equipmentmaster.ui;

import static seedu.equipmentmaster.common.Messages.MESSAGE_GOODBYE;
import static seedu.equipmentmaster.common.Messages.MESSAGE_WELCOME;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Ui {
    private static final String DIVIDER = "===================================================";
    private final Scanner in;
    private final PrintStream out;
    private final String logo =  "    ______                             __  ___           __\n"
            + "   / ____/___  __  ______  ____  ___  /  |/  /___ ______/ /____  _____\n"
            + "  / __/ / __ `/ / / / __ \\/ __ \\/ _ \\/ /|_/ / __ `/ ___/ __/ _ \\/ ___/\n"
            + " / /___/ /_/ / /_/ / /_/ / /_/ /  __/ /  / / /_/ (__  ) /_/  __/ /\n"
            + "/_____/\\__, /\\__,_/ .___/ .___/\\___/\\__/  /_/  /_/\\__,_/____/\\__/\n"
            + "      /____/     /_/   /_/\n";

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
        showMessage(logo);
        showLine();
    }

    public void showGoodByeMessage() {
        showMessage(MESSAGE_GOODBYE);
    }

    public void showLine() {
        showMessage(DIVIDER);
    }
}
