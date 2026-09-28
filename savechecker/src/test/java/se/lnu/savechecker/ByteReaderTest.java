package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ByteReaderTest {

    @Test
    void readsSmallByte() {
        byte[] data = {5};
        assertEquals(5, ByteReader.readByte(data, 0));
    }

    @Test
    void readsByteAbove127AsPositive() {
        // Java stores 200 as -56 in a byte; the reader must give back 200.
        byte[] data = {(byte) 200};
        assertEquals(200, ByteReader.readByte(data, 0));
    }

    @Test
    void readsHighestByte() {
        byte[] data = {(byte) 0xFF};
        assertEquals(255, ByteReader.readByte(data, 0));
    }

    @Test
    void readsFromOffset() {
        byte[] data = {1, 2, 3};
        assertEquals(3, ByteReader.readByte(data, 2));
    }

    @Test
    void readsTwoBytesBiggestPartFirst() {
        // 1 * 256 + 44 = 300
        byte[] data = {1, 44};
        assertEquals(300, ByteReader.readTwoBytes(data, 0));
    }

    @Test
    void readsHighestTwoByteValue() {
        byte[] data = {(byte) 0xFF, (byte) 0xFF};
        assertEquals(65_535, ByteReader.readTwoBytes(data, 0));
    }

    @Test
    void readsThreeBytesBiggestPartFirst() {
        // 1 * 65536 + 0 * 256 + 5 = 65541
        byte[] data = {1, 0, 5};
        assertEquals(65_541, ByteReader.readThreeBytes(data, 0));
    }

    @Test
    void readsHighestThreeByteValue() {
        byte[] data = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        assertEquals(16_777_215, ByteReader.readThreeBytes(data, 0));
    }

    @Test
    void splitsByteIntoHalves() {
        // 167 = 10 * 16 + 7
        byte[] data = {(byte) 167};
        assertEquals(10, ByteReader.readHighHalf(data, 0));
        assertEquals(7, ByteReader.readLowHalf(data, 0));
    }

    @Test
    void splitsHighestByteIntoHalves() {
        byte[] data = {(byte) 0xFF};
        assertEquals(15, ByteReader.readHighHalf(data, 0));
        assertEquals(15, ByteReader.readLowHalf(data, 0));
    }

    @Test
    void findsSetAndUnsetBits() {
        // 0b10000001: only the first (0) and last (7) bits are on.
        byte[] data = {(byte) 0b1000_0001};
        assertTrue(ByteReader.isBitSet(data, 0, 0));
        assertFalse(ByteReader.isBitSet(data, 0, 1));
        assertFalse(ByteReader.isBitSet(data, 0, 6));
        assertTrue(ByteReader.isBitSet(data, 0, 7));
    }

    @Test
    void findsNoBitsInZeroByte() {
        byte[] data = {0};
        for (int bit = 0; bit < 8; bit++) {
            assertFalse(ByteReader.isBitSet(data, 0, bit), "bit " + bit);
        }
    }
}
