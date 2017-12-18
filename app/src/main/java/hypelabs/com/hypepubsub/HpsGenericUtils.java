package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class HpsGenericUtils
{

    public static byte[] stringHash(String str)
    {
        return byteArrayHash(str.getBytes());
    }

    public static byte[] byteArrayHash(byte[] byteArray) {
        try {
            MessageDigest md = MessageDigest.getInstance(HpsConstants.HASH_ALGORITHM);
            return md.digest(byteArray);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm: " + e.getMessage(), e);
        }
    }

    public static String byteArrayToString(byte[] byteArray) {
        try {
            return new String(byteArray, HpsConstants.ENCODING_STANDARD);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding: " + e.getMessage(), e);
        }
    }

    public static boolean areClientsEqual(Client client1, Client client2) {
        return areInstancesEqual(client1.instance, client2.instance);
    }

    public static boolean areInstancesEqual(Instance instance1, Instance instance2) {
        return Arrays.equals(instance1.getIdentifier(), instance2.getIdentifier());
    }

    public static byte[] getAndroidBuildModel() {
        try {
            return (android.os.Build.MODEL).getBytes(HpsConstants.ENCODING_STANDARD);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding: " + e.getMessage(), e);
        }
    }

    public static String getInstanceAnnouncementStr(Instance instance) {
        if(instance.getAnnouncement() == null) {
            return "---";
        }

        try {
            return new String(instance.getAnnouncement(), HpsConstants.ENCODING_STANDARD);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding: " + e.getMessage(), e);
        }
    }

    public static String getLogStrFromClient(Client client) {
        return getLogStrFromInstance(client.instance);
    }

    public static String getLogStrFromInstance(Instance instance) {
        return String.format("%s (%s)", HpsGenericUtils.getInstanceAnnouncementStr(instance),
                getIdStringFromInstance(instance));
    }

    public static String getLogStrFromSubscription(Subscription subscription) {
        return String.format("%s (%s)", subscription.serviceName, getKeyStringFromSubscription(subscription));
    }

    public static String getIdStringFromClient(Client client) {
        return getIdStringFromInstance(client.instance);
    }

    public static String getIdStringFromInstance(Instance instance) {
        return String.format("ID: 0x%s", BinaryUtils.byteArrayToHexString(instance.getIdentifier()));
    }

    public static String getKeyStringFromClient(Client client) {
        return String.format("Key: 0x%s", BinaryUtils.byteArrayToHexString(client.key));
    }

    public static String getKeyStringFromSubscription(Subscription subscription) {
        return String.format("Key: 0x%s", BinaryUtils.byteArrayToHexString(subscription.serviceKey));
    }

    public static String getKeyStringFromServiceManager(ServiceManager serviceManager) {
        return String.format("Key: 0x%s", BinaryUtils.byteArrayToHexString(serviceManager.serviceKey));
    }

    public static String getTimeStamp()
    {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("k'h'mm", Locale.getDefault());
        return sdf.format(now);
    }
}
