package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

class CheckSumValidatorTest {

    private static final int FIRST_CHECKSUMMED_BYTE = 0x2598;
    private static final int STORED_CHECKSUM_BYTE = 0x3523;

    private byte[] loadSave(String resourcePath) throws Exception {
        Path saveFile = Path.of(getClass().getResource(resourcePath).toURI());
        return Files.readAllBytes(saveFile);
    }

    private List<Path> findAllSaves() throws Exception {
        Path savesFolder = Path.of(getClass().getResource("/saves").toURI());
        try (Stream<Path> files = Files.walk(savesFolder)) {
            return files
                .filter(file -> file.toString().endsWith(".sav")
                    || file.toString().endsWith(".srm"))
                .toList();
        }
    }

    @Test
    void isValidForAllFixtureSaves() throws Exception {
        List<Path> saves = findAllSaves();
        assertFalse(saves.isEmpty(), "no save files found in /saves");
        for (Path save : saves) {
            assertTrue(CheckSumValidator.isValid(Files.readAllBytes(save)),
                save.getFileName().toString());
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