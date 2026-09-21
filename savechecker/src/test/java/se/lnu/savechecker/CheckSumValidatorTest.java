package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class CheckSumValidatorTest {

    private static final int FIRST_CHECKSUMMED_BYTE = 0x2598;
    private static final int STORED_CHECKSUM_BYTE = 0x3523;
    private static final String[] ALL_SAVES = {
        "/saves/Red/Pokemon Red (UE) [S][!].sav",
        "/saves/Yellow/Yellow_Random_01.srm",
        "/saves/Yellow/Yellow_Randomizer_01.srm",
        "/saves/Yellow/Yellow_Randomizer_02.srm"
    };

    private byte[] loadSave(String resourcePath) throws Exception {
        Path saveFile = Path.of(getClass().getResource(resourcePath).toURI());
        return Files.readAllBytes(saveFile);
    }
    @Test
    void isValidForAllFixtureSaves() throws Exception {
        for (String path : ALL_SAVES) {
            assertTrue(CheckSumValidator.isValid(loadSave(path)), path);
        }
    }
    @Test
    void rejectsCorruptedData() throws Exception {
        byte[] data = loadSave("/saves/Yellow/Yellow_Random_01.srm");
        byte[] copyData = Arrays.copyOf(data, data.length);
        copyData[STORED_CHECKSUM_BYTE] = 96;
        assertFalse(CheckSumValidator.isValid(copyData));
    }
    @Test
    void rejectsCorruptedCheckSum() throws Exception {
        byte[] data = loadSave("/saves/Yellow/Yellow_Randomizer_01.srm");
        byte[] copyData = Arrays.copyOf(data, data.length);
        copyData[STORED_CHECKSUM_BYTE] = 48;
        assertFalse(CheckSumValidator.isValid(copyData));
    }
}