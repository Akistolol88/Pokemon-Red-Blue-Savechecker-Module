package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Arrays;

class SaveFileTest {

    private static final int STORED_CHECKSUM_BYTE = 0x3523;

    @Test
    void rejectsWrongSize() {
        assertThrows(InvalidSaveFileException.class, () -> {
            new SaveFile(new byte[100]);
        });
    }

    @Test
    void acceptsAllFixtureSaves() throws Exception {
        List<Path> saves = SaveFixtures.findAll();
        assertFalse(saves.isEmpty(), "no save files found in /saves");
        for (Path save : saves) {
            byte[] data = Files.readAllBytes(save);
            assertDoesNotThrow(() -> {
                new SaveFile(data);
            }, save.getFileName().toString());
        }
    }
    
    @Test
    void rejectsCorruptedCheckSum() throws Exception {
        byte[] data = SaveFixtures.load("/saves/Yellow/Yellow_Randomizer_02.srm");
        byte[] copyData = Arrays.copyOf(data, data.length);
        copyData[STORED_CHECKSUM_BYTE] = (byte) (copyData[STORED_CHECKSUM_BYTE] + 1);
        assertThrows(InvalidSaveFileException.class, () -> {
            new SaveFile(copyData);
        });
    }
}
