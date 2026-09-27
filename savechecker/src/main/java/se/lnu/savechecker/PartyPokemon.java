package se.lnu.savechecker;

public final class PartyPokemon {
    private static final int HIGH_BYTE_MULTIPLIER = 256;

    private static final int PARTY_DATA_OFFSET = 0x2F34;
    private static final int POKEMON_SIZE_BYTES = 44;

    private static final int NICKNAME_OFFSET = 0x307E;
    private static final int NAME_MAX_BYTES = 11;

    private static final int CURRENT_HP_OFFSET = 1;
    private static final int MAX_HP_OFFSET = 34;
    private static final int LEVEL_OFFSET = 33;

    private final String nickname;
    private final int level;
    private final int currentHp;
    private final int maxHp;

    PartyPokemon(byte[] data, int slot) {
        int start = PARTY_DATA_OFFSET + (slot * POKEMON_SIZE_BYTES);
        int nicknameStart = NICKNAME_OFFSET + (slot * NAME_MAX_BYTES);

        this.currentHp = readTwoBytes(data, start + CURRENT_HP_OFFSET);
        this.level = data[start + LEVEL_OFFSET] & 0xFF;
        this.maxHp = readTwoBytes(data, start + MAX_HP_OFFSET);
        this.nickname = TextDecoder.decode(data, nicknameStart, NAME_MAX_BYTES);
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
     * Returns the Pokémon's level.
     *
     * @return the level, 1 to 100
     */
    public int getLevel() {
        return this.level;
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
     * Returns the Pokémon's HP when fully healed.
     *
     * @return the max HP
     */
    public int getMaxHp() {
        return this.maxHp;
    }

    private static int readTwoBytes(byte[] data, int offset) {
        return (data[offset] & 0xFF) * HIGH_BYTE_MULTIPLIER + (data[offset + 1] & 0xFF);
    }
}