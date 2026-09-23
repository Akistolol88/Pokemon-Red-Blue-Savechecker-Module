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

    /**
     * Reads and validates the raw bytes of a Generation I save file.
     *
     * @param data the raw bytes of the save file, exactly as read from disk
     * @throws InvalidSaveFileException if the data is the wrong size, or its
     *     stored checksum does not match its actual contents
     */
    public SaveFile(byte[] data) throws InvalidSaveFileException {
        SaveFileValidator.validate(data);
        // TODO: parse trainer/party/pokedex
    }
}