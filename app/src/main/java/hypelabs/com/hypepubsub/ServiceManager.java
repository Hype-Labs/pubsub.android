package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;

public class ServiceManager
{
    byte serviceKey[];
    LinkedList<Client> subscribers;

    public ServiceManager(byte service_key[])
    {
        this.serviceKey = service_key;
    }

    public void addSubscriber(byte subscriberId[]) throws NoSuchAlgorithmException {

        ListIterator<Client> it = this.subscribers.listIterator();

        // Check if client is already subscribed before adding it to the list
        while(it.hasNext())
        {
            if(it.next().id.equals(subscriberId)) {
                return;
            }
        }

        this.subscribers.add(new Client(subscriberId));
    }

    public void removeSubscriber(byte clientId[])
    {
        ListIterator<Client> it = this.subscribers.listIterator();

        // The following code may not be needed.
        //if(this.subscribers.size() == 0)
        //    return;

        while(it.hasNext())
        {
            if(it.next().id.equals(clientId)) {
                it.remove();
                return;
            }
        }
    }
}
