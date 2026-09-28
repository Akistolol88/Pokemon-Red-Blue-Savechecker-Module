package se.lnu.savechecker;

/**
 * Reads numbers out of a Gen 1 save file's raw bytes.
 *
 * <p>Java's {@code byte} is signed (-128 to 127), but the game treats every
 * byte as 0 to 255. Every method here masks with {@code & 0xFF} first, so the
 * rest of the module never has to remember to do it.
 *
 * <p>Numbers bigger than 255 are stored across several bytes with the biggest
 * part first (big-endian). Some small values share a byte: two 4-bit halves,
 * or eight single on/off bits.
 */
final class ByteReader {

    /** What a byte one step further left is worth: 256. */
    private static final int HIGH_BYTE_MULTIPLIER = 256;

    /** What a byte two steps further left is worth: 256 * 256. */
    private static final int HIGHEST_BYTE_MULTIPLIER = 65_536;

    /** How many values fit in half a byte (4 bits): 0 to 15. */
    private static final int HALF_BYTE_SIZE = 16;

    private ByteReader() {
    }

    /**
     * Reads one byte as a number from 0 to 255.
     *
     * @param data the full save file
     * @param offset the position of the byte
     * @return the byte's value, 0 to 255
     */
    static int readByte(byte[] data, int offset) {
        return data[offset] & 0xFF;
    }

    /**
     * Reads a number stored in two bytes, biggest part first.
     * For example the bytes 1, 44 mean 1 * 256 + 44 = 300.
     *
     * @param data the full save file
     * @param offset the position of the first byte
     * @return the number, 0 to 65535
     */
    static int readTwoBytes(byte[] data, int offset) {
        return readByte(data, offset) * HIGH_BYTE_MULTIPLIER
                + readByte(data, offset + 1);
    }

    /**
     * Reads a number stored in three bytes, biggest part first.
     * For example the bytes 1, 0, 5 mean 1 * 65536 + 0 * 256 + 5 = 65541.
     *
     * @param data the full save file
     * @param offset the position of the first byte
     * @return the number, 0 to 16777215
     */
    static int readThreeBytes(byte[] data, int offset) {
        return readByte(data, offset) * HIGHEST_BYTE_MULTIPLIER
                + readByte(data, offset + 1) * HIGH_BYTE_MULTIPLIER
                + readByte(data, offset + 2);
    }

    /**
     * Reads the high (left) half of a byte. For example 167 is 10 * 16 + 7,
     * so its high half is 10.
     *
     * @param data the full save file
     * @param offset the position of the byte
     * @return the high half, 0 to 15
     */
    static int readHighHalf(byte[] data, int offset) {
        return readByte(data, offset) / HALF_BYTE_SIZE;
    }

    /**
     * Reads the low (right) half of a byte. For example 167 is 10 * 16 + 7,
     * so its low half is 7.
     *
     * @param data the full save file
     * @param offset the position of the byte
     * @return the low half, 0 to 15
     */
    static int readLowHalf(byte[] data, int offset) {
        return readByte(data, offset) % HALF_BYTE_SIZE;
    }

    /**
     * Checks whether one bit of a byte is on. Bit 0 is the rightmost bit.
     *
     * <p>The byte is shifted right so the wanted bit lands in the last
     * position, then {@code & 1} keeps only that bit: 1 means on, 0 means off.
     *
     * @param data the full save file
     * @param offset the position of the byte
     * @param bitIndex which bit to check, 0 to 7
     * @return {@code true} if the bit is on
     */
    static boolean isBitSet(byte[] data, int offset, int bitIndex) {
        return ((readByte(data, offset) >> bitIndex) & 1) == 1;
    }
}
