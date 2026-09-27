package se.lnu.savechecker;

/**
 * Represents a single Generation I (Red/Blue/Yellow) Pokemon save file that
 * has already been checked and is known to be genuine and uncorrupted.
 *
 * Creating a {@code SaveFile} is the only way to get one: the constructor
 * checks the given bytes before anything else can use them, so any
 * {@code SaveFile} that exists is guaranteed to hold valid Generation I
 * save data.
 */
public class SaveFile {

    /** The player's name and badges, read from this save. */
    private final Trainer trainer;

    /** Which Pokémon the player has seen and caught, read from this save. */
    private final PokedexStatus pokedexStatus;

    /**
     * Reads and validates the raw bytes of a Generation I save file.
     *
     * @param data the raw bytes of the save file, exactly as read from disk
     * @throws InvalidSaveFileException if the data is {@code null}, the wrong
     *     size, or its stored checksum does not match its actual contents
     */
    public SaveFile(byte[] data) throws InvalidSaveFileException {
        SaveFileValidator.validate(data);

        this.trainer = new Trainer(data);
        this.pokedexStatus = new PokedexStatus(data);
    }

    /**
     * Returns the trainer info stored in this save: the player's name and
     * which gym badges they have earned.
     *
     * @return the trainer from this save
     */
    public Trainer getTrainer() {
        return this.trainer;
    }

    /**
     * Returns the Pokédex progress stored in this save: which Pokémon the
     * player has seen and which they have caught.
     *
     * @return the Pokédex status from this save
     */
    public PokedexStatus getPokedexStatus() {
        return this.pokedexStatus;
    }
}