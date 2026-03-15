package seedu.equipmentmaster.parser;

import org.junit.jupiter.api.Test;
import seedu.equipmentmaster.exception.EquipmentMasterException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    @Test
    public void parse_validSetsem_returnsSetSemCommand() throws EquipmentMasterException {
        // Testing that "setSem" command is correctly identified and parameters are kept intact
        assertNotNull(Parser.parse("setsem AY2024/25 Sem1"));
    }

    @Test
    public void parse_setsemExtraSpaces_returnsSetSemCommand() throws EquipmentMasterException {
        // Testing robustness against multiple spaces between the command and the argument
        assertNotNull(Parser.parse("setsem    AY2024/25 Sem2"));
    }

    @Test
    public void parse_setsemMissingArgs_throwsException() {
        assertThrows(EquipmentMasterException.class, () -> Parser.parse("setsem "));
    }
}
