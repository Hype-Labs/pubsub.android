package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class HpsGenericUtils
{
    public static boolean areInstancesEqual(Instance instance1, Instance instance2)
    {
        return Arrays.equals(instance1.getIdentifier(), instance2.getIdentifier());
    }

    public static String buildInstanceAnnouncementStr(Instance instance) throws UnsupportedEncodingException
    {
        if(instance.getAnnouncement() == null)
            return "---";
        return new String(instance.getAnnouncement(), HpsConstants.ENCODING_STANDARD);
    }

    public static byte[] byteArrayHash(byte[] byteArray) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(HpsConstants.HASH_ALGORITHM);
        return md.digest(byteArray);
    }

    public static byte[] stringHash(String str) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(HpsConstants.HASH_ALGORITHM);
        return md.digest(str.getBytes());
    }

    public static String buildInstanceLogIdStr(Instance instance) throws UnsupportedEncodingException
    {
        return HpsGenericUtils.buildInstanceAnnouncementStr(instance) + " (0x" + BinaryUtils.byteArrayToHexString(instance.getIdentifier()) + ")";
    }

    public static String buildSubscriptionLogStr(Subscription subscription) throws UnsupportedEncodingException
    {
        return subscription.serviceName + " (0x" + BinaryUtils.byteArrayToHexString(subscription.serviceKey) + ")";
    }
}
