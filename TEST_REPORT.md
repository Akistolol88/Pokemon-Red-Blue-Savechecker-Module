# Test Report

<!--
    Commit this file to the root of your GitHub repository, alongside your module's code.
-->

## Summary

I used 93 JUnit 5 tests, using 4 real Pokemon generation 1 savefiles (1 Red, 3 Yellow). All 3 games savefiles follow the same offsets. No Blue save was tested.

I also did 3 manual tests because `/app` doesn't have automated tests. I useda Red save file and a Yellow Save file and one using a different file not being a savefile. I did manual tests because a person has to judge wether the output and erro messages makes sense.

The reason I chose to use save files is because they contain real data from a real play-through, so tests check against such values you can confirm in game, such as names, levels, badges, experience. 

To repeat the tests:

- The unit tests are in `savechecker/src/test/java/`
- save files' path, `savechecker/src/test/resources/saves/`
- `./gradlew test` runs the tests
- `./gradlew run --args="<folder/filename>"` runs the test app for manual tests

## Test Results

All 93 automated tests pass (`./gradlew test`), and `./gradlew check` reports 0 Checkstyle and 0
PMD warnings.

### Manual tests (test app)

| What was tested | How it was tested | Result |
| --- | --- | --- |
| The test app reads a valid Red save. | Manual test: `./gradlew run --args="'savechecker/src/test/resources/saves/Red/Pokemon Red (UE) [S][!].sav'"` and compared the output with the values the unit tests check. | ✅ Passed. Trainer `A`, 0 badges, 7 seen / 5 caught, party EEVEE Lv 20 (56/56 HP) and SQUIRTLE Lv 6 (20/22 HP). |
| The test app reads a valid Yellow save from a randomizer (random Pokémon, 3 badges). | Manual test: `./gradlew run --args="savechecker/src/test/resources/saves/Yellow/Yellow_Randomizer_01.srm"`. | ✅ Passed. Trainer `A`, badges BOULDER, CASCADE, THUNDER, 76 seen / 5 caught, party of 5 (EXEGGUTOR Lv 30, PSYDUCK Lv 8, WEEDLE Lv 6, VENOMOTH Lv 5, STARYU Lv 10). |
| A file that is not a save is rejected with a readable message. | Manual test: ran the test app with a 2,139-byte JPG image instead of a save. | ✅ Passed. Printed "Expected 32768 bytes but got 2139 bytes" and stopped without crashing. |
| A path that does not exist is handled. | Manual test: ran the test app with the path `Red/Pokemon` (a mistyped path). | ✅ Passed. Printed "Could not load save: Red/Pokemon" and stopped without crashing. |

### Automated tests (JUnit 5)

| What was tested | How it was tested | Result |
| --- | --- | --- |
| `SaveFile` accepts valid saves and rejects invalid data. | `SaveFileTest` (11 tests): all 4 fixture saves are accepted; `null`, empty, 32,767 and 32,769 bytes, all zeros and a corrupted checksum all throw `InvalidSaveFileException` with a message that says what is wrong. Also checks that changing the input array after loading does not change the save. | ✅ Passed. |
| The checksum check. | `CheckSumValidatorTest` (7 tests): every fixture save is valid; changing one byte, the checksum byte or the last byte in the checksum range makes it invalid; bytes outside the range are ignored; a correctly updated checksum is accepted. | ✅ Passed. |
| The checksum cannot detect changes that cancel each other out. | `CheckSumValidatorTest.cannotDetectChangesThatCancelOut`: raises one byte by 1 and lowers another by 1, then checks that the save is still reported as valid. | ⚠️ Known limitation of the game's checksum, documented by the test and in the README. |
| `Trainer` reads the name and badges. | `TrainerTest` (10 tests): names from Red and Yellow saves, the name stops at the end marker, no badges, all 8 badges, the first 3 badges, the first and last bit of the badge byte, the badge set cannot be modified, `hasBadge(null)` throws. | ✅ Passed. |
| `PokedexStatus` reads seen and caught Pokémon. | `PokedexStatusTest` (13 tests): seen/caught counts for all fixtures, the exact species for two saves, seen-but-not-caught, the byte boundary between #8 and #9, #1 and #151, numbers outside 1–151 throw, every caught Pokémon is also seen, the sets cannot be modified. | ✅ Passed. |
| `Party` and `PartyPokemon` read the party. | `PartyTest` (31 tests): party size (0, 1, 2, 5 and 6 Pokémon), nickname, level, HP, experience (including values above 2 bytes), original trainer ID (including a traded Pokémon), stats, EVs and IVs checked against the in-game values; more than 6 Pokémon, level 0 or above 100 and current HP above max HP are rejected; the list cannot be modified. | ✅ Passed. |
| Text decoding of names. | `TextDecoderTest` (8 tests): first and last upper/lowercase letter and digit, space, unknown bytes become `?`, empty names, stopping at max length, reading from an offset. | ✅ Passed. |
| Reading numbers and bits from the raw bytes. | `ByteReaderTest` (13 tests): one-, two- and three-byte numbers (including bytes above 127 and the highest values), splitting a byte into halves (used for IVs), reading single bits, and that the reader keeps its own copy of the data. | ✅ Passed. |

### Not tested

- No Blue save was tested (see Summary).
- The test app itself has no automated tests; it is only tested manually above.

### AI-written tests

I wrote the first tests of each class myself and then the remaining tests that follow the same patterns, and
most edge cases, were written by AI. 
