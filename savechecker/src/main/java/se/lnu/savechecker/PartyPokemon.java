package se.lnu.savechecker;

public final class PartyPokemon {
    private static final int HIGH_BYTE_MULTIPLIER = 256;

    private static int readTwoBytes(byte[] data, int offset) {
        return (data[offset] & 0xFF) * HIGH_BYTE_MULTIPLIER + (data[offset + 1] & 0xFF);
    }
}