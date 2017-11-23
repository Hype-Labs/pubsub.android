package hypelabs.com.hypepubsub;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
    Button checkOwnIdButton;
    Button checkHypeDevicesButton;
    Button checkOwnSubscriptionsButton;
    Button checkManagedServicesButton;

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
        checkOwnIdButton = (Button) findViewById(R.id.checkOwnIdButton);
        checkHypeDevicesButton = (Button) findViewById(R.id.checkHypeDevicesButton);
        checkOwnSubscriptionsButton = (Button) findViewById(R.id.checkOwnSubscriptionsButton);
        checkManagedServicesButton = (Button) findViewById(R.id.checkManagedServicesButton);

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
                        {
                            if(hpb.ownSubscriptions.find(GenericUtils.getStrHash(service)) == null)
                                hpb.issueSubscribeReq(service);
                            else{
                                AlertDialogUtils.showOkDialog(MainActivity.this, "INFO", "Service already subscribed");
                                return;
                            }
                        }
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

    private void setListenerUnsubscribeButton()
    {

        unsubscribeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                if(hpb.ownSubscriptions.size() == 0){
                    AlertDialogUtils.showOkDialog(MainActivity.this, "INFO", "No services subscribed");
                    return;
                }

                AlertDialogUtils.ListViewInputDialog unsubscribeList = new AlertDialogUtils.ListViewInputDialog() {

                    @Override
                    public void onItemClick(Object listItem, Dialog dialog) throws IOException, NoSuchAlgorithmException
                    {
                        Subscription subscription = (Subscription) listItem;
                        hpb.issueUnsubscribeReq(subscription.serviceName);
                        dialog.dismiss();
                    }
                };

                SubscriptionsAdapter adapter = new SubscriptionsAdapter(MainActivity.this, hpb.ownSubscriptions.getLinkedListClone());

                AlertDialogUtils.showListViewInputDialog(MainActivity.this,
                        "UNSUBSCRIBE SERVICE" ,
                        adapter,
                        unsubscribeList);
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
                        "PUBLISH IN SERVICE" ,
                        "service",
                        "message",
                        publishInput);
            }
        });
    }

    private void setListenerOwnIdButton() throws NoSuchAlgorithmException
    {
        checkOwnIdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                try
                {
                    AlertDialogUtils.showOkDialog(MainActivity.this,"Own Device",
                                                GenericUtils.getInstanceAnnouncementStr (network.ownClient.instance) + "\n"
                                                + "Id: 0x" + BinaryUtils.byteArrayToHexString(network.ownClient.instance.getIdentifier()) + "\n"
                                                + "Key: 0x" + BinaryUtils.byteArrayToHexString(network.ownClient.key));
                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setListenerHypeDevicesButton() throws NoSuchAlgorithmException
    {
        final Intent intent = new Intent(this, HypeDevicesListActivity.class);

        checkHypeDevicesButton.setOnClickListener(new View.OnClickListener() {

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

        checkOwnSubscriptionsButton.setOnClickListener(new View.OnClickListener() {

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

        checkManagedServicesButton.setOnClickListener(new View.OnClickListener() {

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
