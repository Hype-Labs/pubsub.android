package hypelabs.com.hypepubsub;

public class ServiceManager
{
    byte serviceKey[];
    ClientsList subscribers;

    public ServiceManager(byte service_key[])
    {
        this.serviceKey = service_key;
        this.subscribers = new ClientsList();
    }
}
