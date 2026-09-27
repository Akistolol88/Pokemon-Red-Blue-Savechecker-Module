package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

class PokedexStatusTest {

    private static final String RED_SAVE = "/saves/Red/Pokemon Red (UE) [S][!].sav";
    private static final String YELLOW_SAVE = "/saves/Yellow/Yellow_Random_01.srm";
    private static final String RANDOMIZER_01_SAVE = "/saves/Yellow/Yellow_Randomizer_01.srm";
    private static final String RANDOMIZER_02_SAVE = "/saves/Yellow/Yellow_Randomizer_02.srm";

    private static PokedexStatus loadPokedex(String resourcePath) throws Exception {
        return new SaveFile(SaveFixtures.load(resourcePath)).getPokedexStatus();
    }

    @Test
    void readsRedCounts() throws Exception {
        PokedexStatus pokedex = loadPokedex(RED_SAVE);
        assertEquals(5, pokedex.getCaughtCount());
        assertEquals(7, pokedex.getSeenCount());
    }

    @Test
    void readsYellowCounts() throws Exception {
        PokedexStatus pokedex = loadPokedex(YELLOW_SAVE);
        assertEquals(7, pokedex.getCaughtCount());
        assertEquals(100, pokedex.getSeenCount());
    }

    @Test
    void readsRandomizerCounts() throws Exception {
        PokedexStatus pokedex = loadPokedex(RANDOMIZER_01_SAVE);
        assertEquals(5, pokedex.getCaughtCount());
        assertEquals(76, pokedex.getSeenCount());
    }

    @Test
    void readsExactRedSpecies() throws Exception {
        PokedexStatus pokedex = loadPokedex(RED_SAVE);
        assertEquals(Set.of(7, 16, 23, 133, 149), pokedex.getCaughtSpecies());
        assertEquals(Set.of(1, 7, 16, 19, 23, 133, 149), pokedex.getSeenSpecies());
    }

    @Test
    void readsExactRandomizer02Species() throws Exception {
        PokedexStatus pokedex = loadPokedex(RANDOMIZER_02_SAVE);
        assertEquals(Set.of(11), pokedex.getCaughtSpecies());
        assertEquals(Set.of(11, 24, 40, 62, 77, 96, 117, 121), pokedex.getSeenSpecies());
    }

    @Test
    void seenButNotCaught() throws Exception {
        // Red's rival picks Bulbasaur (#1) when the player picks Squirtle (#7).
        PokedexStatus pokedex = loadPokedex(RED_SAVE);
        assertTrue(pokedex.hasCaught(7));
        assertTrue(pokedex.hasSeen(1));
        assertFalse(pokedex.hasCaught(1));
    }

    @Test
    void neitherSeenNorCaught() throws Exception {
        PokedexStatus pokedex = loadPokedex(RED_SAVE);
        assertFalse(pokedex.hasSeen(25));
        assertFalse(pokedex.hasCaught(25));
    }

    @Test
    void readsLastBitOfFirstByte() throws Exception {
        // Edge case: #8 is byte 0, bit 7, and #9 starts byte 1. Off-by-one errors show up here.
        PokedexStatus pokedex = loadPokedex(YELLOW_SAVE);
        assertTrue(pokedex.hasCaught(8));
        assertFalse(pokedex.hasCaught(9));
    }

    @Test
    void readsFirstAndLastPokedexNumbers() throws Exception {
        // Edge case: #1 is the very first bit, #151 (Mew) is byte 18, bit 6 - the last one used.
        assertTrue(loadPokedex(YELLOW_SAVE).hasSeen(1));
        PokedexStatus randomizer = loadPokedex(RANDOMIZER_01_SAVE);
        assertFalse(randomizer.hasSeen(1));
        assertTrue(randomizer.hasSeen(151));
    }

    @Test
    void numbersOutsidePokedexAreFalse() throws Exception {
        // Edge case: 0, 152 and negative numbers are not Pokémon, so they are never seen or caught.
        PokedexStatus pokedex = loadPokedex(RANDOMIZER_01_SAVE);
        assertFalse(pokedex.hasSeen(0));
        assertFalse(pokedex.hasSeen(152));
        assertFalse(pokedex.hasSeen(-1));
        assertFalse(pokedex.hasCaught(0));
        assertFalse(pokedex.hasCaught(152));
    }

    @Test
    void everyCaughtPokemonIsAlsoSeen() throws Exception {
        // The game marks a Pokémon as seen when it is caught, so this must hold in every save.
        List<Path> saves = SaveFixtures.findAll();
        assertFalse(saves.isEmpty(), "no save files found in /saves");
        for (Path save : saves) {
            PokedexStatus pokedex = new SaveFile(Files.readAllBytes(save)).getPokedexStatus();
            assertTrue(pokedex.getSeenSpecies().containsAll(pokedex.getCaughtSpecies()),
                save.getFileName().toString());
        }
    }

    @Test
    void speciesSetsCannotBeModified() throws Exception {
        PokedexStatus pokedex = loadPokedex(RED_SAVE);
        assertThrows(UnsupportedOperationException.class, () -> {
            pokedex.getCaughtSpecies().add(25);
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            pokedex.getSeenSpecies().add(25);
        });
    }
}
