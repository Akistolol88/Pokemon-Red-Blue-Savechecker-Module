package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class PartyTest {

    private static final String RED_SAVE = "/saves/Red/Pokemon Red (UE) [S][!].sav";
    private static final String YELLOW_SAVE = "/saves/Yellow/Yellow_Random_01.srm";
    private static final String RANDOMIZER_SAVE = "/saves/Yellow/Yellow_Randomizer_01.srm";
    private static final String RANDOMIZER_SAVE_2 = "/saves/Yellow/Yellow_Randomizer_02.srm";

    private static final int PARTY_COUNT_OFFSET = 0x2F2C;
    private static final int MAX_PARTY_SIZE = 6;

    private static Party loadParty(String resourcePath) throws Exception {
        return new SaveFile(SaveFixtures.load(resourcePath)).getParty();
    }

    @Test
    void readsRedPartySize() throws Exception {
        assertEquals(2, loadParty(RED_SAVE).getPokemon().size());
    }

    @Test
    void readsRedFirstPokemonLevel() throws Exception {
        assertEquals(20, loadParty(RED_SAVE).getPokemon().get(0).getLevel());
    }

    @Test
    void readsRedFirstPokemon() throws Exception {
        PartyPokemon eevee = loadParty(RED_SAVE).getPokemon().get(0);
        assertEquals("EEVEE", eevee.getNickname());
        assertEquals(56, eevee.getCurrentHp());
        assertEquals(56, eevee.getMaxHp());
    }

    @Test
    void readsRedSecondPokemon() throws Exception {
        // A damaged Pokémon, so current and max HP differ.
        PartyPokemon squirtle = loadParty(RED_SAVE).getPokemon().get(1);
        assertEquals("SQUIRTLE", squirtle.getNickname());
        assertEquals(6, squirtle.getLevel());
        assertEquals(20, squirtle.getCurrentHp());
        assertEquals(22, squirtle.getMaxHp());
    }

    @Test
    void readsYellowPartySize() throws Exception {
        assertEquals(5, loadParty(YELLOW_SAVE).getPokemon().size());
    }

    @Test
    void readsYellowFirstPokemon() throws Exception {
        // HP above 99 checks that both bytes of the two-byte value are combined.
        PartyPokemon alakazam = loadParty(YELLOW_SAVE).getPokemon().get(0);
        assertEquals("ALAKAZAM", alakazam.getNickname());
        assertEquals(45, alakazam.getLevel());
        assertEquals(100, alakazam.getCurrentHp());
        assertEquals(116, alakazam.getMaxHp());
    }

    @Test
    void readsRandomizerPartySizes() throws Exception {
        assertEquals(5, loadParty(RANDOMIZER_SAVE).getPokemon().size());
        assertEquals(1, loadParty(RANDOMIZER_SAVE_2).getPokemon().size());
    }

    @Test
    void readsSinglePokemonParty() throws Exception {
        PartyPokemon onlyPokemon = loadParty(RANDOMIZER_SAVE_2).getPokemon().get(0);
        assertEquals(6, onlyPokemon.getLevel());
        assertEquals(18, onlyPokemon.getCurrentHp());
        assertEquals(22, onlyPokemon.getMaxHp());
    }

    @Test
    void readsEmptyParty() throws Exception {
        byte[] data = SaveFixtures.load(RED_SAVE);
        data[PARTY_COUNT_OFFSET] = 0;
        assertTrue(new Party(data).getPokemon().isEmpty());
    }

    @Test
    void rejectsTooManyPokemon() throws Exception {
        byte[] data = SaveFixtures.load(RED_SAVE);
        data[PARTY_COUNT_OFFSET] = MAX_PARTY_SIZE + 1;
        assertThrows(InvalidSaveFileException.class, () -> new Party(data));
    }

    @Test
    void acceptsFullParty() throws Exception {
        byte[] data = SaveFixtures.load(RED_SAVE);
        data[PARTY_COUNT_OFFSET] = MAX_PARTY_SIZE;
        assertEquals(MAX_PARTY_SIZE, new Party(data).getPokemon().size());
    }

    @Test
    void partyCannotBeModified() throws Exception {
        List<PartyPokemon> pokemon = loadParty(RED_SAVE).getPokemon();
        assertThrows(UnsupportedOperationException.class, () -> pokemon.remove(0));
    }

    @Test
    void allFixturePartiesAreValid() throws Exception {
        for (Path savePath : SaveFixtures.findAll()) {
            String save = savePath.getFileName().toString();
            List<PartyPokemon> pokemon = new SaveFile(Files.readAllBytes(savePath)).getParty().getPokemon();
            assertTrue(pokemon.size() <= MAX_PARTY_SIZE, save + " has too many Pokémon");

            for (PartyPokemon member : pokemon) {
                assertTrue(member.getLevel() >= 1 && member.getLevel() <= 100,
                        save + ": level out of range");
                assertTrue(member.getCurrentHp() <= member.getMaxHp(),
                        save + ": current HP above max HP");
            }
        }
    }
}
