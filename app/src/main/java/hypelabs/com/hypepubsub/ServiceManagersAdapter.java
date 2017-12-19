package hypelabs.com.hypepubsub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class ServiceManagersAdapter extends ArrayAdapter<ServiceManager>
{
    public ServiceManagersAdapter(Context context, LinkedList<ServiceManager> serviceManagers) {
        super(context, 0, serviceManagers);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ServiceManager serviceManager = getItem(position);
        if(serviceManager == null) {
            throw new NullPointerException("ServiceManager from ServiceManagersAdapter is null!");
        }

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_service_manager, parent, false);
        }

        TextView serviceKey = convertView.findViewById(R.id.item_service_manager_service_key);
        serviceKey.setText(HpsGenericUtils.getKeyStringFromServiceManager(serviceManager));

        return convertView;
    }

}