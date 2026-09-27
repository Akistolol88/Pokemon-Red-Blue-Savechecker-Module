package se.lnu.savechecker;

import java.util.HashSet;
import java.util.Set;

public final class PokedexStatus {
    private static final int LAST_POKEDEX_ENTRY = 151;
    private static final int POKEDEX_CAUGHT_OFFSET = 0x25A3;
    private static final int POKEDEX_SEEN_OFFSET = 0x25B6;
    private static final int BITS_PER_BYTE = 8;

    private static Set<Integer> readDexFlags(byte[] data, int offset) {
        Set<Integer> dexFlags = new HashSet<>();
        for (int dexNumber = 1; dexNumber <= LAST_POKEDEX_ENTRY; dexNumber++) {
            int byteIndex = ((dexNumber - 1) / BITS_PER_BYTE);
            int bitIndex = ((dexNumber - 1) % BITS_PER_BYTE);
            int byteDex = data[offset + byteIndex] & 0xFF;
            if (((byteDex >> bitIndex) & 1) == 1) {
                dexFlags.add(dexNumber);
            }
        }
        return dexFlags;
    }
}