package hypelabs.com.hypepubsub;


import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * This class holds the data structures required to control the UI. These data structure were not defined as members
 * of the MainActivity to avoid complications due to the Android activities lifetime cycle.
 */
public class UIData {

    final private ArrayList<String> availableServices = new ArrayList<>(HpsConstants.STANDARD_HYPE_SERVICES);
    final private ArrayList<String> unsubscribedServices = new ArrayList<>(HpsConstants.STANDARD_HYPE_SERVICES);
    final private ArrayList<String> subscribedServices =  new ArrayList<>();
    private ArrayAdapter<String> availableServicesAdapter;
    private ArrayAdapter<String> unsubscribedServicesAdapter;
    private ArrayAdapter<String> subscribedServicesAdapter;
    public Boolean isToInitializeSdk = true;

    final private static UIData uiData = new UIData(); // Early loading to avoid thread-safety issues
    public static UIData getInstance() {
        return uiData;
    }

    public void addAvailableService(Context context, String serviceName) {
        if (!availableServices.contains(serviceName)) {
            availableServices.add(serviceName);
        }
        getAvailableServicesAdapter(context).notifyDataSetChanged();
    }

    public void addSubscribedService(Context context, String serviceName) {
        if (!subscribedServices.contains(serviceName)) {
            subscribedServices.add(serviceName);
        }
        getSubscribedServicesAdapter(context).notifyDataSetChanged();
    }

    public void addUnsubscribedService(Context context, String serviceName) {
        if (!unsubscribedServices.contains(serviceName)) {
            unsubscribedServices.add(serviceName);
        }
        getUnsubscribedServicesAdapter(context).notifyDataSetChanged();
    }

    public void removeSubscribedService(Context context, String serviceName) {
        subscribedServices.remove(serviceName);
        getSubscribedServicesAdapter(context).notifyDataSetChanged();
    }

    public void removeUnsubscribedService(Context context, String serviceName) {
        unsubscribedServices.remove(serviceName);
        getUnsubscribedServicesAdapter(context).notifyDataSetChanged();
    }

    public ArrayAdapter<String> getAvailableServicesAdapter(Context context) {
        if (availableServicesAdapter == null) {
            availableServicesAdapter = new ArrayAdapter<>(context, R.layout.item_message, R.id.item_message_msg, availableServices);
        }
        return availableServicesAdapter;
    }

    public ArrayAdapter<String> getUnsubscribedServicesAdapter(Context context) {
        if (unsubscribedServicesAdapter == null) {
            unsubscribedServicesAdapter = new ArrayAdapter<>(context, R.layout.item_message, R.id.item_message_msg, unsubscribedServices);
        }
        return unsubscribedServicesAdapter;
    }

    public ArrayAdapter<String> getSubscribedServicesAdapter(Context context) {
        if (subscribedServicesAdapter == null) {
            subscribedServicesAdapter = new ArrayAdapter<>(context, R.layout.item_message, R.id.item_message_msg, subscribedServices);
        }
        return subscribedServicesAdapter;
    }

    public int getNumberOfSubscribedServices(){
        return subscribedServices.size();
    }
}
