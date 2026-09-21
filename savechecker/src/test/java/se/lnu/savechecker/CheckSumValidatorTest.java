package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class CheckSumValidatorTest {

    private static final int FIRST_CHECKSUMMED_BYTE = 0x2598

    @Test
    void isValidPokemonGen1Save() throws Exception {
        Path saveFile = Path.of(getClass().getResource("/saves/Red/Pokemon Red (UE) [S][!].sav").toURI());
        byte[] data = Files.readAllBytes(saveFile);
        assertTrue(CheckSumValidator.isValid(data));
    }
    @Test
    void rejectsCorruptedData() throws Exception {
        Path saveFile = Path.of(getClass().getResource("/saves/Yellow/Yellow_Random_01.srm").toURI());
        byte[] data = Files.readAllBytes(saveFile);
        byte[] copyData = Arrays.copyOf(data, data.length);
        copyData[FIRST_CHECKSUMMED_BYTE] = 96;
        assertFalse(CheckSumValidator.isValid(copyData));
    }
}