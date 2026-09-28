package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    private static final int FIRST_POKEMON_START = 0x2F34;
    private static final int POKEMON_SIZE_BYTES = 44;
    private static final int FIRST_POKEMON_CURRENT_HP_OFFSET = FIRST_POKEMON_START + 1;
    private static final int FIRST_POKEMON_EXPERIENCE_OFFSET = FIRST_POKEMON_START + 14;
    private static final int FIRST_POKEMON_LEVEL_OFFSET = FIRST_POKEMON_START + 33;

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
    void readsRedSecondPokemonOriginalTrainerId() throws Exception {
        assertEquals(48035, loadParty(RED_SAVE).getPokemon().get(1).getOriginalTrainerId());
    }

    @Test
    void readsRedFirstPokemonExperience() throws Exception {
        assertEquals(8000, loadParty(RED_SAVE).getPokemon().get(0).getExperience());
    }

    @Test
    void readsRedFirstPokemonAttack() throws Exception {
        assertEquals(29, loadParty(RED_SAVE).getPokemon().get(0).getAttack());
    }

    @Test
    void readsTradedPokemonOriginalTrainerId() throws Exception {
        // Red's Eevee was traded in, so it carries another player's ID, not the save owner's.
        List<PartyPokemon> pokemon = loadParty(RED_SAVE).getPokemon();
        assertEquals(46116, pokemon.get(0).getOriginalTrainerId());
        assertNotEquals(pokemon.get(1).getOriginalTrainerId(), pokemon.get(0).getOriginalTrainerId());
    }

    @Test
    void readsRedFirstPokemonStats() throws Exception {
        PartyPokemon eevee = loadParty(RED_SAVE).getPokemon().get(0);
        assertEquals(27, eevee.getDefense());
        assertEquals(27, eevee.getSpeed());
        assertEquals(35, eevee.getSpecial());
    }

    @Test
    void readsRedSecondPokemonStats() throws Exception {
        PartyPokemon squirtle = loadParty(RED_SAVE).getPokemon().get(1);
        assertEquals(11, squirtle.getAttack());
        assertEquals(14, squirtle.getDefense());
        assertEquals(11, squirtle.getSpeed());
        assertEquals(11, squirtle.getSpecial());
    }

    @Test
    void readsYellowFirstPokemonStats() throws Exception {
        // Stats above 99 check that both bytes of each two-byte value are combined.
        PartyPokemon alakazam = loadParty(YELLOW_SAVE).getPokemon().get(0);
        assertEquals(27315, alakazam.getOriginalTrainerId());
        assertEquals(62, alakazam.getAttack());
        assertEquals(67, alakazam.getDefense());
        assertEquals(133, alakazam.getSpeed());
        assertEquals(142, alakazam.getSpecial());
    }

    @Test
    void readsHighOriginalTrainerId() throws Exception {
        // 65453 is close to the two-byte maximum (65535), so the high byte is above 127
        // and would come out negative without the & 0xFF mask.
        PartyPokemon onlyPokemon = loadParty(RANDOMIZER_SAVE_2).getPokemon().get(0);
        assertEquals(65453, onlyPokemon.getOriginalTrainerId());
        assertEquals(8, onlyPokemon.getAttack());
        assertEquals(13, onlyPokemon.getDefense());
        assertEquals(9, onlyPokemon.getSpeed());
        assertEquals(9, onlyPokemon.getSpecial());
    }

    @Test
    void readsEffortValuesFromOneBattle() throws Exception {
        // Beating a Pokémon adds its base stats to the winner's EVs. Squirtle has beaten the
        // rival's Bulbasaur once, so its EVs equal Bulbasaur's base stats (45/49/49/45/65).
        PartyPokemon squirtle = loadParty(RED_SAVE).getPokemon().get(1);
        assertEquals(45, squirtle.getHpEv());
        assertEquals(49, squirtle.getAttackEv());
        assertEquals(49, squirtle.getDefenseEv());
        assertEquals(45, squirtle.getSpeedEv());
        assertEquals(65, squirtle.getSpecialEv());
    }

    @Test
    void readsZeroEffortValues() throws Exception {
        PartyPokemon eevee = loadParty(RED_SAVE).getPokemon().get(0);
        assertEquals(0, eevee.getHpEv());
        assertEquals(0, eevee.getAttackEv());
        assertEquals(0, eevee.getDefenseEv());
        assertEquals(0, eevee.getSpeedEv());
        assertEquals(0, eevee.getSpecialEv());
    }

    @Test
    void readsLargeEffortValues() throws Exception {
        // Values above 255 only come out right if both bytes are combined.
        PartyPokemon alakazam = loadParty(YELLOW_SAVE).getPokemon().get(0);
        assertEquals(8988, alakazam.getHpEv());
        assertEquals(9910, alakazam.getAttackEv());
        assertEquals(9407, alakazam.getDefenseEv());
        assertEquals(9381, alakazam.getSpeedEv());
        assertEquals(9439, alakazam.getSpecialEv());
    }

    @Test
    void readsExperienceAboveTwoBytes() throws Exception {
        // 94961 is above 65535, the most two bytes can hold, so the third byte must be read.
        assertEquals(94961, loadParty(YELLOW_SAVE).getPokemon().get(0).getExperience());
    }

    @Test
    void combinesAllThreeExperienceBytes() throws Exception {
        // Bytes 1, 0, 5 mean 1 * 65536 + 0 * 256 + 5 = 65541.
        byte[] data = SaveFixtures.load(RED_SAVE);
        data[FIRST_POKEMON_EXPERIENCE_OFFSET] = 1;
        data[FIRST_POKEMON_EXPERIENCE_OFFSET + 1] = 0;
        data[FIRST_POKEMON_EXPERIENCE_OFFSET + 2] = 5;
        assertEquals(65541, new Party(data).getPokemon().get(0).getExperience());
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
        // Red only has 2 Pokémon, so the empty slots are filled with copies of the first one.
        byte[] data = SaveFixtures.load(RED_SAVE);
        data[PARTY_COUNT_OFFSET] = MAX_PARTY_SIZE;
        for (int slot = 1; slot < MAX_PARTY_SIZE; slot++) {
            System.arraycopy(data, FIRST_POKEMON_START, data,
                    FIRST_POKEMON_START + slot * POKEMON_SIZE_BYTES, POKEMON_SIZE_BYTES);
        }
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

    @Test
    void rejectsLevelZero() throws Exception {
        byte[] data = SaveFixtures.load(RED_SAVE);
        data[FIRST_POKEMON_LEVEL_OFFSET] = 0;
        assertThrows(InvalidSaveFileException.class, () -> new Party(data));
    }

    @Test
    void rejectsLevelAbove100() throws Exception {
        byte[] data = SaveFixtures.load(RED_SAVE);
        data[FIRST_POKEMON_LEVEL_OFFSET] = 101;
        assertThrows(InvalidSaveFileException.class, () -> new Party(data));
    }

    @Test
    void acceptsLevelsOneAndHundred() throws Exception {
        byte[] data = SaveFixtures.load(RED_SAVE);
        data[FIRST_POKEMON_LEVEL_OFFSET] = 1;
        assertEquals(1, new Party(data).getPokemon().get(0).getLevel());
        data[FIRST_POKEMON_LEVEL_OFFSET] = 100;
        assertEquals(100, new Party(data).getPokemon().get(0).getLevel());
    }

    @Test
    void rejectsCurrentHpAboveMaxHp() throws Exception {
        // Red's Eevee has 56 max HP, so 57 current HP is impossible.
        byte[] data = SaveFixtures.load(RED_SAVE);
        data[FIRST_POKEMON_CURRENT_HP_OFFSET] = 0;
        data[FIRST_POKEMON_CURRENT_HP_OFFSET + 1] = 57;
        assertThrows(InvalidSaveFileException.class, () -> new Party(data));
    }

    @Test
    void acceptsFaintedPokemon() throws Exception {
        byte[] data = SaveFixtures.load(RED_SAVE);
        data[FIRST_POKEMON_CURRENT_HP_OFFSET] = 0;
        data[FIRST_POKEMON_CURRENT_HP_OFFSET + 1] = 0;
        assertEquals(0, new Party(data).getPokemon().get(0).getCurrentHp());
    }
}
