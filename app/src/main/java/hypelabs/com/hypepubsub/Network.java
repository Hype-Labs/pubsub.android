package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

import java.security.NoSuchAlgorithmException;
import java.util.ListIterator;


public class Network
{
    private static Network network = null;

    protected Client ownClient;
    protected ClientsList networkClients;

    private Network()
    {
        // The own client can only be initialized after the Hype start when we already
        // have an host instance.
        ownClient = null;
        networkClients = new ClientsList();
    }

    public static Network getInstance()
    {
        if(network == null){
            network = new Network();
        }
        return network;
    }

    protected Instance getServiceManagerInstance(byte serviceKey[]) throws NoSuchAlgorithmException
    {
        Instance managerInstance = ownClient.instance;
        byte lowestDist[] = BinaryUtils.xor(serviceKey, ownClient.key);

        ListIterator<Client> it = networkClients.listIterator();
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

    protected void setOwnClient(Instance ownInstance) throws NoSuchAlgorithmException {
        ownClient = new Client(ownInstance);
    }
}
