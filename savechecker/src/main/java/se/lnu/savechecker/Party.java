package se.lnu.savechecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Party {
    private static final int PARTY_COUNT_OFFSET = 0x2F2C;
    private static final int MAX_PARTY_SIZE = 6;

    private final List<PartyPokemon> pokemon;

    Party(byte[] data) throws InvalidSaveFileException {
        int partyCount = data[PARTY_COUNT_OFFSET] & 0xFF;
        if (partyCount > MAX_PARTY_SIZE) {
            throw new InvalidSaveFileException("Party can hold at most " + MAX_PARTY_SIZE
                    + " Pokemon but the save says " + partyCount);
        }
        this.pokemon = new ArrayList<>();

        for (int slot = 0; slot < partyCount; slot++) {
            this.pokemon.add(new PartyPokemon(data, slot));
        }
    }

    public List<PartyPokemon> getPokemon() {
        return Collections.unmodifiableList(this.pokemon);
    }
}