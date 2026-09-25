package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class SaveFileTest {
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
}
