package se.lnu.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import se.lnu.savechecker.InvalidSaveFileException;
import se.lnu.savechecker.Party;
import se.lnu.savechecker.PartyPokemon;
import se.lnu.savechecker.PokedexStatus;
import se.lnu.savechecker.SaveFile;
import se.lnu.savechecker.Trainer;

/**
 * Small command-line test app for the savechecker module.
 *
 * <p>Loads a Pokémon Red/Blue/Yellow save file from the path given on the command line and
 * prints the trainer, badges, Pokédex counts and party, using only the module's public API.
 */
public class App {

    /**
     * Execution entry point. Only creates an {@code App} and hands over to {@link #run}, since
     * the assignment allows no other static methods.
     *
     * @param args command-line arguments, where the first one is the path to the save file
     */
    public static void main(String[] args) {
        new App().run(args);
    }

    /**
     * Loads the save file named by the first argument and prints what it contains.
     *
     * <p>Prints a usage hint if no path is given, and a readable error message instead of a crash
     * if the file cannot be read or is not a valid save file.
     *
     * @param args command-line arguments, where the first one is the path to the save file
     */
    public void run(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: ./gradlew run --args=\"<path to .sav file>\"");
            return;
        }
        String savePath = args[0];
        try {
            byte[] saveData = Files.readAllBytes(Path.of(savePath));
            SaveFile save = new SaveFile(saveData);
            printTrainer(save.getTrainer());
            printPokedex(save.getPokedexStatus());
            printParty(save.getParty());
        } catch (IOException e) {
            System.out.println("Could not load save: " + savePath);
            return;
        } catch (InvalidSaveFileException e) {
            System.out.println("Could not load Pokemon Generation 1 save: " + e.getMessage());
            return;
        }
    }

    /**
     * Prints the trainer's name, how many badges they have, and which ones.
     *
     * @param trainer the trainer read from the save file
     */
    private void printTrainer(Trainer trainer) {
        System.out.println("Trainername :" + trainer.getName());
        System.out.println("You have " + trainer.getBadges().size() + " badges");
        System.out.println("You have these badges: " + trainer.getBadges());
    }

    /**
     * Prints how many of the 151 Pokémon have been seen and caught.
     *
     * @param pokedex the Pokédex progress read from the save file
     */
    private void printPokedex(PokedexStatus pokedex) {
        System.out.println("You have seen " + pokedex.getSeenCount() + " / 151 Pokemons");
        System.out.println("You have caught " + pokedex.getCaughtCount() + " / 151 Pokemons");

    }

    /**
     * Prints the party size, then one line per Pokémon with its nickname, level and HP.
     *
     * @param party the party read from the save file
     */
    private void printParty(Party party) {
        System.out.println("Party: " + party.getPokemon().size() + " Pokemon");
        for (PartyPokemon pokemon : party.getPokemon()) {
            System.out.println(pokemon.getNickname() + " Level " + pokemon.getLevel()
                    + " HP: " + pokemon.getCurrentHp() + "/" + pokemon.getMaxHp());
        }
    }
}
