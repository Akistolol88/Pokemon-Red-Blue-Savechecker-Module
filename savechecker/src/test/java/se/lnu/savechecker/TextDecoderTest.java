package se.lnu.savechecker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TextDecoderTest {

    private static final byte END = 0x50;
    private static final byte LETTER_A = (byte) 0x80;
    private static final byte LETTER_B = (byte) 0x81;

    @Test
    void decodesFirstAndLastUppercase() {
        assertEquals('A', new TextDecoder(new byte[0]).decodeChar(0x80));
        assertEquals('Z', new TextDecoder(new byte[0]).decodeChar(0x99));
    }

    @Test
    void decodesFirstAndLastLowercase() {
        assertEquals('a', new TextDecoder(new byte[0]).decodeChar(0xA0));
        assertEquals('z', new TextDecoder(new byte[0]).decodeChar(0xB9));
    }

    @Test
    void decodesFirstAndLastDigit() {
        assertEquals('0', new TextDecoder(new byte[0]).decodeChar(0xF6));
        assertEquals('9', new TextDecoder(new byte[0]).decodeChar(0xFF));
    }

    @Test
    void decodesSpace() {
        assertEquals(' ', new TextDecoder(new byte[0]).decodeChar(0x7F));
    }

    @Test
    void unknownBytesBecomeQuestionMark() {
        // Edge cases: the bytes just outside each known range.
        assertEquals('?', new TextDecoder(new byte[0]).decodeChar(0x7E));
        assertEquals('?', new TextDecoder(new byte[0]).decodeChar(0x9A));
        assertEquals('?', new TextDecoder(new byte[0]).decodeChar(0x9F));
        assertEquals('?', new TextDecoder(new byte[0]).decodeChar(0xBA));
        assertEquals('?', new TextDecoder(new byte[0]).decodeChar(0xF5));
    }

    @Test
    void returnsEmptyTextWhenFirstByteIsEnd() {
        byte[] data = {END, LETTER_A, LETTER_B};
        assertEquals("", new TextDecoder(data).decode(0, data.length));
    }

    @Test
    void stopsAtMaxLengthWithoutEndMarker() {
        byte[] data = {LETTER_A, LETTER_B, LETTER_A, LETTER_B};
        assertEquals("AB", new TextDecoder(data).decode(0, 2));
    }

    @Test
    void startsReadingAtOffset() {
        byte[] data = {LETTER_A, LETTER_A, LETTER_B, END};
        assertEquals("AB", new TextDecoder(data).decode(1, 3));
    }
}
