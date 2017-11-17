package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;


public class ClientsList extends LinkedList<Client>
{
    public int add(byte clientId[]) throws NoSuchAlgorithmException
    {
        if(this.find(clientId) != null) // do not add the client if it is already present
            return -1;

        super.add(new Client(clientId));
        return 0;
    }

    public int remove(byte clientId[]) throws NoSuchAlgorithmException
    {
        Client client = this.find(clientId);
        if(client == null)
            return -1;

        super.remove(client);
        return 0;
    }

    public Client find(byte clientId[])
    {
        ListIterator<Client> it = this.listIterator();
        while(it.hasNext())
        {
            Client currentClient = it.next();
            if(Arrays.equals(currentClient.id, clientId)) {
                return currentClient;
            }
        }
        return null;
    }
}
