package hypelabs.com.hypepubsub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;

public class ClientsAdapter extends ArrayAdapter<Client>
{
    public ClientsAdapter(Context context, LinkedList<Client> clients) {
        super(context, 0, clients);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Client client = getItem(position);
        if (client == null) {
            throw new NullPointerException("Client from ClientsAdapter is null!");
        }

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_client, parent, false);
        }

        TextView clientName = convertView.findViewById(R.id.item_client_name);
        TextView clientId = convertView.findViewById(R.id.item_client_id);
        TextView clientKey = convertView.findViewById(R.id.item_client_key);

        clientName.setText(HpsGenericUtils.getInstanceAnnouncementStr(client.instance));
        clientId.setText(HpsGenericUtils.getIdStringFromClient(client));
        clientKey.setText(HpsGenericUtils.getKeyStringFromClient(client));

        return convertView;
    }

}