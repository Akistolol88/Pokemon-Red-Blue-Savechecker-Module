package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Arrays;

class SaveFileTest {

    private static final int STORED_CHECKSUM_BYTE = 0x3523;
    private static final int EXPECTED_SAVE_SIZE = 32_768;

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

    @Test
    void rejectsNullData() {
        assertThrows(InvalidSaveFileException.class, () -> {
            new SaveFile(null);
        });
    }

    @Test
    void rejectsEmptyData() {
        assertThrows(InvalidSaveFileException.class, () -> {
            new SaveFile(new byte[0]);
        });
    }

    @Test
    void rejectsOneByteTooShort() {
        // Edge case: just below the exact size must fail.
        assertThrows(InvalidSaveFileException.class, () -> {
            new SaveFile(new byte[EXPECTED_SAVE_SIZE - 1]);
        });
    }

    @Test
    void rejectsOneByteTooLong() {
        // Edge case: just above the exact size must fail too.
        assertThrows(InvalidSaveFileException.class, () -> {
            new SaveFile(new byte[EXPECTED_SAVE_SIZE + 1]);
        });
    }

    @Test
    void rejectsCorrectSizeButAllZeros() {
        // Right size, but not a real save: an all-zero sum gives checksum 0xFF, not the stored 0x00.
        assertThrows(InvalidSaveFileException.class, () -> {
            new SaveFile(new byte[EXPECTED_SAVE_SIZE]);
        });
    }

    @Test
    void wrongSizeMessageShowsActualSize() {
        InvalidSaveFileException exception = assertThrows(InvalidSaveFileException.class, () -> {
            new SaveFile(new byte[100]);
        });
        assertTrue(exception.getMessage().contains("100"), exception.getMessage());
    }

    @Test
    void checksumMismatchMessageNamesChecksum() throws Exception {
        // Same size as a real save, so the message must blame the checksum, not the size.
        byte[] data = SaveFixtures.load("/saves/Yellow/Yellow_Random_01.srm");
        data[STORED_CHECKSUM_BYTE] = (byte) (data[STORED_CHECKSUM_BYTE] + 1);
        InvalidSaveFileException exception = assertThrows(InvalidSaveFileException.class, () -> {
            new SaveFile(data);
        });
        assertTrue(exception.getMessage().contains("Checksum"), exception.getMessage());
    }

    @Test
    void changingArrayAfterLoadingDoesNotChangeSave() throws Exception {
        // Everything is read in the constructor, so the caller can't change a SaveFile afterwards
        // by editing the array they passed in.
        byte[] data = SaveFixtures.load("/saves/Red/Pokemon Red (UE) [S][!].sav");
        SaveFile save = new SaveFile(data);
        Arrays.fill(data, (byte) 0);

        assertEquals("A", save.getTrainer().getName());
        assertEquals(2, save.getParty().getPokemon().size());
        assertEquals(20, save.getParty().getPokemon().get(0).getLevel());
        assertEquals(7, save.getPokedexStatus().getSeenCount());
    }
}
