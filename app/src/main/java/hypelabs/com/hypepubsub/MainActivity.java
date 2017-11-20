package hypelabs.com.hypepubsub;

import android.app.Dialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity
{
    HypePubSub hpb = HypePubSub.getInstance();
    Button subscribeButton;
    Button unsubscribeButton;
    Button publishButton;
    Button getOwnIdButton;
    Button getHypeDevicesButton;
    Button getOwnSubscriptions;
    Button getManagedServices;


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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        addListenerHypeDevicesButton();
        addListenerOwnSubscriptionsButton();
        addListenerManagedServicesButton();
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
        Network hpbNetwork = Network.getInstance();

        ListIterator<Client> it = hpbNetwork.networkClients.listIterator();
        while(it.hasNext())
        {
            Client client = it.next();

            byteArrayToHexString(client.id);
            byteArrayToHexString(client.key);
        }
    }

    public void addListenerOwnSubscriptionsButton() throws NoSuchAlgorithmException
    {
        HypePubSub hpb = HypePubSub.getInstance();

        ListIterator<Subscription> it = hpb.ownSubscriptions.listIterator();
        while(it.hasNext())
        {
            Subscription subscription = it.next();

            byteArrayToHexString(subscription.managerId);
            byteArrayToHexString(subscription.serviceKey);
            //subscription.serviceName;

        }
    }

    public void addListenerManagedServicesButton() throws NoSuchAlgorithmException
    {
        HypePubSub hpb = HypePubSub.getInstance();

        ListIterator<ServiceManager> itManServices = hpb.managedServices.listIterator();
        while(itManServices.hasNext())
        {
            ServiceManager servMan = itManServices.next();

            byteArrayToHexString(servMan.serviceKey);

            ListIterator<Client> itSubscribers = servMan.subscribers.listIterator();
            while(itSubscribers.hasNext())
            {
                Client client = itSubscribers.next();

                byteArrayToHexString(client.id);
                byteArrayToHexString(client.key);
            }
        }
    }

    public String byteArrayToHexString(byte array[])
    {
        StringBuilder builder = new StringBuilder();
        for(byte b : array) {
            builder.append(String.format("%02x", b));
        }

        return builder.toString();
    }
}
