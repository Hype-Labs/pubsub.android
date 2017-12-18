package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;

import com.hypelabs.hype.Instance;

public class Client
{
    final Instance instance;
    byte key[];

    public Client(Instance instance)
    {
        this.instance = instance;
        this.key = HpsGenericUtils.byteArrayHash(instance.getIdentifier());
    }
}
