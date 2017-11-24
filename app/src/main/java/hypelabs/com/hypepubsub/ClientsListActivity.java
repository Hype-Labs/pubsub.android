package hypelabs.com.hypepubsub;

import android.os.Bundle;
import android.widget.ListView;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;


public class ClientsListActivity extends AppCompatActivity
{
    private ListView clientsListView;
    private static WeakReference<ClientsListActivity> defaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setTitle("Hype Devices");
        setContentView(R.layout.activity_clients_list);

        Network network = Network.getInstance();
        clientsListView = findViewById(R.id.activity_clients_list_view);
        clientsListView.setAdapter(network.networkClients.getClientsAdapter(ClientsListActivity.this));

        setClientsListActivity(this);
    }

    public static ClientsListActivity getDefaultInstance() {

        return defaultInstance != null ? defaultInstance.get() : null;
    }

    private static void setClientsListActivity(ClientsListActivity instance) {

        defaultInstance = new WeakReference<>(instance);
    }

    protected void updateInterface() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ClientsAdapter) clientsListView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

}
