package hypelabs.com.hypepubsub;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.hypelabs.hype.Instance;

/**
 * This class represents an Hype client. Each object of this class contains an Hype Instance and a key.
 * The key is generating by hashing the Hype ID using the SHA-1 hashing algorithm.
 */
public class Client
{
    Instance instance;
    byte key[];

    public Client(Instance instance) throws NoSuchAlgorithmException
    {
        this.instance = instance;
        this.key = GenericUtils.getByteArrayHash(instance.getIdentifier());
    }
}
