package se.lnu.savechecker;

/**
 * Turns text stored in a Gen 1 save file into a normal Java {@code String}.
 *
 * <p>Pokémon Red, Blue and Yellow do not store text as normal letters. They use
 * their own table where every character has its own byte, for example
 * {@code 0x80} is 'A' and {@code 0xA0} is 'a'. Names (player, rival, Pokémon
 * nicknames) are stored as a row of these bytes that ends with the byte
 * {@code 0x50}. Anything after that end byte is leftover junk and is ignored.
 *
 * <p>Only letters, digits and spaces are supported. Any other byte (punctuation,
 * special symbols) is shown as '?'.
 */
class TextDecoder {

    /** Reads the bytes of the save file the text comes from. */
    private final ByteReader reader;

    /** Byte that marks the end of a name. Everything after it is ignored. */
    private static final int NAME_TEXT_END = 0x50;

    /** Byte for the letter 'A'. */
    private static final int FIRST_UPPERCASE = 0x80;
    /** Byte for the letter 'Z'. */
    private static final int LAST_UPPERCASE = 0x99;
    /** Byte for the letter 'a'. */
    private static final int FIRST_LOWERCASE = 0xA0;
    /** Byte for the letter 'z'. */
    private static final int LAST_LOWERCASE = 0xB9;
    /** Byte for the digit '0'. */
    private static final int FIRST_DIGIT = 0xF6;
    /** Byte for the digit '9'. */
    private static final int LAST_DIGIT = 0xFF;
    /** Byte for a space. */
    private static final int SPACE = 0x7F;

    /**
     * Creates a decoder for text stored in one save file.
     *
     * @param data the full save file
     */
    TextDecoder(byte[] data) {
        this.reader = new ByteReader(data);
    }

    /**
     * Turns one Gen 1 text byte into the matching Java character.
     *
     * <p>Letters and digits come in unbroken ranges, so the character is found by
     * counting how many steps the byte is from the start of its range, and moving
     * that many steps from 'A', 'a' or '0'. For example {@code 0x82} is two steps
     * after {@code 0x80}, so it becomes the letter two steps after 'A', which is 'C'.
     *
     * @param textByte one byte of Gen 1 text, as a number from 0 to 255
     * @return the matching character, or '?' if the byte is not supported
     */
    char decodeChar(int textByte) {
        if (textByte >= FIRST_UPPERCASE && textByte <= LAST_UPPERCASE) {
            return (char) ('A' + (textByte - FIRST_UPPERCASE));
        } else if (textByte >= FIRST_LOWERCASE && textByte <= LAST_LOWERCASE) {
            return (char) ('a' + (textByte - FIRST_LOWERCASE));
        } else if (textByte >= FIRST_DIGIT && textByte <= LAST_DIGIT) {
            return (char) ('0' + (textByte - FIRST_DIGIT));
        } else if (textByte == SPACE) {
            return ' ';
        } else {
            return '?';
        }
    }

    /**
     * Reads a piece of Gen 1 text, such as a name, out of the save data.
     *
     * <p>Reading stops at the end byte {@code 0x50}, or after {@code maxLength}
     * bytes if no end byte is found, whichever comes first.
     *
     * @param offset the position in the save file where the text starts
     * @param maxLength the most bytes the text can take up in the save file
     * @return the decoded text, without the end byte
     */
    String decode(int offset, int maxLength) {
        StringBuilder decodedText = new StringBuilder();

        for (int i = 0; i < maxLength; i++) {
            int textByte = this.reader.readByte(offset + i);
            if (textByte == NAME_TEXT_END) {
                break;
            }
            decodedText.append(decodeChar(textByte));
        }
        return decodedText.toString();
    }
}
