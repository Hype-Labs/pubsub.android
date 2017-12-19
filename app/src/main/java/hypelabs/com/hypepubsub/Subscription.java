package hypelabs.com.hypepubsub;

import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

public class Subscription {

    final String serviceName;
    final byte serviceKey[];
    final ArrayList<String> receivedMsg;
    Client manager;
    private ArrayAdapter<String> receivedMsgAdapter;

    public Subscription(String serviceName, Client manager) {
        this.serviceName = serviceName;
        this.serviceKey = HpsGenericUtils.stringHash(serviceName);
        this.manager = manager;
        this.receivedMsg = new ArrayList<>();
    }

    public ArrayAdapter<String> getReceivedMsgAdapter(Context context) {
        if (receivedMsgAdapter == null) {
            receivedMsgAdapter = new ArrayAdapter<>(context, R.layout.item_message, R.id.item_message_msg, receivedMsg);
        }
        return receivedMsgAdapter;
    }
}
