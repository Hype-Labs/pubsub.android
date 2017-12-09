package hypelabs.com.hypepubsub;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.hypelabs.hype.Instance;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Subscription {

    String serviceName;
    byte serviceKey[];
    Client manager;
    ArrayList<String> receivedMsg;
    private ArrayAdapter<String> receivedMsgAdapter;

    public Subscription(String serviceName, Client manager) throws NoSuchAlgorithmException
    {
        this.serviceName = serviceName;
        this.serviceKey = HpsGenericUtils.stringHash(serviceName);
        this.manager = manager;
        this.receivedMsg = new ArrayList<>();
    }

    public ArrayAdapter<String> getReceivedMsgAdapter(Context context)
    {
        if (receivedMsgAdapter == null) {
            receivedMsgAdapter = new ArrayAdapter<>(context, R.layout.item_message, R.id.item_message_msg, receivedMsg);
        }
        return receivedMsgAdapter;
    }
}
