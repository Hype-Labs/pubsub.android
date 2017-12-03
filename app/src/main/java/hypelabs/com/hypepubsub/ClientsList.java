package hypelabs.com.hypepubsub;

import android.content.Context;

import com.hypelabs.hype.Instance;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;


public class ClientsList
{
    // Used composition instead of inheritance to hide the methods that shouldn't be called in
    // a ClientsList.
    final private LinkedList<Client> clients = new LinkedList<>();

    private ClientsAdapter clientsAdapter = null;

    /**
     * Adds a client to the client list
     *
     * @param instance Instance to be used in the construction of the client object to be added
     * @return Returns -1 if the client is already present and 0 otherwise.
     * @throws NoSuchAlgorithmException This exception is thrown when the
     *          {@value Constants#HPB_HASH_ALGORITHM} algorithm is not available on the device.
     */
    public synchronized int add(Instance instance) throws NoSuchAlgorithmException
    {
        if(find(instance) != null) // do not add the client if it is already present
            return -1;

        clients.add(new Client(instance));
        return 0;
    }

    /**
     * Removes a client from the client list
     *
     * @param instance Instance of the client object to be removed
     * @return Returns -1 if the client is not present and 0 otherwise.
     */
    public synchronized int remove(Instance instance)
    {
        Client client = find(instance);
        if(client == null)
            return -1;

        clients.remove(client);
        return 0;
    }


    /**
     * Finds a client with a given instance.
     *
     * @param instance Instance of the client object to be searched.
     * @return Returns the object of the client to be searched or NULL if the client does not exist.
     */
    public synchronized Client find(Instance instance)
    {
        ListIterator<Client> it = listIterator();
        while(it.hasNext())
        {
            Client currentClient = it.next();
            if(HpbGenericUtils.areInstancesEqual(currentClient.instance, instance)) {
                return currentClient;
            }
        }
        return null;
    }

    // Methods from LinkedList that we want to enable.
    public synchronized ListIterator<Client> listIterator()
    {
        return clients.listIterator();
    }

    public synchronized int size()
    {
        return clients.size();
    }

    public synchronized Client get(int index)
    {
        return clients.get(index);
    }

    public synchronized ClientsAdapter getClientsAdapter(Context context)
    {
        if(clientsAdapter == null){
            clientsAdapter = new ClientsAdapter(context, clients);
        }
        return clientsAdapter;
    }
}
