package se.lnu.savechecker;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Which Pokémon the player has seen and caught, as recorded in the Pokédex.
 *
 * <p>Pokémon are identified by their National Pokédex number, from 1
 * (Bulbasaur) to 151 (Mew). Catching a Pokémon also marks it as seen, so every
 * caught Pokémon is counted in the seen numbers too, the same way the in-game
 * Pokédex shows "SEEN" and "OWN".
 *
 * <p>You don't create a {@code PokedexStatus} yourself. Get one from
 * {@link SaveFile#getPokedexStatus()} instead.
 */
public final class PokedexStatus {
    /** The highest Pokédex number in Generation I (Mew). */
    private static final int LAST_POKEDEX_ENTRY = 151;

    /** Where the 19-byte "caught" list starts in the save file. */
    private static final int POKEDEX_CAUGHT_OFFSET = 0x25A3;

    /** Where the 19-byte "seen" list starts, right after the caught list. */
    private static final int POKEDEX_SEEN_OFFSET = 0x25B6;

    /** Each byte holds the flags of 8 Pokémon, one bit each. */
    private static final int BITS_PER_BYTE = 8;

    /** Pokédex numbers of every Pokémon the player has caught. */
    private final Set<Integer> caughtPokemon;

    /** Pokédex numbers of every Pokémon the player has seen (caught ones included). */
    private final Set<Integer> seenPokemon;

    /**
     * Reads both Pokédex lists from a save file's raw bytes.
     *
     * @param data the full save file, already checked by {@link SaveFileValidator}
     */
    PokedexStatus(byte[] data) {
        this.caughtPokemon = readDexFlags(data, POKEDEX_CAUGHT_OFFSET);
        this.seenPokemon = readDexFlags(data, POKEDEX_SEEN_OFFSET);
    }

    /**
     * Checks whether the player has seen a Pokémon.
     *
     * @param dexNumber the Pokémon's National Pokédex number, 1 to 151
     * @return {@code true} if it has been seen (or caught); {@code false} for
     *     numbers outside 1 to 151
     */
    public boolean hasSeen(int dexNumber) {
        return this.seenPokemon.contains(dexNumber);
    }

    /**
     * Checks whether the player has caught a Pokémon.
     *
     * @param dexNumber the Pokémon's National Pokédex number, 1 to 151
     * @return {@code true} if it has been caught; {@code false} for numbers
     *     outside 1 to 151
     */
    public boolean hasCaught(int dexNumber) {
        return this.caughtPokemon.contains(dexNumber);
    }

    /**
     * Returns how many different Pokémon the player has seen, caught ones included.
     *
     * @return the "SEEN" number from the in-game Pokédex, 0 to 151
     */
    public int getSeenCount() {
        return this.seenPokemon.size();
    }

    /**
     * Returns how many different Pokémon the player has caught.
     *
     * @return the "OWN" number from the in-game Pokédex, 0 to 151
     */
    public int getCaughtCount() {
        return this.caughtPokemon.size();
    }

    /**
     * Returns the Pokédex numbers of every Pokémon the player has seen. The set
     * can't be changed; trying to add or remove a number throws an exception.
     *
     * @return the seen Pokédex numbers, caught ones included
     */
    public Set<Integer> getSeenSpecies() {
        return Collections.unmodifiableSet(this.seenPokemon);
    }

    /**
     * Returns the Pokédex numbers of every Pokémon the player has caught. The
     * set can't be changed; trying to add or remove a number throws an exception.
     *
     * @return the caught Pokédex numbers
     */
    public Set<Integer> getCaughtSpecies() {
        return Collections.unmodifiableSet(this.caughtPokemon);
    }

    /**
     * Reads one 19-byte Pokédex list and returns the numbers whose bit is on.
     *
     * <p>Pokémon number {@code n} is stored in byte {@code (n - 1) / 8} of the
     * list, at bit {@code (n - 1) % 8}. The {@code - 1} is there because the
     * Pokédex starts at 1 but bits start at 0. For example, Pikachu (#25) is
     * byte 3, bit 0.
     *
     * @param data the full save file
     * @param offset where the list starts ({@link #POKEDEX_CAUGHT_OFFSET} or
     *     {@link #POKEDEX_SEEN_OFFSET})
     * @return the Pokédex numbers that are marked in this list
     */
    private static Set<Integer> readDexFlags(byte[] data, int offset) {
        Set<Integer> dexFlags = new HashSet<>();
        for (int dexNumber = 1; dexNumber <= LAST_POKEDEX_ENTRY; dexNumber++) {
            int byteIndex = ((dexNumber - 1) / BITS_PER_BYTE);
            int bitIndex = ((dexNumber - 1) % BITS_PER_BYTE);
            if (ByteReader.isBitSet(data, offset + byteIndex, bitIndex)) {
                dexFlags.add(dexNumber);
            }
        }
        return dexFlags;
    }
}
