package se.lnu.savechecker;

/**
 * Checks whether a given byte array could be a genuine Generation I
 * (Red/Blue/Yellow) save file, and rejects it with a specific reason if not.
 *
 * There are two separate ways the data can fail: it can be the wrong size,
 * or - even if the size is right - its checksum can fail to match, which
 * means the data was corrupted or was never a Generation I save file at
 * all. This class checks both, so that {@link SaveFile} itself does not
 * need to know about either check individually.
 */
class SaveFileValidator {

    /** The exact size, in bytes, of a genuine Generation I save file. */
    private static final int EXPECTED_SAVE_SIZE_BYTES = 32_768;

    /**
     * Validates the given save file bytes, throwing if they are invalid.
     *
     * @param data the raw bytes to validate
     * @throws InvalidSaveFileException if {@code data} is not
     *     {@value #EXPECTED_SAVE_SIZE_BYTES} bytes long, or its stored
     *     checksum does not match one recomputed from its contents
     */
    static void validate(byte[] data) throws InvalidSaveFileException {
        if (data.length != EXPECTED_SAVE_SIZE_BYTES) {
            throw new InvalidSaveFileException("Expected " + EXPECTED_SAVE_SIZE_BYTES
                + " bytes but got " + data.length + " bytes. \nNot a valid Pokemon Generation 1 savefile.");
        }
        if (!CheckSumValidator.isValid(data)) {
            throw new InvalidSaveFileException("Checksum does not match");
        }
    }
}

