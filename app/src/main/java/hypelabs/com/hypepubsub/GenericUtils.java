package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.State;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class GenericUtils
{
    public static boolean areInstancesEqual(Instance instance1, Instance instance2)
    {
        return Arrays.equals(instance1.getIdentifier(), instance2.getIdentifier());
    }

    public static byte[] getByteArrayHash(byte[] content) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(Constants.HPB_HASH_ALGORITHM);
        return md.digest(content);
    }

    public static byte[] getStrHash(String content) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance(Constants.HPB_HASH_ALGORITHM);
        return md.digest(content.getBytes());
    }
}