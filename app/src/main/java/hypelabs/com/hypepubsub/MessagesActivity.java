package hypelabs.com.hypepubsub;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class MessagesActivity extends AppCompatActivity
{
    private ListView messagesView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        String service = this.getIntent().getStringExtra("service");
        ArrayList<String> messages = this.getIntent().getStringArrayListExtra("messages");

        this.setTitle(service + " messages");
        setContentView(R.layout.activity_messages);

        ArrayAdapter receivedMsgAdapter = new ArrayAdapter(MessagesActivity.this, android.R.layout.simple_list_item_1, messages);

        messagesView = findViewById(R.id.activity_messages_view);
        messagesView.setAdapter(receivedMsgAdapter);
    }
}
