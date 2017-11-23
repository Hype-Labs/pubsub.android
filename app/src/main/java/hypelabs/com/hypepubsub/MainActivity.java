package hypelabs.com.hypepubsub;

import android.app.AlertDialog;
import android.content.Context;
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
import java.io.UnsupportedEncodingException;
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

    TextView serviceToUnsubscribe;
    TextView serviceToPublish;

    public MainActivity() throws NoSuchAlgorithmException {}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HypeSdkInterface hypeSdkInterface = HypeSdkInterface.getInstance();
        try {
            hypeSdkInterface.requestHypeToStart(getApplicationContext());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

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
        subscribeButton = (Button) findViewById(R.id.subscribeButton);
        unsubscribeButton = (Button) findViewById(R.id.unsubscribeButton);
        publishButton = (Button) findViewById(R.id.publishButton);
        getOwnIdButton = (Button) findViewById(R.id.getOwnIdButton);
        getHypeDevicesButton = (Button) findViewById(R.id.getHypeDevicesButton);
        getOwnSubscriptionsButton = (Button) findViewById(R.id.getOwnSubscriptionsButton);
        getManagedServicesButton = (Button) findViewById(R.id.getManagedServicesButton);

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

        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                AlertDialogUtils.SingleInputDialog subscribeInput = new AlertDialogUtils.SingleInputDialog() {

                    @Override
                    public void actionOk(String service) throws IOException, NoSuchAlgorithmException
                    {
                        service = service.toLowerCase().trim();
                        if(service.length() > 0)
                            hpb.issueSubscribeReq(service);
                    }

                    @Override
                    public void actionCancel() {
                        // do nothing;
                    }
                };

                AlertDialogUtils.showSingleInputDialog(MainActivity.this,
                                                        "SUBSCRIBE SERVICE" ,
                                                        "service",
                                                        subscribeInput);

            }
        });
    }

    private void setListenerUnsubscribeButton() {

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
                    AlertDialogUtils.showOkDialog(MainActivity.this, "Warning", "A service to unsubscribe must be specified");
                    Log.d(this.toString(), "A service to unsubscribe must be specified");
                }
            }
        });
    }

    private void setListenerPublishButton() {

        publishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                AlertDialogUtils.DoubleInputDialog publishInput = new AlertDialogUtils.DoubleInputDialog() {

                    @Override
                    public void actionOk(String service, String msg) throws IOException, NoSuchAlgorithmException
                    {
                        service = service.toLowerCase().trim();
                        msg = msg.trim();
                        if(service.length() > 0 && msg.length() > 0)
                            hpb.issuePublishReq(service, msg);
                        else
                            AlertDialogUtils.showOkDialog(MainActivity.this,
                                                            "WARNING",
                                                            "A service and a message must be specified");
                    }

                    @Override
                    public void actionCancel() {
                        // do nothing;
                    }
                };

                AlertDialogUtils.showDoubleInputDialog(MainActivity.this,
                        "PUBLISH" ,
                        "service",
                        "message",
                        publishInput);
            }
        });
    }

    private void setListenerOwnIdButton() throws NoSuchAlgorithmException
    {
        getOwnIdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                AlertDialogUtils.showOkDialog(MainActivity.this,"Own Device", "Id: 0x" + BinaryUtils.byteArrayToHexString(network.ownClient.instance.getIdentifier()) + "\n"
                                                            + "Key: 0x" + BinaryUtils.byteArrayToHexString(network.ownClient.key));
            }
        });
    }

    private void setListenerHypeDevicesButton() throws NoSuchAlgorithmException
    {
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

    public boolean isHypeSdkReady()
    {
        if(HypeSdkInterface.isHypeFail){
            AlertDialogUtils.showOkDialog(MainActivity.this, "Warning", "Hype SDK could not be started");
            return false;
        }
        else if( ! HypeSdkInterface.isHypeReady){
            AlertDialogUtils.showOkDialog(MainActivity.this, "Warning", "Hype SDK is not ready yet");
            return false;
        }

        return true;
    }
}
