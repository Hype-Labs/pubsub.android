package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Error;
import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.NetworkObserver;

import java.security.NoSuchAlgorithmException;
import java.util.ListIterator;


public class Network implements NetworkObserver
{

    static Network network = null; // Singleton

    Client ownClient;
    ClientsList networkClients;

    private Network() throws NoSuchAlgorithmException
    {
        this.ownClient = null; // Must be set after HypeStart
        this.networkClients = new ClientsList();
    }

    public static Network getInstance() throws NoSuchAlgorithmException
    {
        if (network == null) {
            network = new Network();
        }

        return network;
    }

    public Instance getServiceManagerInstance(byte serviceKey[])
    {
        // Compare the service key with the hash of the Hype clients id and return
        // the id of the closest client. Consider own ID also!!!!
        Instance managerInstance = this.ownClient.instance;
        byte lowestDist[] = BinaryUtils.xor(serviceKey, this.ownClient.key);

        ListIterator<Client> it = this.networkClients.listIterator();
        while(it.hasNext())
        {
            Client client = it.next();

            byte dist[] = BinaryUtils.xor(serviceKey, client.key);
            if(BinaryUtils.getHigherByteArray(lowestDist, dist) == 1)
            {
                lowestDist = dist;
                managerInstance = client.instance;
            }
        }
        return managerInstance;
    }

    protected void setOwnClient() throws NoSuchAlgorithmException {
        try {
            this.ownClient = new Client(Hype.getHostInstance());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////
    // Methods from NetworkObserver
    //////////////////////////////////////////////////

    @Override
    public void onHypeInstanceFound(Instance var1)
    {
        try {
            this.networkClients.add(var1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeInstanceLost(Instance var1, Error var2)
    {
        try {
            this.networkClients.remove(var1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeInstanceResolved(Instance var1, byte[] var2){}

    @Override
    public void onHypeInstanceFailResolving(Instance var1, Error var2){}
}
