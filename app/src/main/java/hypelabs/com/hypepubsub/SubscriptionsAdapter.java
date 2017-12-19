package hypelabs.com.hypepubsub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class SubscriptionsAdapter extends ArrayAdapter<Subscription>
{
    public SubscriptionsAdapter(Context context, LinkedList<Subscription> subscriptions) {
        super(context, 0, subscriptions);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Subscription subscription = getItem(position);
        if(subscription == null) {
            throw new NullPointerException("Subscription from SubscriptionsAdapter is null!");
        }

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_subscription, parent, false);
        }

        TextView serviceName = convertView.findViewById(R.id.item_subscription_service_name);
        TextView serviceKey = convertView.findViewById(R.id.item_subscription_service_key);

        serviceName.setText(subscription.serviceName);
        serviceKey.setText(HpsGenericUtils.getKeyStringFromSubscription(subscription));

        return convertView;
    }

}