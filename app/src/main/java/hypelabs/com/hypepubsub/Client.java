package hypelabs.com.hypepubsub;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * This class represents an Hype client. Each object of this class contains an Hype ID and a key.
 * The key is generating by hashing the Hype ID using the SHA-1 hashing algorithm.
 */
public class Client
{
    byte id[];
    byte key[];

    public Client(byte[] id) throws NoSuchAlgorithmException
    {
        this.id = id;
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        this.key = md.digest(this.id);
    }
}
