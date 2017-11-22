package hypelabs.com.hypepubsub;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.support.v7.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;
import java.util.ListIterator;
import java.util.ArrayList;


public class HypeDevicesListActivity extends AppCompatActivity
{
    ListView hypeDevicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hype_devices_list);

        // Get ListView object from xml
        hypeDevicesListView = (ListView) findViewById(R.id.hypeDevicesList);

        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, getHypeDevicesStrings());
            hypeDevicesListView.setAdapter(adapter);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getHypeDevicesStrings() throws NoSuchAlgorithmException
    {
        Network hpbNetwork = Network.getInstance();

        ArrayList<String> hypeDevices = new ArrayList<String>();

        ListIterator<Client> it = hpbNetwork.networkClients.listIterator();
        while(it.hasNext())
        {
            Client client = it.next();

            String clientId = BinaryUtils.byteArrayToHexString(client.instance.getIdentifier());
            String clientKey = BinaryUtils.byteArrayToHexString(client.key);
            hypeDevices.add("ClientID: 0x" + clientId + "\n"
                            + "ClientKey: 0x" + clientKey + "\n");
        }

        return hypeDevices;
    }
}
