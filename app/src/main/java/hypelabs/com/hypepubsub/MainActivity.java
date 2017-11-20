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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
{
    HypePubSub hpb = HypePubSub.getInstance();
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

        try {
            addListenerOwnIdButton();
            addListenerHypeDevicesButton();
            addListenerOwnSubscriptionsButton();
            addListenerManagedServicesButton();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void addListenerSubscribeButton() {

        subscribeButton = (Button) findViewById(R.id.subscribeButton);
        serviceToSubscribe = (TextView) findViewById(R.id.subscribeText);

        AlertDialog.Builder noServiceBuilder = new AlertDialog.Builder(this);
        noServiceBuilder.setMessage("A service to subscribe must be specified")
                .setTitle("Warning");
        final AlertDialog noServiceAlert = noServiceBuilder.create();

        subscribeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
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
                    noServiceAlert.show();
                    Log.d(this.toString(), "A service to subscribe must be specified");
                }
            }
        });
    }

    public void addListenerUnsubscribeButton() {

        unsubscribeButton = (Button) findViewById(R.id.unsubscribeButton);
        serviceToUnsubscribe = (TextView) findViewById(R.id.unsubscribeText);

        AlertDialog.Builder noServiceBuilder = new AlertDialog.Builder(this);
        noServiceBuilder.setMessage("A service to unsubscribe must be specified")
                .setTitle("Warning");
        final AlertDialog noServiceAlert = noServiceBuilder.create();

        unsubscribeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
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
                    noServiceAlert.show();
                    Log.d(this.toString(), "A service to unsubscribe must be specified");
                }
            }
        });
    }

    public void addListenerPublishButton() {

        publishButton = (Button) findViewById(R.id.publishButton);
        serviceToPublish = (TextView) findViewById(R.id.publishServiceText);

        AlertDialog.Builder noServiceBuilder = new AlertDialog.Builder(this);
        noServiceBuilder.setMessage("A service in which to publish must be specified")
                .setTitle("Warning");
        final AlertDialog alert = noServiceBuilder.create();


        final AlertDialog.Builder messageBuilder = new AlertDialog.Builder(this);
        messageBuilder.setTitle("Insert Message");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        if(input.getParent()!=null)
            ((ViewGroup)input.getParent()).removeView(input); // <- fix
        messageBuilder.setView(input);

        // Set up the buttons
        messageBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
            }
        });
        messageBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        publishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if(serviceToPublish.getText().length() > 0){
                    messageBuilder.show();
                }
                else {
                    alert.show();
                    Log.d(this.toString(), "A service in which to publish and a message must be specified");
                }
            }
        });
    }

    public void addListenerOwnIdButton() throws NoSuchAlgorithmException
    {
        Network hpbNetwork = Network.getInstance();
        getOwnIdButton = (Button) findViewById(R.id.getOwnIdButton);

        StringBuilder idHexBuilder = new StringBuilder();
        for(byte b : hpbNetwork.ownClient.id) {
            idHexBuilder.append(String.format("%02x", b));
        }

        StringBuilder keyHexBuilder = new StringBuilder();
        for(byte b : hpbNetwork.ownClient.key) {
            keyHexBuilder.append(String.format("%02x", b));
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Own Device");
        alertDialog.setMessage("Id: 0x" + idHexBuilder.toString() + "\n"
                                + "Key: 0x" + keyHexBuilder.toString());
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        getOwnIdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                alertDialog.show();
            }
        });
    }

    public void addListenerHypeDevicesButton() throws NoSuchAlgorithmException
    {
        getHypeDevicesButton = (Button) findViewById(R.id.getHypeDevicesButton);
        final Intent intent = new Intent(this, HypeDevicesListActivity.class);

        getHypeDevicesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                startActivity(intent);
            }
        });
    }

    public void addListenerOwnSubscriptionsButton() throws NoSuchAlgorithmException
    {
        getOwnSubscriptionsButton = (Button) findViewById(R.id.getOwnSubscriptionsButton);
        final Intent intent = new Intent(this, SubscriptionsListActivity.class);

        getOwnSubscriptionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                startActivity(intent);
            }
        });
    }

    public void addListenerManagedServicesButton() throws NoSuchAlgorithmException
    {
        getManagedServicesButton = (Button) findViewById(R.id.getManagedServicesButton);
        final Intent intent = new Intent(this, ServiceManagersListActivity.class);

        getManagedServicesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                startActivity(intent);
            }
        });
    }
}
