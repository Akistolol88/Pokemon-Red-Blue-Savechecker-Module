package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

class CheckSumValidatorTest {

    private static final int FIRST_CHECKSUMMED_BYTE = 0x2598;
    private static final int STORED_CHECKSUM_BYTE = 0x3523;

    @Test
    void isValidForAllFixtureSaves() throws Exception {
        List<Path> saves = SaveFixtures.findAll();
        assertFalse(saves.isEmpty(), "no save files found in /saves");
        for (Path save : saves) {
            assertTrue(CheckSumValidator.isValid(Files.readAllBytes(save)),
                save.getFileName().toString());
        }
    }
    @Test
    void rejectsCorruptedData() throws Exception {
        byte[] data = SaveFixtures.load("/saves/Yellow/Yellow_Random_01.srm");
        byte[] copyData = Arrays.copyOf(data, data.length);
        copyData[FIRST_CHECKSUMMED_BYTE] = (byte) (copyData[FIRST_CHECKSUMMED_BYTE] + 1);
        assertFalse(CheckSumValidator.isValid(copyData));
    }
    @Test
    void rejectsCorruptedCheckSum() throws Exception {
        byte[] data = SaveFixtures.load("/saves/Yellow/Yellow_Randomizer_01.srm");
        byte[] copyData = Arrays.copyOf(data, data.length);
        copyData[STORED_CHECKSUM_BYTE] = (byte) (copyData[STORED_CHECKSUM_BYTE] + 1);
        assertFalse(CheckSumValidator.isValid(copyData));
    }
}