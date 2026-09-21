package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class CheckSumValidatorTest {

    private static final int FIRST_CHECKSUMMED_BYTE = 0x2598;
    private static final int CHECKSUMMD_BYTE_OFFSET = 0x3523;

    private byte[] loadSave(String resourcePath) throws Exception {
        Path saveFile = Path.of(getClass().getResource(resourcePath).toURI());
        byte[] data = Files.readAllBytes(saveFile);
        return data;
    }
    @Test
    void isValidPokemonGen1Save() throws Exception {
        assertTrue(CheckSumValidator.isValid(loadSave("/saves/Red/Pokemon Red (UE) [S][!].sav")));
    }
    @Test
    void rejectsCorruptedData() throws Exception {
        byte[] data = loadSave("/saves/Yellow/Yellow_Random_01.srm");
        byte[] copyData = Arrays.copyOf(data, data.length);
        copyData[FIRST_CHECKSUMMED_BYTE] = 96;
        assertFalse(CheckSumValidator.isValid(copyData));
    }
    @Test
    void rejectsCorruptedCheckSum() throws Exception {

    }
}