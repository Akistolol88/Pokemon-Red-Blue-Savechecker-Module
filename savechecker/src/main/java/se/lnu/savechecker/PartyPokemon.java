package se.lnu.savechecker;

/**
 * One Pokémon in the player's party, with its nickname, level, HP, stats,
 * EVs and IVs.
 *
 * <p>You don't create a {@code PartyPokemon} yourself. Get them from
 * {@link Party#getPokemon()} instead.
 *
 * <p>Each party Pokémon takes up 44 bytes in the save file. The offsets from
 * {@code CURRENT_HP_OFFSET} onward count from the start of those 44 bytes, and
 * the fields are listed in the same order as they appear in the save file.
 */
public final class PartyPokemon {

    /** Position where the first party Pokémon's 44 bytes start. */
    private static final int PARTY_DATA_OFFSET = 0x2F34;

    /** How many bytes each party Pokémon takes up. */
    private static final int POKEMON_SIZE_BYTES = 44;

    /**
     * Position where the first party Pokémon's nickname starts. Nicknames are
     * stored in their own list, away from the rest of the Pokémon's data.
     */
    private static final int NICKNAME_OFFSET = 0x307E;

    /** How many bytes the save reserves for each nickname. */
    private static final int NAME_MAX_BYTES = 11;

    /** The lowest level a Pokémon can have. */
    private static final int MIN_LEVEL = 1;

    /** The highest level a Pokémon can have. */
    private static final int MAX_LEVEL = 100;

    /** Where the current HP is (2 bytes). */
    private static final int CURRENT_HP_OFFSET = 1;

    /** Where the original trainer's ID is (2 bytes). */
    private static final int OT_ID_OFFSET = 12;

    /** Where the experience points are (3 bytes). */
    private static final int EXPERIENCE_OFFSET = 14;

    /** Where the HP EV is (2 bytes). */
    private static final int HP_EV_OFFSET = 17;

    /** Where the Attack EV is (2 bytes). */
    private static final int ATTACK_EV_OFFSET = 19;

    /** Where the Defense EV is (2 bytes). */
    private static final int DEFENSE_EV_OFFSET = 21;

    /** Where the Speed EV is (2 bytes). */
    private static final int SPEED_EV_OFFSET = 23;

    /** Where the Special EV is (2 bytes). */
    private static final int SPECIAL_EV_OFFSET = 25;

    /**
     * Where the IVs are. All four fit in 2 bytes, one half-byte each: Attack
     * and Defense in the first byte, Speed and Special in the second.
     */
    private static final int IV_OFFSET = 27;

    /** Where the level is (1 byte). */
    private static final int LEVEL_OFFSET = 33;

    /** Where the max HP is (2 bytes). */
    private static final int MAX_HP_OFFSET = 34;

    /** Where the Attack stat is (2 bytes). */
    private static final int ATTACK_OFFSET = 36;

    /** Where the Defense stat is (2 bytes). */
    private static final int DEFENSE_OFFSET = 38;

    /** Where the Speed stat is (2 bytes). */
    private static final int SPEED_OFFSET = 40;

    /** Where the Special stat is (2 bytes). */
    private static final int SPECIAL_OFFSET = 42;

    /** The nickname, decoded into normal text. */
    private final String nickname;

    /** The HP the Pokémon has right now. */
    private final int currentHp;

    /** The ID of the trainer who first caught this Pokémon. */
    private final int originalTrainerId;

    /** The total experience points the Pokémon has earned. */
    private final int experience;

    /** The HP EV. */
    private final int hpEv;

    /** The Attack EV. */
    private final int attackEv;

    /** The Defense EV. */
    private final int defenseEv;

    /** The Speed EV. */
    private final int speedEv;

    /** The Special EV. */
    private final int specialEv;

    /** The Attack IV. */
    private final int attackIv;

    /** The Defense IV. */
    private final int defenseIv;

    /** The Speed IV. */
    private final int speedIv;

    /** The Special IV. */
    private final int specialIv;

    /** The level. */
    private final int level;

    /** The HP when fully healed. */
    private final int maxHp;

    /** The Attack stat. */
    private final int attack;

    /** The Defense stat. */
    private final int defense;

    /** The Speed stat. */
    private final int speed;

    /** The Special stat. */
    private final int special;

    /**
     * Reads one party Pokémon from a save file's raw bytes.
     *
     * <p>{@code start} is where this Pokémon's 44 bytes begin: the first
     * Pokémon's position plus 44 for every slot before it. The nickname is
     * found the same way, but in the nickname list with 11 bytes per slot.
     *
     * @param data the full save file as raw bytes
     * @param slot which party slot to read, 0 for the first Pokémon
     * @throws InvalidSaveFileException if the level or HP values are
     *     impossible, see {@link #checkValues(int)}
     */
    PartyPokemon(byte[] data, int slot) throws InvalidSaveFileException {
        int start = PARTY_DATA_OFFSET + (slot * POKEMON_SIZE_BYTES);
        int nicknameStart = NICKNAME_OFFSET + (slot * NAME_MAX_BYTES);
        ByteReader reader = new ByteReader(data);

        this.nickname = new TextDecoder(data).decode(nicknameStart, NAME_MAX_BYTES);
        this.currentHp = reader.readTwoBytes(start + CURRENT_HP_OFFSET);
        this.originalTrainerId = reader.readTwoBytes(start + OT_ID_OFFSET);
        this.experience = reader.readThreeBytes(start + EXPERIENCE_OFFSET);
        this.hpEv = reader.readTwoBytes(start + HP_EV_OFFSET);
        this.attackEv = reader.readTwoBytes(start + ATTACK_EV_OFFSET);
        this.defenseEv = reader.readTwoBytes(start + DEFENSE_EV_OFFSET);
        this.speedEv = reader.readTwoBytes(start + SPEED_EV_OFFSET);
        this.specialEv = reader.readTwoBytes(start + SPECIAL_EV_OFFSET);
        this.attackIv = reader.readHighHalf(start + IV_OFFSET);
        this.defenseIv = reader.readLowHalf(start + IV_OFFSET);
        this.speedIv = reader.readHighHalf(start + IV_OFFSET + 1);
        this.specialIv = reader.readLowHalf(start + IV_OFFSET + 1);
        this.level = reader.readByte(start + LEVEL_OFFSET);
        this.maxHp = reader.readTwoBytes(start + MAX_HP_OFFSET);
        this.attack = reader.readTwoBytes(start + ATTACK_OFFSET);
        this.defense = reader.readTwoBytes(start + DEFENSE_OFFSET);
        this.speed = reader.readTwoBytes(start + SPEED_OFFSET);
        this.special = reader.readTwoBytes(start + SPECIAL_OFFSET);

        checkValues(slot);
    }

    /**
     * Returns the Pokémon's nickname, or its species name if it was never
     * given one (for example {@code "SQUIRTLE"}).
     *
     * @return the nickname as normal text
     */
    public String getNickname() {
        return this.nickname;
    }

    /**
     * Returns the Pokémon's HP right now, which can be lower than its max HP
     * after a battle.
     *
     * @return the current HP, 0 if the Pokémon has fainted
     */
    public int getCurrentHp() {
        return this.currentHp;
    }

    /**
     * Returns the ID of the trainer who first caught this Pokémon. If it is
     * different from the player's own ID, the Pokémon was traded.
     *
     * @return the original trainer's ID, 0 to 65535
     */
    public int getOriginalTrainerId() {
        return this.originalTrainerId;
    }

    /**
     * Returns the total experience points the Pokémon has earned.
     *
     * @return the experience points, 0 to 16777215
     */
    public int getExperience() {
        return this.experience;
    }

    /**
     * Returns the HP EV (effort value). EVs grow when the Pokémon wins
     * battles and make its stats a bit higher.
     *
     * @return the HP EV, 0 to 65535
     */
    public int getHpEv() {
        return this.hpEv;
    }

    /**
     * Returns the Attack EV (effort value).
     *
     * @return the Attack EV, 0 to 65535
     */
    public int getAttackEv() {
        return this.attackEv;
    }

    /**
     * Returns the Defense EV (effort value).
     *
     * @return the Defense EV, 0 to 65535
     */
    public int getDefenseEv() {
        return this.defenseEv;
    }

    /**
     * Returns the Speed EV (effort value).
     *
     * @return the Speed EV, 0 to 65535
     */
    public int getSpeedEv() {
        return this.speedEv;
    }

    /**
     * Returns the Special EV (effort value).
     *
     * @return the Special EV, 0 to 65535
     */
    public int getSpecialEv() {
        return this.specialEv;
    }

    /**
     * Returns the Attack IV (individual value). IVs are set when the Pokémon
     * is caught and never change, so two Pokémon of the same species can have
     * different stats.
     *
     * @return the Attack IV, 0 to 15
     */
    public int getAttackIv() {
        return this.attackIv;
    }

    /**
     * Returns the Defense IV (individual value).
     *
     * @return the Defense IV, 0 to 15
     */
    public int getDefenseIv() {
        return this.defenseIv;
    }

    /**
     * Returns the Speed IV (individual value).
     *
     * @return the Speed IV, 0 to 15
     */
    public int getSpeedIv() {
        return this.speedIv;
    }

    /**
     * Returns the Special IV (individual value).
     *
     * @return the Special IV, 0 to 15
     */
    public int getSpecialIv() {
        return this.specialIv;
    }

    /**
     * Returns the Pokémon's level.
     *
     * @return the level, 1 to 100
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Returns the Pokémon's HP when fully healed.
     *
     * @return the max HP
     */
    public int getMaxHp() {
        return this.maxHp;
    }

    /**
     * Returns the Pokémon's Attack stat, as shown on its summary screen.
     *
     * @return the Attack stat
     */
    public int getAttack() {
        return this.attack;
    }

    /**
     * Returns the Pokémon's Defense stat, as shown on its summary screen.
     *
     * @return the Defense stat
     */
    public int getDefense() {
        return this.defense;
    }

    /**
     * Returns the Pokémon's Speed stat, as shown on its summary screen.
     *
     * @return the Speed stat
     */
    public int getSpeed() {
        return this.speed;
    }

    /**
     * Returns the Pokémon's Special stat, as shown on its summary screen. In
     * the first-generation games one stat covers both special attack and
     * special defense.
     *
     * @return the Special stat
     */
    public int getSpecial() {
        return this.special;
    }

    /**
     * Makes sure the values just read are possible in the real game. If they
     * are not, the bytes are probably not a real party Pokémon.
     *
     * @param slot the party slot, used to say which Pokémon is wrong
     * @throws InvalidSaveFileException if the level is outside 1 to 100, or
     *     the current HP is higher than the max HP
     */
    private void checkValues(int slot) throws InvalidSaveFileException {
        if (this.level < MIN_LEVEL || this.level > MAX_LEVEL) {
            throw new InvalidSaveFileException("Party Pokemon " + (slot + 1) + " has level "
                    + this.level + ", but levels go from " + MIN_LEVEL + " to " + MAX_LEVEL);
        }
        if (this.currentHp > this.maxHp) {
            throw new InvalidSaveFileException("Party Pokemon " + (slot + 1) + " has "
                    + this.currentHp + " HP, more than its max HP of " + this.maxHp);
        }
    }
}
