package hypelabs.com.hypepubsub;

public class BinaryUtils {

    /**
     * Calculates the XOR operation between 2 byte arrays.
     *
     * @param array1 1st byte array
     * @param array2 2nd byte array
     * @return If one of the arrays is null, the length of one of the arrays is 0 or
     *         the length the arrays is different a Null pointer is returned. Otherwise a byte
     *         array containing the XOR result is returned.
     */
    public static byte[] xor(byte array1[], byte array2[]) {
        if (array1 == null || array2 == null || array1.length != array2.length || array1.length == 0) {
            return null;
        }

        byte xorArray[] = new byte[array1.length];
        for(int i=0; i<array1.length; i++) {
            xorArray[i] = (byte) (array1[i] ^ array2[i]);
        }
        return xorArray;
    }

    /**
     * Interprets the byte arrays as an integer value and analyzes which one has an higher integer
     * value. It considers that the arrays are Big Endian (the first byte in the arrays is the most
     * significant one).
     *
     * @param array1 1st byte array
     * @param array2 2nd byte array
     * @return If one of the arrays is null, the length of one of the arrays is 0 or
     *         the length the arrays is different the value -1 is returned. If the arrays are equal
     *         the value 0 is returned. If the byte array specified in the first parameter has an
     *         higher integer value the value 1 is returned. If the byte array specified in the second
     *         parameter has an higher integer value the value 2 is returned.
     */
    public static int determineHigherBigEndianByteArray(byte array1[], byte array2[]) {
        if (array1 == null || array2 == null || array1.length != array2.length || array1.length == 0) {
            return -1;
        }

        for(int i=0; i<array1.length; i++) {
            int val1 = byteToUnsignedInt(array1[i]);
            int val2 = byteToUnsignedInt(array2[i]);

            if(val1 == val2) {
                continue;
            }

            // The array which has the largest most significant byte is the higher one
            if(val1 > val2) {
                return 1;
            }
            else {
                return 2;
            }
        }

        return  0;
    }

    /**
     * Converts a byte array to its string hexadecimal representation.
     *
     * @param array Byte array to be converted
     * @return Returns the string containing the hexadecimal representation of the byte array.
     */
    public static String byteArrayToHexString(byte array[]) {
        StringBuilder builder = new StringBuilder();
        for(byte b : array) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

    /**
     * Convert a Byte to its correspondent unsigned int value
     *
     * @param b Byte to be converted
     * @return Return the correspondent unsigned int value
     */
    public static int byteToUnsignedInt(byte b) {
        return ((int) b & 0xFF);
    }
}
