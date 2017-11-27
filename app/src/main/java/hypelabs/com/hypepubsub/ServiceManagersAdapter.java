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
    public ServiceManagersAdapter(Context context, LinkedList<ServiceManager> serviceManagers)
    {
        super(context, 0, serviceManagers);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        // Get the data item for this position
        ServiceManager serviceManager = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_service_manager, parent, false);
        }

        // Lookup view for data population
        TextView serviceKey = convertView.findViewById(R.id.item_service_manager_service_key);

        if(serviceManager != null)
        {
            // Populate the data into the template view using the data object
            serviceKey.setText(BinaryUtils.byteArrayToHexString(serviceManager.serviceKey));
        }

        // Return the completed view to render on screen
        return convertView;
    }

}