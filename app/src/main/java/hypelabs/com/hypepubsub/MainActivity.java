package hypelabs.com.hypepubsub;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.InputType;

import com.hypelabs.hype.Hype;
import com.hypelabs.hype.State;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
{
    HypePubSub hpb = null;
    Network network = null;

    Button subscribeButton;
    Button unsubscribeButton;
    Button publishButton;
    Button getOwnIdButton;
    Button getHypeDevicesButton;
    Button getOwnSubscriptionsButton;
    Button getManagedServicesButton;

    TextView serviceToSubscribe;
    TextView serviceToUnsubscribe;
    TextView serviceToPublish;

    public MainActivity() throws NoSuchAlgorithmException {}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HypeSdkInterface hypeSdkInterface = HypeSdkInterface.getInstance();
        hypeSdkInterface.requestHypeToStart(getApplicationContext());

        try
        {
            // Get Singletons
            hpb = HypePubSub.getInstance();
            network = Network.getInstance();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        setButtonListeners();
    }

    private void setButtonListeners()
    {
        setListenerSubscribeButton();
        setListenerUnsubscribeButton();
        setListenerPublishButton();

        try
        {
            setListenerOwnIdButton();
            setListenerHypeDevicesButton();
            setListenerOwnSubscriptionsButton();
            setListenerManagedServicesButton();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    private void setListenerSubscribeButton() {

        subscribeButton = (Button) findViewById(R.id.subscribeButton);
        serviceToSubscribe = (TextView) findViewById(R.id.subscribeText);

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                if(serviceToSubscribe.getText().length() > 0 )
                {
                    try
                    {
                        String service = serviceToSubscribe.getText().toString().toLowerCase();
                        hpb.issueSubscribeReq(service);
                        serviceToSubscribe.setText("");
                        Log.d(this.toString(), "Subscribe service " + service);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    displayAlertDialog("Warning", "A service to subscribe must be specified");
                    Log.d(this.toString(), "A service to subscribe must be specified");
                }
            }
        });
    }

    private void setListenerUnsubscribeButton() {

        unsubscribeButton = (Button) findViewById(R.id.unsubscribeButton);
        serviceToUnsubscribe = (TextView) findViewById(R.id.unsubscribeText);

        unsubscribeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                if(serviceToUnsubscribe.getText().length() > 0 )
                {
                    try {
                        String service = serviceToUnsubscribe.getText().toString().toLowerCase();
                        hpb.issueUnsubscribeReq(service);
                        serviceToUnsubscribe.setText("");
                        Log.d(this.toString(), "Unsubscribe service " + service);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    displayAlertDialog("Warning", "A service to unsubscribe must be specified");
                    Log.d(this.toString(), "A service to unsubscribe must be specified");
                }
            }
        });
    }

    private void setListenerPublishButton() {

        final EditText input = new EditText(this);

        publishButton = (Button) findViewById(R.id.publishButton);
        serviceToPublish = (TextView) findViewById(R.id.publishServiceText);
        publishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                if(serviceToPublish.getText().length() > 0){
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(input.getParent()!=null)
                                ((ViewGroup)input.getParent()).removeView(input); // <- fix

                            if (!isFinishing()){
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Insert Message")
                                        .setCancelable(false)
                                        .setView(input)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if(input.length() > 0) {
                                                    try {
                                                        String service = serviceToPublish.getText().toString().toLowerCase();
                                                        hpb.issuePublishReq(service, input.getText().toString());
                                                        Log.d(this.toString(), "Published in service " + service
                                                                + ": " + input.getText().toString());
                                                    } catch (NoSuchAlgorithmException e) {
                                                        e.printStackTrace();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                serviceToPublish.setText("");
                                                input.setText("");
                                            }
                                        }).show();
                            }
                        }
                    });
                }
                else {
                    displayAlertDialog("Warning", "A service in which to publish must be specified");
                    Log.d(this.toString(), "A service in which to publish and a message must be specified");
                }
            }
        });
    }

    private void setListenerOwnIdButton() throws NoSuchAlgorithmException
    {
        getOwnIdButton = (Button) findViewById(R.id.getOwnIdButton);
        getOwnIdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                displayAlertDialog("Own Device", "Id: 0x" + BinaryUtils.byteArrayToHexString(network.ownClient.instance.getIdentifier()) + "\n"
                                                            + "Key: 0x" + BinaryUtils.byteArrayToHexString(network.ownClient.key));
            }
        });
    }

    private void setListenerHypeDevicesButton() throws NoSuchAlgorithmException
    {
        getHypeDevicesButton = (Button) findViewById(R.id.getHypeDevicesButton);
        final Intent intent = new Intent(this, HypeDevicesListActivity.class);

        getHypeDevicesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                startActivity(intent);
            }
        });
    }

    private void setListenerOwnSubscriptionsButton() throws NoSuchAlgorithmException
    {
        getOwnSubscriptionsButton = (Button) findViewById(R.id.getOwnSubscriptionsButton);
        final Intent intent = new Intent(this, SubscriptionsListActivity.class);

        getOwnSubscriptionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                startActivity(intent);
            }
        });
    }

    private void setListenerManagedServicesButton() throws NoSuchAlgorithmException
    {
        getManagedServicesButton = (Button) findViewById(R.id.getManagedServicesButton);
        final Intent intent = new Intent(this, ServiceManagersListActivity.class);

        getManagedServicesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                startActivity(intent);
            }
        });
    }

    public void displayAlertDialog(String title, String msg)
    {
        final String finalTitle = title;
        final String finalMsg = msg;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(finalTitle)
                            .setMessage(finalMsg)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            }).show();
                }
            }
        });
    }

    public boolean isHypeSdkReady()
    {
        if(HypeSdkInterface.isHypeFail){
            displayAlertDialog("Warning", "Hype SDK could not be started");
            return false;
        }
        else if( ! HypeSdkInterface.isHypeReady){
            displayAlertDialog("Warning", "Hype SDK is not ready yet");
            return false;
        }

        return true;
    }
}
