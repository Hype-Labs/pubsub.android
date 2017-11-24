package hypelabs.com.hypepubsub;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.support.v7.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.util.ListIterator;
import java.util.ArrayList;


public class HypeDevicesListActivity extends AppCompatActivity
{
    private ListView hypeDevicesListView;
    private static WeakReference<HypeDevicesListActivity> defaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hype_devices_list);

        Network network = Network.getInstance();
        hypeDevicesListView = findViewById(R.id.activity_hype_devices_list_view);
        hypeDevicesListView.setAdapter(network.networkClients.getClientsAdapter(HypeDevicesListActivity.this));

        setHypeDevicesListActivity(this);
    }

    public static HypeDevicesListActivity getDefaultInstance() {

        return defaultInstance != null ? defaultInstance.get() : null;
    }

    private static void setHypeDevicesListActivity(HypeDevicesListActivity instance) {

        defaultInstance = new WeakReference<>(instance);
    }

    protected void updateInterface() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ClientsAdapter)hypeDevicesListView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

}
