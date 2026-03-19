package seedu.equipmentmaster;

import java.util.logging.LogManager;
import seedu.equipmentmaster.commands.Command;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.parser.Parser;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EquipmentMaster {
    private static Storage storage;
    private static AcademicSemester currentSystemSemester;
    private static final Logger logger = Logger.getLogger(EquipmentMaster.class.getName());
    private Ui ui;
    private EquipmentList equipments;

    /**
     * Initializes the application, loads system settings, and populates the equipment list.
     *
     * @param filePath The path to the equipment data file.
     */
    public EquipmentMaster(String filePath) {
        logger.log(Level.INFO, "Starting EquipmentMaster initialization...");
        this.ui = new Ui();
        EquipmentMaster.storage = new Storage(filePath, ui);

        // Load the system time from settings.txt during startup
        try {
            String savedSemStr = storage.loadSettings();
            currentSystemSemester = new AcademicSemester(savedSemStr);
        } catch (EquipmentMasterException e) {
            // Fallback to default if the saved settings are corrupted
            try {
                currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            } catch (EquipmentMasterException ignored) {
                // Should not happen with valid hardcoded default
            }
        }

        // Load equipment data
        this.equipments = new EquipmentList(storage.load());
        logger.log(Level.INFO, "System time loaded successfully.");

        // Check loaded commands
        logger.log(Level.INFO, "Loaded "+Parser.getCommandSpecs().size()+" commands.");
        assert !Parser.getCommandSpecs().isEmpty() : "No commands loaded! Check Parser initialization.";
    }

    /**
     * Updates the global current academic semester for the entire application.
     *
     * @param semester The new AcademicSemester to set as the current system time.
     */
    public static void setCurrentSemester(AcademicSemester semester) {
        currentSystemSemester = semester;
    }

    /**
     * Retrieves the current global system academic semester.
     *
     * @return The AcademicSemester currently set as the system time.
     */
    public static AcademicSemester getCurrentSemester() {
        return currentSystemSemester;
    }

    public void run() {
        ui.showWelcomeMessage();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(equipments, ui, storage);
                isExit = c.isExit();
            } catch (EquipmentMasterException e) {
                ui.showMessage(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    public static void main(String[] args) throws EquipmentMasterException{
        LogManager.getLogManager().reset();
        new EquipmentMaster("data/equipment.txt").run();
    }

    public EquipmentList getEquipmentList() {
        return this.equipments;
    }
}
