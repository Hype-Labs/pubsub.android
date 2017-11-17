package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;

public class ServiceManager
{
    byte serviceKey[];
    ListClients subscribers;

    public ServiceManager(byte service_key[])
    {
        this.serviceKey = service_key;
        this.subscribers = new ListClients();
    }

    public void addSubscriber(byte subscriberId[]) throws NoSuchAlgorithmException
    {
        this.subscribers.add(subscriberId);
    }

    public void removeSubscriber(byte clientId[]) throws NoSuchAlgorithmException
    {
        this.subscribers.remove(clientId);
    }
}
