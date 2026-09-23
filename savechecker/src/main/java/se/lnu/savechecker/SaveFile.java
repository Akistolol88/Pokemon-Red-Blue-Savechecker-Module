package se.lnu.savechecker;

public class SaveFile {
    
    private static final int EXPECTED_SAVE_SIZE_BYTES = 32_768;

    public SaveFile(byte[] data) throws InvalidSaveFileException {
        if (data.length != EXPECTED_SAVE_SIZE_BYTES) {
            throw new InvalidSaveFileException("Expected " + EXPECTED_SAVE_SIZE_BYTES
                + " bytes but got " + data.length + " bytes. \nNot a valid Pokemon Generation 1 savefile.");
        }
    }
}