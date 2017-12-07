package hypelabs.com.hypepubsub;

import org.junit.Test;

import static org.junit.Assert.*;

public class BinaryUtilsUnitTest {

    @Test
    public void testXor()
    {
        byte xorResult[];

        byte binStr1[] = new byte[]{'X', 's', 'E', '6', 's', '1', 'l', 'h', '8', 'I', 'D', 'p', 'a', 'v', 'U', '1', 'R', '8', 'B', 'U'};
        byte binStr2[] = new byte[]{'N', 'I', 'Y', '6', 'B', '9', 'u', '4', 'E', 'b', 'w', 's', 'a', 'f', 'k', 'o', 'G', '4', 'n', 'B'};
        byte expectedXorResult[] = new byte[]{(byte) 0x16, (byte) 0x3a, (byte) 0x1c, (byte) 0x00, (byte) 0x31, (byte) 0x08, (byte) 0x19, (byte) 0x5c, (byte) 0x7d, (byte) 0x2b, (byte) 0x33, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x3e, (byte) 0x5e, (byte) 0x15, (byte) 0x0c, (byte) 0x2c, (byte) 0x17};

        xorResult = BinaryUtils.xor(binStr1, binStr2);
        assertArrayEquals(expectedXorResult, xorResult);

        byte bin_str3[] = new byte[]{(byte) 0xe4, (byte) 0x66, (byte) 0x56, (byte) 0xb7, (byte) 0x5a, (byte) 0xb6, (byte) 0x36, (byte) 0x3d, (byte) 0x34, (byte) 0x9d, (byte) 0x11, (byte) 0xef, (byte) 0xd8, (byte) 0xe4, (byte) 0x4a, (byte) 0xdf, (byte) 0x06, (byte) 0xd3, (byte) 0x48, (byte) 0x4d};
        byte bin_str4[] = new byte[]{(byte) 0x7b, (byte) 0x39, (byte) 0xa7, (byte) 0xb6, (byte) 0x29, (byte) 0x29, (byte) 0xda, (byte) 0x8d, (byte) 0x23, (byte) 0x6c, (byte) 0xd4, (byte) 0x08, (byte) 0xe2, (byte) 0x5c, (byte) 0x9d, (byte) 0x17, (byte) 0xaa, (byte) 0x41, (byte) 0x7c, (byte) 0x89};
        expectedXorResult = new byte[]{(byte) 0x9f, (byte) 0x5f, (byte) 0xf1, (byte) 0x01, (byte) 0x73, (byte) 0x9f, (byte) 0xec, (byte) 0xb0, (byte) 0x17, (byte) 0xf1, (byte) 0xc5, (byte) 0xe7, (byte) 0x3a, (byte) 0xb8, (byte) 0xd7, (byte) 0xc8, (byte) 0xac, (byte) 0x92, (byte) 0x34, (byte) 0xc4};

        xorResult = BinaryUtils.xor(bin_str3, bin_str4);
        assertArrayEquals(expectedXorResult, xorResult);
    }
    
    @Test
    public void testHigherBinaryArray()
    {
        byte val1[] = new byte[] {(byte) 0xd1, (byte) 0x21, (byte) 0x21, (byte) 0x58, (byte) 0x0d, (byte) 0x5a, (byte) 0x6a, (byte) 0xf0, (byte) 0x2a, (byte) 0xdf, (byte) 0x4e, (byte) 0xe4, (byte) 0x09, (byte) 0x55, (byte) 0xe7, (byte) 0x20, (byte) 0x30, (byte) 0x56, (byte) 0x5a, (byte) 0xa4}; // 1101000100100001001000010101100000001101010110100110101011110000001010101101111101001110111001000000100101010101111001110010000000110000010101100101101010100100
        byte val2[] = new byte[] {(byte) 0x6e, (byte) 0xee, (byte) 0xca, (byte) 0xd0, (byte) 0x51, (byte) 0xaa, (byte) 0x22, (byte) 0xb4, (byte) 0xb8, (byte) 0x75, (byte) 0x02, (byte) 0xde, (byte) 0x60, (byte) 0xee, (byte) 0x4e, (byte) 0xdf, (byte) 0x21, (byte) 0x50, (byte) 0x1b, (byte) 0xfe}; // 0110111011101110110010101101000001010001101010100010001010110100101110000111010100000010110111100110000011101110010011101101111100100001010100000001101111111110
        byte val3[] = new byte[] {(byte) 0x1d, (byte) 0x02, (byte) 0x8b, (byte) 0xb7, (byte) 0xc4, (byte) 0x90, (byte) 0x7f, (byte) 0xcb, (byte) 0xab, (byte) 0x6f, (byte) 0x9e, (byte) 0x9c, (byte) 0x8b, (byte) 0x26, (byte) 0x2c, (byte) 0x87, (byte) 0x7e, (byte) 0xe3, (byte) 0x84, (byte) 0xe4}; // 0001110100000010100010111011011111000100100100000111111111001011101010110110111110011110100111001000101100100110001011001000011101111110111000111000010011100100
        byte val4[] = new byte[] {(byte) 0xa0, (byte) 0x81, (byte) 0x89, (byte) 0xe8, (byte) 0xca, (byte) 0x01, (byte) 0x37, (byte) 0x19, (byte) 0x07, (byte) 0xab, (byte) 0x9d, (byte) 0xb8, (byte) 0x0f, (byte) 0x0e, (byte) 0xb0, (byte) 0x62, (byte) 0x12, (byte) 0x42, (byte) 0x72, (byte) 0x22}; // 1010000010000001100010011110100011001010000000010011011100011001000001111010101110011101101110000000111100001110101100000110001000010010010000100111001000100010

        assertEquals(0, BinaryUtils.determineHigherBigEndianByteArray(val1, val1));
        assertEquals(1, BinaryUtils.determineHigherBigEndianByteArray(val1, val2));
        assertEquals(2, BinaryUtils.determineHigherBigEndianByteArray(val2, val1));
        assertEquals(1, BinaryUtils.determineHigherBigEndianByteArray(val2, val3));
        assertEquals(1, BinaryUtils.determineHigherBigEndianByteArray(val4, val2));
        assertEquals(2, BinaryUtils.determineHigherBigEndianByteArray(val4, val1));
    }
}
