package hypelabs.com.hypepubsub;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.support.v7.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ListIterator;
import java.util.ArrayList;


public class SubscriptionsListActivity extends AppCompatActivity
{
    private ListView subscriptionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setTitle("Subscriptions");
        setContentView(R.layout.activity_subscriptions_list);

        // Get ListView object from xml
        HypePubSub hpb = null;
        try
        {
            hpb = HypePubSub.getInstance();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        subscriptionsListView = findViewById(R.id.activity_subscriptions_list_view);
        subscriptionsListView.setAdapter(hpb.ownSubscriptions.getSubscriptionsAdapter(SubscriptionsListActivity.this));
    }
}
