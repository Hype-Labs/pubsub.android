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


public class ServiceManagersListActivity extends AppCompatActivity
{
    private ListView serviceManagersListView;
    private static WeakReference<ServiceManagersListActivity> defaultInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_managers_list);

        HypePubSub hpb = null;
        try
        {
            hpb = HypePubSub.getInstance();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        serviceManagersListView = findViewById(R.id.activity_service_manager_list_view);
        serviceManagersListView.setAdapter(hpb.managedServices.getServiceManagersAdapter(ServiceManagersListActivity.this));

        setServiceManagersListActivity(this);
    }

    public static ServiceManagersListActivity getDefaultInstance() {

        return defaultInstance != null ? defaultInstance.get() : null;
    }

    private static void setServiceManagersListActivity(ServiceManagersListActivity instance) {

        defaultInstance = new WeakReference<>(instance);
    }

    protected void updateInterface() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ServiceManagersAdapter)serviceManagersListView.getAdapter()).notifyDataSetChanged();
            }
        });
    }
}
