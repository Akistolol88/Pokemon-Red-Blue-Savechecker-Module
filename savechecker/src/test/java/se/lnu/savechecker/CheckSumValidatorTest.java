package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

class CheckSumValidatorTest {

    private final CheckSumValidator validator = new CheckSumValidator();

    private static final int FIRST_CHECKSUMMED_BYTE = 0x2598;
    private static final int LAST_CHECKSUMMED_BYTE = 0x3522;
    private static final int STORED_CHECKSUM_BYTE = 0x3523;
    private static final String YELLOW_SAVE = "/saves/Yellow/Yellow_Random_01.srm";

    @Test
    void isValidForAllFixtureSaves() throws Exception {
        List<Path> saves = SaveFixtures.findAll();
        assertFalse(saves.isEmpty(), "no save files found in /saves");
        for (Path save : saves) {
            assertTrue(validator.isValid(Files.readAllBytes(save)),
                save.getFileName().toString());
        }
    }
    @Test
    void rejectsCorruptedData() throws Exception {
        byte[] data = SaveFixtures.load("/saves/Yellow/Yellow_Random_01.srm");
        byte[] copyData = Arrays.copyOf(data, data.length);
        copyData[FIRST_CHECKSUMMED_BYTE] = (byte) (copyData[FIRST_CHECKSUMMED_BYTE] + 1);
        assertFalse(validator.isValid(copyData));
    }
    @Test
    void rejectsCorruptedCheckSum() throws Exception {
        byte[] data = SaveFixtures.load("/saves/Yellow/Yellow_Randomizer_01.srm");
        byte[] copyData = Arrays.copyOf(data, data.length);
        copyData[STORED_CHECKSUM_BYTE] = (byte) (copyData[STORED_CHECKSUM_BYTE] + 1);
        assertFalse(validator.isValid(copyData));
    }

    @Test
    void rejectsCorruptedLastChecksummedByte() throws Exception {
        // Edge case: the last byte inside the range must still be counted (off-by-one check).
        byte[] data = SaveFixtures.load(YELLOW_SAVE);
        data[LAST_CHECKSUMMED_BYTE] = (byte) (data[LAST_CHECKSUMMED_BYTE] + 1);
        assertFalse(validator.isValid(data));
    }

    @Test
    void ignoresBytesOutsideChecksumRange() throws Exception {
        // Edge case: the bytes just before and just after the range are not part of the sum.
        byte[] data = SaveFixtures.load(YELLOW_SAVE);
        int byteBeforeRange = FIRST_CHECKSUMMED_BYTE - 1;
        int byteAfterStoredChecksum = STORED_CHECKSUM_BYTE + 1;
        data[byteBeforeRange] = (byte) (data[byteBeforeRange] + 1);
        data[byteAfterStoredChecksum] = (byte) (data[byteAfterStoredChecksum] + 1);
        assertTrue(validator.isValid(data));
    }

    @Test
    void acceptsDataWithMatchingUpdatedCheckSum() throws Exception {
        // Raising one byte by 1 raises the sum by 1, so the flipped (~) checksum drops by 1.
        // Updating both together must keep the save valid.
        byte[] data = SaveFixtures.load(YELLOW_SAVE);
        data[FIRST_CHECKSUMMED_BYTE] = (byte) (data[FIRST_CHECKSUMMED_BYTE] + 1);
        data[STORED_CHECKSUM_BYTE] = (byte) (data[STORED_CHECKSUM_BYTE] - 1);
        assertTrue(validator.isValid(data));
    }

    @Test
    void cannotDetectChangesThatCancelOut() throws Exception {
        // Known limitation of a simple sum: +1 on one byte and -1 on another leave the
        // sum unchanged, so this corrupted save still passes.
        byte[] data = SaveFixtures.load(YELLOW_SAVE);
        data[FIRST_CHECKSUMMED_BYTE] = (byte) (data[FIRST_CHECKSUMMED_BYTE] + 1);
        data[LAST_CHECKSUMMED_BYTE] = (byte) (data[LAST_CHECKSUMMED_BYTE] - 1);
        assertTrue(validator.isValid(data));
    }
}
