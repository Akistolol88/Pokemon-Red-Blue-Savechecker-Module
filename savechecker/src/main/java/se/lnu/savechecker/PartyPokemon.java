package se.lnu.savechecker;

public final class PartyPokemon {
    private static final int PARTY_DATA_OFFSET = 0x2F34;
    private static final int POKEMON_SIZE_BYTES = 44;

    private static final int NICKNAME_OFFSET = 0x307E;
    private static final int NAME_MAX_BYTES = 11;

    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 100;

    private static final int CURRENT_HP_OFFSET = 1;
    private static final int OT_ID_OFFSET = 12;
    private static final int EXPERIENCE_OFFSET = 14;
    private static final int HP_EV_OFFSET = 17;
    private static final int ATTACK_EV_OFFSET = 19;
    private static final int DEFENSE_EV_OFFSET = 21;
    private static final int SPEED_EV_OFFSET = 23;
    private static final int SPECIAL_EV_OFFSET = 25;
    private static final int IV_OFFSET = 27;
    private static final int LEVEL_OFFSET = 33;
    private static final int MAX_HP_OFFSET = 34;
    private static final int ATTACK_OFFSET = 36;
    private static final int DEFENSE_OFFSET = 38;
    private static final int SPEED_OFFSET = 40;
    private static final int SPECIAL_OFFSET = 42;

    private final String nickname;
    private final int currentHp;
    private final int originalTrainerId;
    private final int experience;
    private final int hpEv;
    private final int attackEv;
    private final int defenseEv;
    private final int speedEv;
    private final int specialEv;
    private final int attackIv;
    private final int defenseIv;
    private final int speedIv;
    private final int specialIv;
    private final int level;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int speed;
    private final int special;

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

    public int getOriginalTrainerId() {
        return this.originalTrainerId;
    }

    public int getExperience() {
        return this.experience;
    }

    public int getHpEv() {
        return this.hpEv;
    }

    public int getAttackEv() {
        return this.attackEv;
    }

    public int getDefenseEv() {
        return this.defenseEv;
    }

    public int getSpeedEv() {
        return this.speedEv;
    }

    public int getSpecialEv() {
        return this.specialEv;
    }

    public int getAttackIv() {
        return this.attackIv;
    }

    public int getDefenseIv() {
        return this.defenseIv;
    }

    public int getSpeedIv() {
        return this.speedIv;
    }

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

    public int getAttack() {
        return this.attack;
    }

    public int getDefense() {
        return this.defense;
    }

    public int getSpeed() {
        return this.speed;
    }

    public int getSpecial() {
        return this.special;
    }

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
