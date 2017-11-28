package hypelabs.com.hypepubsub;

import android.os.Bundle;
import android.widget.ListView;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;


public class ClientsListActivity extends AppCompatActivity
{
    private ListView clientsListView;
    private static WeakReference<ClientsListActivity> defaultInstance;
    private ClientsList activityClientsList = new ClientsList();
    private ClientsAdapter activityClientsAdapter;
    Network network = Network.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setTitle("Hype Devices");
        setContentView(R.layout.activity_clients_list);

        clientsListView = findViewById(R.id.activity_clients_list_view);
        setClientsAdapterFromNetworkClients();
        clientsListView.setAdapter(getClientsAdapter());

        setClientsListActivity(this);
    }

    private void setClientsAdapterFromNetworkClients()
    {
        ClientsAdapter clientAdapter = getClientsAdapter();
        clientAdapter.clear();
        for(int i=0; i<network.networkClients.size();i++) {
            clientAdapter.add(network.networkClients.get(i));
        }
    }

    private ClientsAdapter getClientsAdapter()
    {
        if (activityClientsAdapter == null) {
            activityClientsAdapter = activityClientsList.getClientsAdapter(ClientsListActivity.this);
        }
        return activityClientsAdapter;
    }

    public static ClientsListActivity getDefaultInstance() {

        return defaultInstance != null ? defaultInstance.get() : null;
    }

    private static void setClientsListActivity(ClientsListActivity instance) {

        defaultInstance = new WeakReference<>(instance);
    }

    protected void updateUI() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setClientsAdapterFromNetworkClients();
            }
        });
    }

}
