package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;


public class Network
{

    static Network network = null; // Singleton

    Client ownClient;
    LinkedList<Client> networkClients;

    private Network() throws NoSuchAlgorithmException
    {
        this.ownClient = new Client(this.getOwnId());
    }

    public static Network getInstance() throws NoSuchAlgorithmException
    {
        if (network == null) {
            network = new Network();
        }

        return network;
    }

    public byte[] getServiceManagerId(byte serviceKey[])
    {
        // Compare the service key with the hash of the Hype clients id and return
        // the id of the closest client. Consider own ID also!!!!
        byte managerId[] = this.ownClient.id;
        byte lowestDist[] = BinaryUtils.xor(serviceKey, this.ownClient.key);

        ListIterator<Client> it = this.networkClients.listIterator();
        while(it.hasNext())
        {
            Client client = it.next();

            byte dist[] = BinaryUtils.xor(serviceKey, client.key);
            if(BinaryUtils.getHigherByteArray(lowestDist, dist) == 1)
            {
                lowestDist = dist;
                managerId = client.id;
            }
        }
        return managerId;
    }

    private byte[] getOwnId()
    {
        // Dummy ID before integrating HypeSDK
        return new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12};
    }
}
