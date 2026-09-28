package se.lnu.savechecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Pokémon the player carries with them: from zero up to six.
 *
 * <p>You don't create a {@code Party} yourself. Get one from
 * {@link SaveFile#getParty()} instead.
 */
public final class Party {

    /** Position of the byte that says how many Pokémon are in the party. */
    private static final int PARTY_COUNT_OFFSET = 0x2F2C;

    /** The most Pokémon a party can hold in the game. */
    private static final int MAX_PARTY_SIZE = 6;

    /** The party's Pokémon, in the same order as in the game's menu. */
    private final List<PartyPokemon> pokemon;

    /**
     * Reads the party size, then reads one {@link PartyPokemon} for each slot
     * that is in use.
     *
     * @param data the full save file as raw bytes, already checked by
     *     {@link SaveFileValidator}
     * @throws InvalidSaveFileException if the save says the party has more
     *     than six Pokémon, or if one of the Pokémon has impossible values
     */
    Party(byte[] data) throws InvalidSaveFileException {
        int partyCount = new ByteReader(data).readByte(PARTY_COUNT_OFFSET);
        if (partyCount > MAX_PARTY_SIZE) {
            throw new InvalidSaveFileException("Party can hold at most " + MAX_PARTY_SIZE
                    + " Pokemon but the save says " + partyCount);
        }
        this.pokemon = new ArrayList<>();

        for (int slot = 0; slot < partyCount; slot++) {
            this.pokemon.add(new PartyPokemon(data, slot));
        }
    }

    /**
     * Returns the Pokémon in the party, first slot first.
     *
     * <p>The list can't be changed: trying to add or remove a Pokémon throws
     * an {@link UnsupportedOperationException}.
     *
     * @return the party's Pokémon, an empty list if the party is empty
     */
    public List<PartyPokemon> getPokemon() {
        return Collections.unmodifiableList(this.pokemon);
    }
}
