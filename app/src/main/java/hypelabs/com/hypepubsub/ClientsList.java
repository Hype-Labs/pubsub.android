package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;


public class ClientsList
{
    // Used composition instead of inheritance to hide the methods that shouldn't be called in
    // a ServiceManagersList.

    private LinkedList<Client> clients = new LinkedList<>();

    public int add(byte clientId[]) throws NoSuchAlgorithmException
    {
        if(find(clientId) != null) // do not add the client if it is already present
            return -1;

        clients.add(new Client(clientId));
        return 0;
    }

    public int remove(byte clientId[]) throws NoSuchAlgorithmException
    {
        Client client = find(clientId);
        if(client == null)
            return -1;

        clients.remove(client);
        return 0;
    }

    public Client find(byte clientId[])
    {
        ListIterator<Client> it = listIterator();
        while(it.hasNext())
        {
            Client currentClient = it.next();
            if(Arrays.equals(currentClient.id, clientId)) {
                return currentClient;
            }
        }
        return null;
    }

    // Methods from LinkedList that we want to enable.
    public ListIterator<Client> listIterator() {
        return clients.listIterator();
    }

    public int size() {
        return clients.size();
    }

    public Client get(int index) {
        return clients.get(index);
    }
}
