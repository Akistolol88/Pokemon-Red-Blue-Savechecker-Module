package se.lnu.savechecker;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * The player's trainer info from a save file: their name and which gym badges
 * they have earned.
 *
 * <p>You don't create a {@code Trainer} yourself. Get one from
 * {@link SaveFile#getTrainer()} instead.
 */
public final class Trainer {

    /** Position of the byte that holds all eight badges, one bit per badge. */
    private static final int BADGE_OFFSET = 0x2602;

    /** Position where the player's name starts in the save file. */
    private static final int NAME_OFFSET = 0x2598;

    /**
     * How many bytes the save reserves for the name. This is the size of the
     * storage, not the longest possible name: in-game names are at most 7
     * characters, and the rest is the end marker plus leftover bytes.
     */
    private static final int NAME_MAX_BYTES = 11;

    /** The badges this trainer has earned. */
    private final Set<Badge> badges;

    /** The trainer's name, decoded into normal text. */
    private final String name;

    /**
     * Reads the trainer's name and badges from a save file's raw bytes.
     *
     * <p>Each badge is one bit in the badge byte, and a badge's
     * {@link Badge#ordinal()} is its bit number. For every badge, the byte is
     * shifted right so that badge's bit lands in the last position, and
     * {@code & 1} keeps only that bit: 1 means earned, 0 means not.
     *
     * @param data the full save file, already checked by {@link SaveFileValidator}
     */
    Trainer(byte[] data) {
        name = TextDecoder.decode(data, NAME_OFFSET, NAME_MAX_BYTES);

        badges = EnumSet.noneOf(Badge.class);
        int badgeByte = data[BADGE_OFFSET] & 0xFF;

        for (Badge badge : Badge.values()) {
            if (((badgeByte >> badge.ordinal()) & 1) == 1) {
                badges.add(badge);
            }
        }
    }

    /**
     * Checks whether the trainer has earned one specific badge.
     *
     * @param badge the badge to look for
     * @return {@code true} if the trainer has that badge
     */
    public boolean hasBadge(Badge badge) {
        return this.badges.contains(badge);
    }

    /**
     * Returns every badge the trainer has earned. The set can't be changed;
     * trying to add or remove a badge throws an exception.
     *
     * @return the earned badges, empty if the trainer has none
     */
    public Set<Badge> getBadges() {
        return Collections.unmodifiableSet(this.badges);
    }

    /**
     * Returns the trainer's name as normal text, for example {@code "RED"}.
     *
     * @return the trainer's name
     */
    public String getName() {
        return this.name;
    }

}
