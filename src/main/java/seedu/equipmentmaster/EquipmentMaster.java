package seedu.equipmentmaster;

import java.util.logging.LogManager;
import seedu.equipmentmaster.commands.Command;
import seedu.equipmentmaster.context.Context;
import seedu.equipmentmaster.equipmentlist.EquipmentList;
import seedu.equipmentmaster.exception.EquipmentMasterException;
import seedu.equipmentmaster.modulelist.ModuleList;
import seedu.equipmentmaster.parser.Parser;
import seedu.equipmentmaster.semester.AcademicSemester;
import seedu.equipmentmaster.storage.Storage;
import seedu.equipmentmaster.ui.Ui;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EquipmentMaster {
    private static final Logger logger = Logger.getLogger(EquipmentMaster.class.getName());
    private Storage storage;
    private AcademicSemester currentSystemSemester;
    private Ui ui;
    private EquipmentList equipments;
    private ModuleList moduleList;

    /**
     * Initializes the application, loads system settings, and populates the equipment list.
     *
     * @param equipmentFilePath The relative path to the data.txt storage file.
     * @param settingFilePath The relative path to the setting.txt storage file.
     * @param moduleFilePath The relative path to the module.txt storage file.
     */
    public EquipmentMaster(String equipmentFilePath, String settingFilePath, String moduleFilePath) {
        logger.log(Level.INFO, "Starting EquipmentMaster initialization...");
        this.ui = new Ui();
        this.storage = new Storage(equipmentFilePath, ui, settingFilePath, moduleFilePath);

        // @@author Hongyu1231
        // Load the system time from settings.txt during startup
        try {
            String savedSemStr = storage.loadSettings();
            this.currentSystemSemester = new AcademicSemester(savedSemStr);
        } catch (EquipmentMasterException e) {
            // Fallback to default if the saved settings are corrupted
            try {
                this.currentSystemSemester = new AcademicSemester("AY2024/25 Sem1");
            } catch (EquipmentMasterException ignored) {
                // Should not happen with valid hardcoded default
            }
        }

        // Load equipment data
        this.equipments = new EquipmentList(storage.load());
        logger.log(Level.INFO, "System time loaded successfully.");

        this.moduleList = storage.loadModules();

        // Check loaded commands
        logger.log(Level.INFO, "Loaded "+Parser.getCommandSpecs().size()+" commands.");
        assert !Parser.getCommandSpecs().isEmpty() : "No commands loaded! Check Parser initialization.";
        // @@author
    }

    public void run() {
        ui.showWelcomeMessage();
        Context context = new Context(equipments, moduleList, ui, storage, currentSystemSemester);
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine();
                Command c = Parser.parse(fullCommand);
                c.execute(context);
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
        new EquipmentMaster("data/equipment.txt", "data/setting.txt", "data/module.txt").run();
    }

    public EquipmentList getEquipmentList() {
        return this.equipments;
    }
}
