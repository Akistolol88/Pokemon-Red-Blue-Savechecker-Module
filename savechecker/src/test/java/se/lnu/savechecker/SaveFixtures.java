package se.lnu.savechecker;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

class SaveFixtures {

    static byte[] load(String resourcePath) throws Exception {
        Path saveFile = Path.of(SaveFixtures.class.getResource(resourcePath).toURI());
        return Files.readAllBytes(saveFile);
    }

    static List<Path> findAll() throws Exception {
        Path savesFolder = Path.of(SaveFixtures.class.getResource("/saves").toURI());
        try (Stream<Path> files = Files.walk(savesFolder)) {
            return files
                .filter(file -> file.toString().endsWith(".sav")
                    || file.toString().endsWith(".srm"))
                .toList();
        }
    }
}
