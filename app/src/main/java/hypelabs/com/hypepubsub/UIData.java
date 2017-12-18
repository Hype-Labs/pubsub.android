package hypelabs.com.hypepubsub;


import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class UIData {

    public ArrayList<String> availableServices = new ArrayList<>(HpsConstants.STANDARD_HYPE_SERVICES);
    public ArrayList<String> unsubscribedServices = new ArrayList<>(HpsConstants.STANDARD_HYPE_SERVICES);
    public ArrayList<String> subscribedServices =  new ArrayList<>();

    public ArrayAdapter<String> availableServicesAdapter;
    public ArrayAdapter<String> unsubscribedServicesAdapter;
    public ArrayAdapter<String> subscribedServicesAdapter;

    public Boolean isToInitializeSdk = true;

    final private static UIData uiData = new UIData(); // Early loading to avoid thread-safety issues
    public static UIData getInstance() { return uiData; }


    public void addAvailableService(Context context, String serviceName)
    {
        if (!availableServices.contains(serviceName)) {
            availableServices.add(serviceName);
        }
        getAvailableServicesAdapter(context).notifyDataSetChanged();
    }

    public void addSubscribedService(Context context, String serviceName)
    {
        if (!subscribedServices.contains(serviceName)) {
            subscribedServices.add(serviceName);
        }
        getSubscribedServicesAdapter(context).notifyDataSetChanged();
    }

    public void addUnsubscribedService(Context context, String serviceName)
    {
        if (!unsubscribedServices.contains(serviceName)) {
            unsubscribedServices.add(serviceName);
        }
        getUnsubscribedServicesAdapter(context).notifyDataSetChanged();
    }

    public void removeAvailableService(Context context, String serviceName)
    {
        availableServices.remove(serviceName);
        getAvailableServicesAdapter(context).notifyDataSetChanged();
    }

    public void removeSubscribedService(Context context, String serviceName)
    {
        subscribedServices.remove(serviceName);
        getSubscribedServicesAdapter(context).notifyDataSetChanged();
    }

    public void removeUnsubscribedService(Context context, String serviceName)
    {
        unsubscribedServices.remove(serviceName);
        getUnsubscribedServicesAdapter(context).notifyDataSetChanged();
    }

    public ArrayAdapter<String> getAvailableServicesAdapter(Context context)
    {
        if (availableServicesAdapter == null) {
            availableServicesAdapter = new ArrayAdapter<>(context, R.layout.item_message, R.id.item_message_msg, availableServices);
        }
        return availableServicesAdapter;
    }

    public ArrayAdapter<String> getUnsubscribedServicesAdapter(Context context)
    {
        if (unsubscribedServicesAdapter == null) {
            unsubscribedServicesAdapter = new ArrayAdapter<>(context, R.layout.item_message, R.id.item_message_msg, unsubscribedServices);
        }
        return unsubscribedServicesAdapter;
    }

    public ArrayAdapter<String> getSubscribedServicesAdapter(Context context)
    {
        if (subscribedServicesAdapter == null) {
            subscribedServicesAdapter = new ArrayAdapter<>(context, R.layout.item_message, R.id.item_message_msg, subscribedServices);
        }
        return subscribedServicesAdapter;
    }
}
