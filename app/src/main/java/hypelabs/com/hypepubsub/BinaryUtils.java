package hypelabs.com.hypepubsub;



public class BinaryUtils {

    public static byte[] xor(byte array1[], byte array2[])
    {
        if ((array1.length != array2.length) || (array1.length == 0)) {
            return null;
        }

        byte xorArray[] = new byte[array1.length];
        for(int i=0; i<array1.length; i++) {
            xorArray[i] = (byte) (array1[i] ^ array2[i]);
        }
        return xorArray;
    }

    public static int getHigherByteArray(byte array1[], byte array2[])
    {
        if ((array1.length != array2.length) || (array1.length == 0)) {
            return -1;
        }

        for(int i=0; i<array1.length; i++)
        {
            int val1 = byteToUnsignedInt(array1[i]);
            int val2 = byteToUnsignedInt(array2[i]);

            if(val1 == val2)
                continue;

            if(val1 > val2) {
                return 1;
            }
            else {
                return 2;
            }
        }

        return  0;
    }

    public static String byteArrayToHexString(byte array[])
    {
        StringBuilder builder = new StringBuilder();
        for(byte b : array) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }

    public static int byteToUnsignedInt(byte b)
    {
        return ((int) b & 0xFF);
    }
}
