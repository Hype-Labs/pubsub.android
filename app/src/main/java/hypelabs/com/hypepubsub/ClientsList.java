package hypelabs.com.hypepubsub;

import android.content.Context;

import com.hypelabs.hype.Instance;

import java.util.LinkedList;
import java.util.ListIterator;


public class ClientsList
{
    // Used composition instead of inheritance to hide the methods from LinkedList class that
    // shouldn't be called in a ClientsList.
    final private LinkedList<Client> clients = new LinkedList<>();
    private ClientsAdapter clientsAdapter = null;

    public synchronized boolean addClient(Client client) {
        if(containsClientWithInstance(client.instance)) {
            return false;
        }
        return clients.add(client);
    }

    public synchronized boolean removeClientWithInstance(Instance instance) {
        Client client = findClientWithInstance(instance);
        if(client == null) {
            return false;
        }
        return clients.remove(client);
    }

    public synchronized Client findClientWithInstance(Instance instance) {
        ListIterator<Client> it = listIterator();
        while(it.hasNext()) {
            Client currentClient = it.next();
            if(HpsGenericUtils.areInstancesEqual(currentClient.instance, instance)) {
                return currentClient;
            }
        }
        return null;
    }

    public synchronized boolean containsClientWithInstance(Instance instance) {
        return findClientWithInstance(instance) != null;
    }

    public synchronized ClientsAdapter getClientsAdapter(Context context) {
        if(clientsAdapter == null){
            clientsAdapter = new ClientsAdapter(context, clients);
        }

        return clientsAdapter;
    }

    //////////////////////////////////////////////////////////////////////////////
    //  LinkedList methods to enable
    //////////////////////////////////////////////////////////////////////////////

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
}
