package se.lnu.savechecker;

/**
 * Checks whether a Generation I (Red/Blue/Yellow) save file has been
 * corrupted, by comparing a checksum stored in the file against one
 * freshly recomputed from its current contents. A checksum is just a sum
 * of bytes; if the recomputed sum doesn't match what was stored, the data
 * has changed since it was last saved.
 *
 * A mismatch doesn't only mean corruption - it can also mean the given
 * bytes were never a Generation I save file at all (for example, a
 * different game's save, or an unrelated file). This class cannot tell
 * those two cases apart.
 *
 * Java's {@code byte} type is signed (-128 to 127), but save file bytes
 * are always meant to be read as plain 0-255 values, so every byte read
 * here is masked with {@code & 0xFF} to correct for that.
 */
class CheckSumValidator {

  /**
   * Byte position where the checksum calculation starts. Comes from
   * reading the actual Generation I game code (the "pret/pokered"
   * disassembly project), not guessed.
   */
  private static final int CHECKSUM_RANGE_START = 0x2598;

  /** Byte position where the checksum calculation ends (inclusive). */
  private static final int CHECKSUM_RANGE_END = 0x3522;

  /**
   * Byte position where the calculated checksum is stored in the file -
   * exactly one byte after {@link #CHECKSUM_RANGE_END}.
   */
  private static final int CHECKSUM_BYTE_OFFSET = 0x3523;

  /**
   * Checks whether the save file's stored checksum matches one recomputed
   * from its current contents.
   *
   * @param data the raw bytes of a 32,768-byte Generation I save file
   * @return {@code true} if the stored checksum matches the recomputed one,
   *     meaning the file is very likely a genuine, uncorrupted Generation I
   *     save file; {@code false} if it does not match, which could mean the
   *     file was corrupted, or that it was never a valid Generation I save
   *     file in the first place
   */
  static boolean isValid(byte[] data) {
    // Step 1: add up every byte in the checksum range.
    int sum = 0;
    for (int i = CHECKSUM_RANGE_START; i <= CHECKSUM_RANGE_END; i++) {
      // In Java, the "byte" type can hold negative numbers (-128 to 127),
      // but save file data doesn't have negative bytes - every byte is
      // really just a value from 0 to 255. "& 0xFF" is a standard Java
      // trick that converts a byte back into its correct 0-255 value
      // before we add it to the running total. Without it, some bytes
      // would be added as negative numbers and the sum would come out
      // wrong.
      sum += (data[i] & 0xFF);
    }

    // Step 2: the original game only keeps the last 8 bits of the sum
    // (i.e. it wraps around every 256, the same way a byte would), so we
    // shrink our sum down to that same range before comparing anything.
    int truncatedCheckSum = sum & 0xFF;

    // Step 3: the original game then flips every bit of that value (0s
    // become 1s and 1s become 0s) before storing it. In Java, "~" performs
    // exactly that bit-flip. We mask with "& 0xFF" again afterwards for the
    // same reason as step 1: to keep the result as a plain 0-255 value
    // instead of a negative number.
    int computedCheckSum = (~truncatedCheckSum) & 0xFF;

    // Step 4: read the checksum value that is actually stored in the file,
    // so we have something to compare our own calculation against. Masked
    // with "& 0xFF" for the same reason as every other byte read above.
    int storedCheckSum = data[CHECKSUM_BYTE_OFFSET] & 0xFF;

    // Step 5: if both values match, the file's data is (very likely)
    // unchanged since it was last saved.
    return computedCheckSum == storedCheckSum;
  }
}