package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;


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

    public byte[] getServiceManagerId(byte service_key[])
    {
        return new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12};
    }

    private byte[] getOwnId()
    {
        // Dummy ID before integrating HypeSDK
        return new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x10, 0x11, 0x12};
    }
}
