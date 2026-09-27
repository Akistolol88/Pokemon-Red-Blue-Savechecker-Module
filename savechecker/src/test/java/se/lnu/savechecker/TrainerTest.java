package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.EnumSet;
import java.util.Set;

class TrainerTest {

    private static final String RED_SAVE = "/saves/Red/Pokemon Red (UE) [S][!].sav";
    private static final String YELLOW_SAVE = "/saves/Yellow/Yellow_Random_01.srm";
    private static final String RANDOMIZER_SAVE = "/saves/Yellow/Yellow_Randomizer_01.srm";

    private static Trainer loadTrainer(String resourcePath) throws Exception {
        return new SaveFile(SaveFixtures.load(resourcePath)).getTrainer();
    }

    @Test
    void readsRedTrainerName() throws Exception {
        assertEquals("A", loadTrainer(RED_SAVE).getName());
    }

    @Test
    void stopsNameAtEndMarker() throws Exception {
        // The bytes after the name spell "ASH"; the decoder must stop at 0x50 before them.
        assertEquals("YELLOW", loadTrainer(YELLOW_SAVE).getName());
    }

    @Test
    void readsRandomizerTrainerName() throws Exception {
        assertEquals("A", loadTrainer(RANDOMIZER_SAVE).getName());
    }

    @Test
    void readsNoBadges() throws Exception {
        assertTrue(loadTrainer(RED_SAVE).getBadges().isEmpty());
    }

    @Test
    void readsAllEightBadges() throws Exception {
        assertEquals(EnumSet.allOf(Badge.class), loadTrainer(YELLOW_SAVE).getBadges());
    }

    @Test
    void readsFirstThreeBadges() throws Exception {
        assertEquals(EnumSet.of(Badge.BOULDER, Badge.CASCADE, Badge.THUNDER),
                loadTrainer(RANDOMIZER_SAVE).getBadges());
    }

    @Test
    void hasBadgeMatchesEarnedBadges() throws Exception {
        Trainer trainer = loadTrainer(RANDOMIZER_SAVE);
        assertTrue(trainer.hasBadge(Badge.THUNDER));
        assertFalse(trainer.hasBadge(Badge.RAINBOW));
    }

    @Test
    void readsFirstAndLastBadgeBits() throws Exception {
        // Edge case: bit 0 (BOULDER) and bit 7 (EARTH) are the easiest to get off by one.
        Trainer trainer = loadTrainer(YELLOW_SAVE);
        assertTrue(trainer.hasBadge(Badge.BOULDER));
        assertTrue(trainer.hasBadge(Badge.EARTH));
    }

    @Test
    void badgesCannotBeModified() throws Exception {
        Set<Badge> badges = loadTrainer(RED_SAVE).getBadges();
        assertThrows(UnsupportedOperationException.class, () -> {
            badges.add(Badge.EARTH);
        });
    }
}
