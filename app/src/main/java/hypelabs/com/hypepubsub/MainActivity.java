package hypelabs.com.hypepubsub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
{
    HypePubSub hpb = HypePubSub.getInstance();
    Button subscribeButton;
    Button unsubscribeButton;
    Button publishButton;

    TextView serviceToSubscribe;
    TextView serviceToUnsubscribe;
    TextView serviceToPublish;
    TextView messageToPublish;

    public MainActivity() throws NoSuchAlgorithmException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addListenerSubscribeButton();
        addListenerUnsubscribeButton();
        addListenerPublishButton();
    }

    public void addListenerSubscribeButton() {

        subscribeButton = (Button) findViewById(R.id.subscribeButton);
        serviceToSubscribe = (TextView) findViewById(R.id.subscribeText);
        subscribeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if(serviceToSubscribe.getText().length() > 0 )
                {
                    try
                    {
                        hpb.issueSubscribeReq(serviceToSubscribe.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d(this.toString(), "A service to subscribe must be specified");
                }

            }
        });
    }

    public void addListenerUnsubscribeButton() {

        unsubscribeButton = (Button) findViewById(R.id.unsubscribeButton);
        serviceToUnsubscribe = (TextView) findViewById(R.id.unsubscribeText);
        unsubscribeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if(serviceToUnsubscribe.getText().length() > 0 )
                {
                    try {
                        hpb.issueUnsubscribeReq(serviceToUnsubscribe.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d(this.toString(), "A service to unsubscribe must be specified");
                }
            }
        });
    }

    public void addListenerPublishButton() {

        publishButton = (Button) findViewById(R.id.publishButton);
        serviceToPublish = (TextView) findViewById(R.id.publishServiceText);
        messageToPublish = (TextView) findViewById(R.id.publishMsgText);

        publishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if(serviceToPublish.getText().length() > 0 && messageToPublish.getText().length() > 0)
                {
                    try
                    {
                        hpb.issuePublishReq(serviceToPublish.getText().toString(), messageToPublish.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d(this.toString(), "A service in which to publish and a message must be specified");
                }
            }
        });
    }
}
