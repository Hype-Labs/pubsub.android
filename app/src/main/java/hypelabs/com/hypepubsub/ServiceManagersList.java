package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;


public class ServiceManagersList
{
    // Used composition instead of inheritance to hide the methods that shouldn't be called in
    // a ServiceManagersList.

    private LinkedList<ServiceManager> serviceManagers = new LinkedList<>();

    public int add(byte serviceKey[]) throws NoSuchAlgorithmException
    {
        if(find(serviceKey) != null)
            return -1;

        serviceManagers.add(new ServiceManager(serviceKey));
        return 0;
    }

    public int remove(byte serviceKey[]) throws NoSuchAlgorithmException
    {
        ServiceManager servMan = find(serviceKey);
        if(servMan == null)
            return -1;

        serviceManagers.remove(servMan);
        return 0;
    }

    public ServiceManager find(byte serviceKey[])
    {
        ListIterator<ServiceManager> it = listIterator();
        while(it.hasNext())
        {
            ServiceManager currentServMan = it.next();
            if(Arrays.equals(currentServMan.serviceKey, serviceKey)) {
                return currentServMan;
            }
        }
        return null;
    }

    // Methods from LinkedList that we want to enable.
    public ListIterator<ServiceManager> listIterator() {
        return serviceManagers.listIterator();
    }

    public int size() {
        return serviceManagers.size();
    }

    public ServiceManager get(int index) {
        return serviceManagers.get(index);
    }

    public ServiceManager getLast() {
        return serviceManagers.getLast();
    }

}
