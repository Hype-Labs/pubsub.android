package hypelabs.com.hypepubsub;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.support.v7.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;
import java.util.ListIterator;
import java.util.ArrayList;


public class SubscriptionsListActivity extends AppCompatActivity
{
    ListView subscriptionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriptions_list);

        // Get ListView object from xml
        subscriptionsListView = (ListView) findViewById(R.id.subscriptionsList);

        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, getSubscriptionsStrings());
            subscriptionsListView.setAdapter(adapter);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getSubscriptionsStrings() throws NoSuchAlgorithmException {
        HypePubSub hpb = HypePubSub.getInstance();

        ArrayList<String> subs = new ArrayList<String>();

        ListIterator<Subscription> it = hpb.ownSubscriptions.listIterator();
        while(it.hasNext())
        {
            Subscription subscription = it.next();

            String manIdStr = BinaryUtils.byteArrayToHexString(subscription.managerId);
            String servKeyStr = BinaryUtils.byteArrayToHexString(subscription.serviceKey);
            subs.add("ServiceName: " + subscription.serviceName + "\n"
                     + "ServiceKey: 0x" + servKeyStr + "\n"
                     + "ManagerId: 0x" + manIdStr + "\n");
        }
        return subs;
    }
}
