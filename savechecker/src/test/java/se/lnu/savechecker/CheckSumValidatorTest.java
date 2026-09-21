package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

class CheckSumValidatorTest {
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
        copyData[0x2598] = 96;
        assertFalse(CheckSumValidator.isValid(copyData));
    }
}