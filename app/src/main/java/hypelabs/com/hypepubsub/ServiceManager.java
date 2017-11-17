package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;

public class ServiceManager
{
    byte serviceKey[];
    ClientsList subscribers;

    public ServiceManager(byte service_key[])
    {
        this.serviceKey = service_key;
        this.subscribers = new ClientsList();
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
