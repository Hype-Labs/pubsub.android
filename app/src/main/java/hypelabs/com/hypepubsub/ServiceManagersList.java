package hypelabs.com.hypepubsub;

import android.content.Context;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;


public class ServiceManagersList
{
    // Used composition instead of inheritance to hide the methods that shouldn't be called in
    // a ServiceManagersList.
    final private LinkedList<ServiceManager> serviceManagers = new LinkedList<>();
    private ServiceManagersAdapter serviceManagersAdapter = null;

    public synchronized boolean addServiceManager(ServiceManager serviceManager) {
        if (containsServiceManagerWithKey(serviceManager.serviceKey)) {
            return false;
        }
        return serviceManagers.add(serviceManager);
    }

    public synchronized boolean removeServiceManagerWithKey(byte serviceKey[]) {
        ServiceManager serviceMan = findServiceManagerWithKey(serviceKey);
        if (serviceMan == null) {
            return false;
        }
        return serviceManagers.remove(serviceMan);
    }

    public synchronized ServiceManager findServiceManagerWithKey(byte serviceKey[]) {
        ListIterator<ServiceManager> it = listIterator();
        while(it.hasNext()) {
            ServiceManager currentServiceMan = it.next();
            if(Arrays.equals(currentServiceMan.serviceKey, serviceKey)) {
                return currentServiceMan;
            }
        }
        return null;
    }

    public synchronized boolean containsServiceManagerWithKey(byte serviceKey[]) {
        if(findServiceManagerWithKey(serviceKey) == null) {
            return false;
        }
        return true;
    }

    public synchronized ServiceManagersAdapter getServiceManagersAdapter(Context context) {
        if(serviceManagersAdapter == null){
            serviceManagersAdapter = new ServiceManagersAdapter(context, serviceManagers);
        }
        return serviceManagersAdapter;
    }

    //////////////////////////////////////////////////////////////////////////////
    //  LinkedList methods to enable
    //////////////////////////////////////////////////////////////////////////////

    public synchronized ListIterator<ServiceManager> listIterator() {
        return serviceManagers.listIterator();
    }

    public synchronized int size() {
        return serviceManagers.size();
    }

    public synchronized ServiceManager get(int index)
    {
        return serviceManagers.get(index);
    }

    public synchronized ServiceManager getLast() {
        return serviceManagers.getLast();
    }

}
