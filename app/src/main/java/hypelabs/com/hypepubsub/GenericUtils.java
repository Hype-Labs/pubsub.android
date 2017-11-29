package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class GenericUtils
{
    /**
     *  Compares 2 Hype instances.
     *
     * @param instance1 1st Hype instance.
     * @param instance2 2nd Hype instance.
     * @return Returns true if the instances identifiers are equal and false otherwise
     */
    public static boolean areInstancesEqual(Instance instance1, Instance instance2)
    {
        return Arrays.equals(instance1.getIdentifier(), instance2.getIdentifier());
    }

    /**
     * Decodes the announcement string of an Hype instance.
     *
     * @param instance Hype instance containing the announcement to be decoded.
     * @return Returns the decoded string of the announcement.
     * @throws UnsupportedEncodingException Throws this exception when the UTF-8 encoding is not
     *                                      available on the device.
     */
    public static String getInstanceAnnouncementStr(Instance instance) throws UnsupportedEncodingException
    {
        if(instance.getAnnouncement() == null)
            return "---";
        return new String(instance.getAnnouncement(), Constants.HPB_ENCODING_STANDARD);
    }

    /**
     * Hashes the content of a byte array.
     *
     * @param byteArray Byte array to be hashed.
     * @return Returns the hash of the specified byte array.
     * @throws NoSuchAlgorithmException Throws this exception when the SHA-1 algorithm is not
     *                                  available on the device.
     */
    public static byte[] getByteArrayHash(byte[] byteArray) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(Constants.HPB_HASH_ALGORITHM);
        return md.digest(byteArray);
    }

    /**
     * Hashes the content of a string.
     *
     * @param str String to be hashed.
     * @return Returns the hash of the specified string.
     * @throws NoSuchAlgorithmException Throws this exception when the SHA-1 algorithm is not
     *                                  available on the device.
     */
    public static byte[] getStrHash(String str) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(Constants.HPB_HASH_ALGORITHM);
        return md.digest(str.getBytes());
    }

    public static String getInstanceLogIdStr(Instance instance) throws UnsupportedEncodingException
    {
        return GenericUtils.getInstanceAnnouncementStr(instance) + " (0x" + BinaryUtils.byteArrayToHexString(instance.getIdentifier()) + ")";
    }
}
