package se.lnu.savechecker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Party {
    private static final int PARTY_COUNT_OFFSET = 0x2F2C;

    private final List<PartyPokemon> pokemon;

    Party(byte[] data) {
        int partyCount = data[PARTY_COUNT_OFFSET] & 0xFF;
        this.pokemon = new ArrayList<>();

        for (int slot = 0; slot < partyCount; slot++) {
            this.pokemon.add(new PartyPokemon(data, slot));
        }
    }

    public List<PartyPokemon> getPokemon() {
        return Collections.unmodifiableList(this.pokemon);
    }
}