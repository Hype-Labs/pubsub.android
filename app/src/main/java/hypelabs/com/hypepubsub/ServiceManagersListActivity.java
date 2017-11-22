package hypelabs.com.hypepubsub;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.support.v7.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ListIterator;
import java.util.ArrayList;


public class ServiceManagersListActivity extends AppCompatActivity
{
    ListView serviceManagersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_managers_list);

        // Get ListView object from xml
        serviceManagersListView = (ListView) findViewById(R.id.serviceManagersList);

        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, getServiceManagersStrings());
            serviceManagersListView.setAdapter(adapter);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getServiceManagersStrings() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        HypePubSub hpb = HypePubSub.getInstance();

        ArrayList<String> servManagers = new ArrayList<String>();

        ListIterator<ServiceManager> itManServices = hpb.managedServices.listIterator();
        while(itManServices.hasNext())
        {
            ServiceManager servMan = itManServices.next();

            String servKeyStr = BinaryUtils.byteArrayToHexString(servMan.serviceKey);

            String subscribersStr = new String();
            subscribersStr = "";

            ListIterator<Client> itSubscribers = servMan.subscribers.listIterator();
            while(itSubscribers.hasNext())
            {
                Client client = itSubscribers.next();

                String clientId = BinaryUtils.byteArrayToHexString(client.instance.getIdentifier());
                String clientKey = BinaryUtils.byteArrayToHexString(client.key);
                String clientName = new String(client.instance.getAnnouncement(), Constants.HPB_ENCODING_STANDARD);

                subscribersStr += ("Client ID: 0x" + clientId + "\n"
                                   + "Client Key: 0x" + clientKey + "\n"
                                   + "Client Name: " + clientName + "\n");
            }

            servManagers.add("ServiceKey: 0x" + servKeyStr + "\n"
                             + subscribersStr);
        }

        return servManagers;
    }
}
