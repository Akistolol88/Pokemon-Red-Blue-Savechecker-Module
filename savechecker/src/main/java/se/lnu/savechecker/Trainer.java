package se.lnu.savechecker;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * The player's trainer info from a save file: their name, their trainer ID and
 * which gym badges they have earned.
 *
 * <p>You don't create a {@code Trainer} yourself. Get one from
 * {@link SaveFile#getTrainer()} instead.
 */
public final class Trainer {

    /** Position of the byte that holds all eight badges, one bit per badge. */
    private static final int BADGE_OFFSET = 0x2602;

    /** Position where the player's name starts in the save file. */
    private static final int NAME_OFFSET = 0x2598;

    /** Position where the player's trainer ID starts (2 bytes). */
    private static final int ID_OFFSET = 0x2605;

    /** The badges this trainer has earned. */
    private final Set<Badge> badges;

    /** The trainer's name, decoded into normal text. */
    private final String name;

    /** The trainer's ID number, shown on the trainer card. */
    private final int id;

    /**
     * Reads the trainer's name, ID and badges from a save file's raw bytes.
     *
     * <p>Each badge is one bit in the badge byte, and a badge's
     * {@link Badge#ordinal()} is its bit number: bit on means earned.
     *
     * @param reader reads the bytes of the save file, already checked by
     *     {@link SaveFileValidator}
     */
    Trainer(ByteReader reader) {
        this.name = new TextDecoder(reader).decode(NAME_OFFSET, TextDecoder.NAME_MAX_BYTES);
        this.id = reader.readTwoBytes(ID_OFFSET);
        this.badges = EnumSet.noneOf(Badge.class);
        for (Badge badge : Badge.values()) {
            if (reader.isBitSet(BADGE_OFFSET, badge.ordinal())) {
                this.badges.add(badge);
            }
        }
    }

    /**
     * Checks whether the trainer has earned one specific badge.
     *
     * @param badge the badge to look for
     * @return {@code true} if the trainer has that badge
     * @throws NullPointerException if {@code badge} is {@code null}
     */
    public boolean hasBadge(Badge badge) {
        Objects.requireNonNull(badge, "badge must not be null");
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

    /**
     * Returns the trainer's ID number, as shown on the in-game trainer card.
     * Every Pokémon the player catches gets this ID as its original trainer
     * ID, so a party Pokémon whose {@link PartyPokemon#getOriginalTrainerId()}
     * is different was traded from another game.
     *
     * @return the trainer's ID, 0 to 65535
     */
    public int getId() {
        return this.id;
    }

}
