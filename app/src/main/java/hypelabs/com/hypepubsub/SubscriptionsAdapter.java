package hypelabs.com.hypepubsub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class SubscriptionsAdapter extends ArrayAdapter<Subscription>
{
    public SubscriptionsAdapter(Context context, LinkedList<Subscription> subscriptions)
    {
        super(context, 0, subscriptions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get the data item for this position
        Subscription subscription = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_subscription, parent, false);
        }

        // Lookup view for data population
        TextView serviceName = (TextView) convertView.findViewById(R.id.serviceName);

        // Populate the data into the template view using the data object
        serviceName.setText(subscription.serviceName);

        // Return the completed view to render on screen
        return convertView;
    }

}