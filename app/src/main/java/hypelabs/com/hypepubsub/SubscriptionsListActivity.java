package hypelabs.com.hypepubsub;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
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

        subscriptionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Subscription subscription = (Subscription) subscriptionsListView.getItemAtPosition(position);;

                ArrayAdapter receivedMsgAdapter = new ArrayAdapter(SubscriptionsListActivity.this, android.R.layout.simple_list_item_1, subscription.receivedMsg);

                AlertDialogUtils.showListViewDialog(SubscriptionsListActivity.this,
                        subscription.serviceName + " service",
                        receivedMsgAdapter);
            }
        });
    }
}
