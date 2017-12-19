package hypelabs.com.hypepubsub;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;


public class MessagesActivity extends AppCompatActivity
{
    public final static String EXTRA_SUBSCRIPTION_KEY = "EXTRA_SUBSCRIPTION_KEY";
    private ListView messagesView;
    private static WeakReference<MessagesActivity> defaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        byte[] subscriptionKey = this.getIntent().getByteArrayExtra(EXTRA_SUBSCRIPTION_KEY);

        // Get ListView object from xml
        HypePubSub hps = HypePubSub.getInstance();
        Subscription subscription = hps.ownSubscriptions.findSubscriptionWithServiceKey(subscriptionKey);

        if(subscription == null) { // Protection against subscription not found..
            this.finish();
        }

        this.setTitle(subscription.serviceName + " messages");
        setContentView(R.layout.activity_messages);

        ArrayAdapter<String> receivedMsgAdapter = subscription.getReceivedMsgAdapter(MessagesActivity.this);

        messagesView = findViewById(R.id.activity_messages_view);
        messagesView.setAdapter(receivedMsgAdapter);

        setMessagesActivity(this);
    }

    public static MessagesActivity getDefaultInstance() {
        return defaultInstance != null ? defaultInstance.get() : null;
    }

    private static void setMessagesActivity(MessagesActivity instance) {
        defaultInstance = new WeakReference<>(instance);
    }

    protected void updateInterface() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ArrayAdapter) messagesView.getAdapter()).notifyDataSetChanged();
            }
        });
    }
}
