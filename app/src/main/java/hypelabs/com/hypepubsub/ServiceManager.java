package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

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

    public void addSubscriber(Instance subscriberInstance) throws NoSuchAlgorithmException
    {
        this.subscribers.add(subscriberInstance);
    }

    public void removeSubscriber(Instance subscriberInstance) throws NoSuchAlgorithmException
    {
        this.subscribers.remove(subscriberInstance);
    }
}
