package hypelabs.com.hypepubsub;

import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;


public class ListServiceManagers extends LinkedList<ServiceManager>
{
    public int add(byte serviceKey[]) throws NoSuchAlgorithmException
    {
        if(this.find(serviceKey) != null)
            return -1;

        super.add(new ServiceManager(serviceKey));
        return 0;
    }

    public int remove(byte serviceKey[]) throws NoSuchAlgorithmException
    {
        ServiceManager servMan = this.find(serviceKey);
        if(servMan == null)
            return -1;

        super.remove(servMan);
        return 0;
    }

    public ServiceManager find(byte serviceKey[])
    {
        ListIterator<ServiceManager> it = this.listIterator();
        while(it.hasNext())
        {
            ServiceManager currentServMan = it.next();
            if(currentServMan.serviceKey.equals(serviceKey)) {
                return currentServMan;
            }
        }
        return null;
    }

}
