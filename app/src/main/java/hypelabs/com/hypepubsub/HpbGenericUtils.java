package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HpbGenericUtils
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
     * @throws UnsupportedEncodingException Throws this exception when the {@value HpbConstants#ENCODING_STANDARD} encoding is not
     *                                      available on the device.
     */
    public static String getInstanceAnnouncementStr(Instance instance) throws UnsupportedEncodingException
    {
        if(instance.getAnnouncement() == null)
            return "---";
        return new String(instance.getAnnouncement(), HpbConstants.ENCODING_STANDARD);
    }

    /**
     * Hashes the content of a byte array.
     *
     * @param byteArray Byte array to be hashed.
     * @return Returns the hash of the specified byte array.
     * @throws NoSuchAlgorithmException Throws this exception when the {@value HpbConstants#HASH_ALGORITHM} algorithm is not
     *                                  available on the device.
     */
    public static byte[] getByteArrayHash(byte[] byteArray) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(HpbConstants.HASH_ALGORITHM);
        return md.digest(byteArray);
    }

    /**
     * Hashes the content of a string.
     *
     * @param str String to be hashed.
     * @return Returns the hash of the specified string.
     * @throws NoSuchAlgorithmException Throws this exception when the {@value HpbConstants#HASH_ALGORITHM} algorithm is not
     *                                  available on the device.
     */
    public static byte[] getStrHash(String str) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(HpbConstants.HASH_ALGORITHM);
        return md.digest(str.getBytes());
    }

    /**
     * This method builds a string containing the announcement string of a given instance
     * joined with the instance identifier in an hexadecimal format.
     *
     * @param instance Instance to be processed.
     * @return Returns a string containing the announcement string of a given instance and
     *          the instance identifier in an hexadecimal format.
     * @throws UnsupportedEncodingException This exception is thrown when the encoding format
     *          used {@value HpbConstants#ENCODING_STANDARD}
     */
    public static String getInstanceLogIdStr(Instance instance) throws UnsupportedEncodingException
    {
        return HpbGenericUtils.getInstanceAnnouncementStr(instance) + " (0x" + BinaryUtils.byteArrayToHexString(instance.getIdentifier()) + ")";
    }

    public static String getSubscriptionLogStr(Subscription subscription) throws UnsupportedEncodingException
    {
        return subscription.serviceName + " (0x" + BinaryUtils.byteArrayToHexString(subscription.serviceKey) + ")";
    }
}
