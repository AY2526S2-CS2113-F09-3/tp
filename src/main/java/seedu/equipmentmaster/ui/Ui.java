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
    private final String logo = "    ______                                     __   "
            + "__  ___                __               \n"
            + "   / ____/____ _ __  __ ____   ____   ____ _  / /_   /  |/  /____ _ _____ / /_ ___   _____\n"
            + "  / __/  / __ `// / / // __ \\ / __ \\ / __ `/ / __/  / /|_/ // __ `// ___// __// _ \\ / ___/\n"
            + " / /___ / /_/ // /_/ // /_/ // /_/ // /_/ / / /_   / /  / // /_/ /(__  )/ /_ /  __// /    \n"
            + "/_____/ \\__, / \\__,_// .___// .___/ \\__,_/  \\__/  /_/  /_/ \\__,_//____/ \\__/ \\___//_/     \n"
            + "          /_/       /_/    /_/                                                            ";

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
