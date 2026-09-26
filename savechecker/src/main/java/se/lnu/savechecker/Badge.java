package se.lnu.savechecker;

/**
 * The eight gym badges a trainer can earn in Pokémon Red, Blue and Yellow.
 *
 * <p>The save file stores all eight badges in a single byte, one bit per badge.
 * The badges are listed here in the same order as those bits: {@code BOULDER} is
 * bit 0, {@code CASCADE} is bit 1, and so on up to {@code EARTH} at bit 7. This
 * means a badge's {@link #ordinal()} is also its bit number in the save file,
 * so <strong>do not reorder these values</strong>.
 */
public enum Badge {
    /** Pewter City gym, leader Brock (bit 0). */
    BOULDER,
    /** Cerulean City gym, leader Misty (bit 1). */
    CASCADE,
    /** Vermilion City gym, leader Lt. Surge (bit 2). */
    THUNDER,
    /** Celadon City gym, leader Erika (bit 3). */
    RAINBOW,
    /** Fuchsia City gym, leader Koga (bit 4). */
    SOUL,
    /** Saffron City gym, leader Sabrina (bit 5). */
    MARSH,
    /** Cinnabar Island gym, leader Blaine (bit 6). */
    VOLCANO,
    /** Viridian City gym, leader Giovanni (bit 7). */
    EARTH
}
