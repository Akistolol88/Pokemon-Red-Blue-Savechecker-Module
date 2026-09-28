package se.lnu.cli;

import se.lnu.savechecker.SaveFile;
import se.lnu.savechecker.Trainer;
import se.lnu.savechecker.PokedexStatus;
import se.lnu.savechecker.InvalidSaveFileException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 * Main application class and execution entry point.
 */
public class App {


    /**
     * Execution entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        new App().run(args);
    }

    public void run(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: ./gradlew run --args=\"<path to .sav file>\"");
            return;
        }
        String savePath = args[0];
        System.out.println(savePath);  
        try {
            byte[] saveData = Files.readAllBytes(Path.of(savePath));
            SaveFile save = new SaveFile(saveData);
            printTrainer(save.getTrainer());
            printPokedex(save.getPokedexStatus());
        } catch (IOException e) {
            System.out.println("Could not load save: " + savePath);
            return;
        } catch (InvalidSaveFileException e) {
            System.out.println("Could not load Pokemon Generation 1 save: " + e.getMessage());
            return;
        }
    }

    private void printTrainer(Trainer trainer) {
        System.out.println("Your Trainername is :" + trainer.getName());
        System.out.println("You have " + trainer.getBadges().size() + " Badges");
        System.out.println("You have these badges: " + trainer.getBadges());
    }

    private void printPokedex(PokedexStatus pokedex) {
        System.out.println("You have seen " + pokedex.getSeenCount() + " / 151 Pokemons");
        System.out.println("You have caught " + pokedex.getCaughtCount() + " / 151 Pokemons");

    }
}
