package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

import java.security.NoSuchAlgorithmException;
import java.util.ListIterator;


public class Network
{
    final private static Network network = new Network(); // Early loading to avoid thread-safety issues

    protected Client ownClient;
    protected ClientsList networkClients;

    private Network() {
        // The own client can only be initialized after the Hype start when we already
        // have an host instance.
        ownClient = null;
        networkClients = new ClientsList();
    }

    public static Network getInstance()
    {
        return network;
    }

    protected Client determineClientResponsibleForService(byte serviceKey[]) {
        Client managerClient = ownClient;
        byte lowestDist[] = BinaryUtils.xor(serviceKey, ownClient.key);

        synchronized (network) { // Add thread safety to iteration procedure
            ListIterator<Client> it = networkClients.listIterator();
            while (it.hasNext()) {
                Client client = it.next();

                byte dist[] = BinaryUtils.xor(serviceKey, client.key);
                if (BinaryUtils.determineHigherBigEndianByteArray(lowestDist, dist) == 1) {
                    lowestDist = dist;
                    managerClient = client;
                }
            }
        }
        return managerClient;
    }

    protected void setOwnClient(Instance ownInstance) {
        ownClient = new Client(ownInstance);
    }
}
