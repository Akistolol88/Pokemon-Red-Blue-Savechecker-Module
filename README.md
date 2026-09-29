# Pokemon Red/Blue SaveChecker Module

## What it does

A Java module for developers who want to read Pokémon Red, Blue and Yellow (Generation 1) save
files in their own programs. You pass the raw bytes of a `.sav` file to `SaveFile`, and it checks
that the file is valid and gives you:

- **Checksum check:** rejects files that are the wrong size or whose checksum does not match.
- **Trainer:** the trainer name and which of the 8 gym badges have been earned.
- **Pokédex:** which Pokémon have been seen and caught, as counts or per Pokédex number.
- **Party:** up to six Pokémon, each with nickname, level, experience, current and max HP, stats,
  EVs, IVs and original trainer ID.

## What it doesn't do

- It only reads saves. It cannot edit or write them.
- It does not read PC boxes, items or money.
- It does not read Pokémon moves, species, types or HP IV.
- The checksum cannot detect changes that cancel each other out (for example one byte going up by
  1 and another going down by 1).
- Text characters it does not recognise are shown as `?`.
- It has been tested with Red and Yellow saves. Blue uses the same save format as Red but has no
  test save of its own.

## Requirements

- JDK 25
- Git (to clone the repo)
- A Pokémon Red, Blue or Yellow save file (exactly 32,768 bytes)

No Gradle install is needed, because the Gradle wrapper is included.

## Installation

1. Clone the repo:
   ```bash
   git clone https://github.com/Akistolol88/Pokemon-Red-Blue-Savechecker-Module.git
   cd Pokemon-Red-Blue-Savechecker-Module
   ```
2. Build it once to check that everything works:
   ```bash
   ./gradlew build
   ```
   On Windows, use `gradlew.bat build`.
3. Add the module to your own project. The `savechecker` folder has to be inside your Gradle build,
   next to your own project. Copy it to the root of your project, or build your project inside this
   repo (like the `app` test app does). Then add:
   ```groovy
   // settings.gradle
   include("savechecker")

   // your project's build.gradle
   dependencies {
       implementation project(':savechecker')
   }
   ```
4. Import the classes you need from the `se.lnu.savechecker` package:
   ```java
   import se.lnu.savechecker.SaveFile;
   ```

## Usage

```java
try {
   byte[] saveData = Files.readAllBytes(Path.of("red.sav"));
   SaveFile save = new SaveFile(saveData);
   System.out.println(save.getTrainer().getName());
   System.out.println(save.getTrainer().hasBadge(Badge.RAINBOW));
   System.out.println(save.getPokedexStatus().getCaughtCount());
   for (PartyPokemon pokemon : save.getParty().getPokemon()) {
      System.out.println(pokemon.getNickname() + " Lv. " + pokemon.getLevel());
   }
} catch (IOException e) {
   System.out.println("file could not be read");
} catch (InvalidSaveFileException e) {
   System.out.println(e.getMessage());
}
```

`SaveFile` throws an `InvalidSaveFileException` if the data is `null`, the wrong size or has the wrong checksum.

## Public API

All classes are in the `se.lnu.savechecker` package. Start with `SaveFile`; every other object is
reached through it.

### `SaveFile`

Reads and validates the raw bytes of a save file.

| Constructor / method | Returns |
|---  |---|
| `SaveFile(byte[] data)` | a new save. Throws `InvalidSaveFileException` if `data` is `null`, the wrong size, or has the wrong checksum |
| `getTrainer()` | the `Trainer` in this save |
| `getPokedexStatus()` | the `PokedexStatus` in this save |
| `getParty()` | the `Party` in this save |

### `Trainer`

The player's name and gym badges.

| Method | Returns |
|---|---|
| `getName()` | the trainer's name |
| `hasBadge(Badge badge)` | `true` if the trainer has that badge |
| `getBadges()` | a `Set<Badge>` of all earned badges, empty if none |

### `Badge`

An enum of the eight gym badges, in the order they are stored in the save:
`BOULDER`, `CASCADE`, `THUNDER`, `RAINBOW`, `SOUL`, `MARSH`, `VOLCANO`, `EARTH`.

### `PokedexStatus`

Which Pokémon the player has seen and caught. Pokédex numbers are 1 to 151; other numbers throw
`IllegalArgumentException`.

| Method | Returns |
|---|---|
| `hasSeen(int dexNumber)` | `true` if that Pokémon has been seen (or caught) |
| `hasCaught(int dexNumber)` | `true` if that Pokémon has been caught |
| `getSeenCount()` | the "SEEN" number from the in-game Pokédex |
| `getCaughtCount()` | the "OWN" number from the in-game Pokédex |
| `getSeenSpecies()` | a `Set<Integer>` of seen Pokédex numbers, caught ones included |
| `getCaughtSpecies()` | a `Set<Integer>` of caught Pokédex numbers |

### `Party`

The Pokémon the player is carrying.

| Method | Returns |
|---|---|
| `getPokemon()` | a `List<PartyPokemon>` in party order (up to six), empty if the party is empty |

### `PartyPokemon`

One Pokémon in the party. All methods take no arguments.

| Methods | Returns |
|---|---|
| `getNickname()` | the nickname as normal text |
| `getLevel()` | the level, 1 to 100 |
| `getExperience()` | the experience points |
| `getCurrentHp()`, `getMaxHp()` | current HP (0 if fainted) and max HP |
| `getAttack()`, `getDefense()`, `getSpeed()`, `getSpecial()` | the stats |
| `getHpEv()`, `getAttackEv()`, `getDefenseEv()`, `getSpeedEv()`, `getSpecialEv()` | the EVs, 0 to 65535 |
| `getAttackIv()`, `getDefenseIv()`, `getSpeedIv()`, `getSpecialIv()` | the IVs, 0 to 15 |
| `getOriginalTrainerId()` | the original trainer's ID, 0 to 65535 |

### `InvalidSaveFileException`

A checked exception thrown by `new SaveFile(...)` when the data is not a valid save. The message
(`getMessage()`) says what is wrong.

## Test app

`app/` is a small console program that shows the module in use. It is not a part of the module.

```bash
./gradlew run --args="savechecker/src/test/resources/saves/Yellow/Yellow_Randomizer_02.srm"
```

Windows users can use `gradlew.bat run --args="..."`.

Example output:

```text
Trainername :YELLOW
You have 0 badges
You have these badges: []
You have seen 8 / 151 Pokemons
You have caught 1 / 151 Pokemons
Party: 1 Pokemon
METAPOD Level 6 HP: 18/22
```

## Running the tests

```bash
./gradlew test
./gradlew check
```

`test` runs 93 unit tests, `check` runs the tests, Checkstyle and PMD.

The tests are in `savechecker/src/test/`.

## Versioning

Version 1.0.0, semantic versioning.

## Contributing & bug reports

If you found a bug, feel free to open an issue on GitHub and submit your save file.
Contributions are welcome: make a pull request, and run `./gradlew check` before opening it.

## License

This project is released under the Unlicense (public domain). See `LICENSE`.