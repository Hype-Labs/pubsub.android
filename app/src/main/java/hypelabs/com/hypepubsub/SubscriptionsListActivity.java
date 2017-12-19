package hypelabs.com.hypepubsub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v7.app.AppCompatActivity;

public class SubscriptionsListActivity extends AppCompatActivity
{
    private ListView subscriptionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Subscriptions");
        setContentView(R.layout.activity_subscriptions_list);

        // Get ListView object from xml
        HypePubSub hps = HypePubSub.getInstance();

        subscriptionsListView = findViewById(R.id.activity_subscriptions_list_view);
        subscriptionsListView.setAdapter(hps.ownSubscriptions.getSubscriptionsAdapter(SubscriptionsListActivity.this));

        subscriptionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Subscription subscription = (Subscription) subscriptionsListView.getItemAtPosition(position);

                Intent intent = new Intent(SubscriptionsListActivity.this, MessagesActivity.class);

                intent.putExtra(MessagesActivity.EXTRA_SUBSCRIPTION_KEY, subscription.serviceKey);

                startActivity(intent);

            }
        });
    }
}
