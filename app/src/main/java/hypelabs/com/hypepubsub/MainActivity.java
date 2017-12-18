package hypelabs.com.hypepubsub;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity
{
    private HypePubSub hps = HypePubSub.getInstance();
    private Network network = Network.getInstance();
    private HypeSdkInterface hypeSdk = HypeSdkInterface.getInstance();
    private UIData uiData = UIData.getInstance();

    private Button subscribeButton;
    private Button unsubscribeButton;
    private Button publishButton;
    private Button checkOwnIdButton;
    private Button checkHypeDevicesButton;
    private Button checkOwnSubscriptionsButton;
    private Button checkManagedServicesButton;

    private static MainActivity instance; // Way of accessing the application context from other classes

    public MainActivity() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtonsFromResourceIDs();
        setButtonListeners();

        if(uiData.isToInitializeSdk) {
            initHypeSdk();
            uiData.isToInitializeSdk = false;
        }
    }

    private void initHypeSdk()
    {
        HypeSdkInterface hypeSdkInterface = HypeSdkInterface.getInstance();
        hypeSdkInterface.requestHypeToStart(getApplicationContext());
    }

    private void setButtonListeners()
    {
        setListenerSubscribeButton();
        setListenerUnsubscribeButton();
        setListenerPublishButton();
        setListenerCheckOwnIdButton();
        setListenerCheckHypeDevicesButton();
        setListenerCheckOwnSubscriptionsButton();
        setListenerCheckManagedServicesButton();
    }

    private void initButtonsFromResourceIDs()
    {
        subscribeButton = findViewById(R.id.activity_main_subscribe_button);
        unsubscribeButton = findViewById(R.id.activity_main_unsubscribe_button);
        publishButton = findViewById(R.id.activity_main_publish_button);
        checkOwnIdButton = findViewById(R.id.activity_main_check_own_id_button);
        checkHypeDevicesButton = findViewById(R.id.activity_main_check_hype_devices_button);
        checkOwnSubscriptionsButton = findViewById(R.id.activity_main_check_own_subscriptions_button);
        checkManagedServicesButton = findViewById(R.id.activity_main_check_managed_services_button);
    }

    //////////////////////////////////////////////////////////////////////////////
    // Button Listener Methods
    //////////////////////////////////////////////////////////////////////////////

    private void setListenerSubscribeButton()
    {
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                if( !isHypeSdkReady()){
                    return;
                }

                IOnServiceSelection onSubscribeServiceSelection = new IOnServiceSelection() {
                    @Override
                    public void action(String input) {
                        processUserSubscribeAction(input);
                    }
                };

                displayServicesNamesList(MainActivity.this,
                        "Subscribe",
                        "Select a service to subscribe",
                        uiData.getUnsubscribedServicesAdapter(MainActivity.this),
                        onSubscribeServiceSelection,
                        true);
            }
        });
    }

    private void setListenerUnsubscribeButton()
    {
        unsubscribeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( !isHypeSdkReady()){
                    return;
                }

                if(uiData.subscribedServices.size() == 0){
                    AlertDialogUtils.showOkDialog(MainActivity.this,
                            "INFO", "No services subscribed");
                    return;
                }

                IOnServiceSelection onUnsubscribeServiceSelection = new IOnServiceSelection() {
                    @Override
                    public void action(String input) {
                        processUserUnsubscribeAction(input);
                    }
                };

                displayServicesNamesList(MainActivity.this,
                        "Unsubscribe",
                        "Select a service to unsubscribe",
                        uiData.getSubscribedServicesAdapter(MainActivity.this),
                        onUnsubscribeServiceSelection,
                        false);
            }
        });
    }

    private void setListenerPublishButton()
    {
        publishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if( !isHypeSdkReady()){
                    return;
                }

                IOnServiceSelection onPublishServiceSelection = new IOnServiceSelection() {
                    @Override
                    public void action(String input) {
                        processUserPublishAction(input);
                    }
                };

                displayServicesNamesList(MainActivity.this,
                        "Publish",
                        "Select a service in which to publish",
                        uiData.getAvailableServicesAdapter(MainActivity.this),
                        onPublishServiceSelection,
                        true);
            }
        });
    }

    private void setListenerCheckOwnIdButton()
    {
        checkOwnIdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                AlertDialogUtils.showOkDialog(MainActivity.this,"Own Device",
                        HpsGenericUtils.getInstanceAnnouncementStr(network.ownClient.instance) + "\n"
                                + HpsGenericUtils.getIdStringFromClient(network.ownClient) + "\n"
                                + HpsGenericUtils.getKeyStringFromClient(network.ownClient));
            }
        });
    }

    private void setListenerCheckHypeDevicesButton()
    {
        final Intent intent = new Intent(this, ClientsListActivity.class);

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

    private void setListenerCheckOwnSubscriptionsButton()
    {
        final Intent intent = new Intent(this, SubscriptionsListActivity.class);

        checkOwnSubscriptionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }

                if(hps.ownSubscriptions.size() == 0){
                    AlertDialogUtils.showOkDialog(MainActivity.this,
                            "INFO", "No services subscribed");
                    return;
                }

                startActivity(intent);
            }
        });
    }

    private void setListenerCheckManagedServicesButton()
    {
        final Intent intent = new Intent(this, ServiceManagersListActivity.class);

        checkManagedServicesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( ! isHypeSdkReady()){
                    return;
                }
                MainActivity.this.
                startActivity(intent);
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////
    // User Action Processing Methods
    //////////////////////////////////////////////////////////////////////////////

    private void displayServicesNamesList(Context context,
                                          String title,
                                          String message,
                                          ListAdapter adapter,
                                          final IOnServiceSelection onServiceSelection,
                                          Boolean isNewServiceSelectionAllowed)
    {
        final ListView listView = new ListView(context);
        listView.setAdapter(adapter);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listView);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setView(layout);
        builder.setMessage(message);
        builder.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });

        if(isNewServiceSelectionAllowed) {
            builder.setNeutralButton("New Service",
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processUserNewServiceSelection(onServiceSelection);
                        dialog.dismiss();
                    }
                });
        }

        final Dialog dialog = builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String listItem = (String) listView.getItemAtPosition(position);
                onServiceSelection.action(listItem);
                dialog.dismiss();
            }
        });
    }

    private interface IOnServiceSelection
    {
        void action(String serviceName);
    }

    private void processUserSubscribeAction(String userInput)
    {
        String serviceName = processUserServiceNameInput(userInput);
        if(serviceName.length() == 0) {
            return;
        }

        if(hps.ownSubscriptions.containsSubscriptionWithServiceName(serviceName))
        {
            AlertDialogUtils.showOkDialog(MainActivity.this,
                    "INFO", "Service already subscribed");
        }
        else {
            boolean wasSubscribed = hps.issueSubscribeReq(serviceName);
            if(wasSubscribed) {
                uiData.addSubscribedService(MainActivity.this, serviceName);
                uiData.removeUnsubscribedService(MainActivity.this, serviceName);
            }
        }
    }

    private void processUserUnsubscribeAction(String userInput)
    {
        String serviceName = processUserServiceNameInput(userInput);
        if(serviceName.length() == 0) {
            return;
        }

        boolean wasUnsubscribed = hps.issueUnsubscribeReq(serviceName);
        if(wasUnsubscribed) {
            uiData.addUnsubscribedService(MainActivity.this, serviceName);
            uiData.removeSubscribedService(MainActivity.this, serviceName);
        }
    }

    private void processUserPublishAction(String userInput)
    {
        final String serviceName = processUserServiceNameInput(userInput);
        if(serviceName.length() == 0) {
            return;
        }

        AlertDialogUtils.ISingleInputDialog publishMsgInput = new AlertDialogUtils.ISingleInputDialog() {

            @Override
            public void onOk(String msg) throws IOException, NoSuchAlgorithmException
            {
                msg = msg.trim();
                if(msg.length() > 0)
                    hps.issuePublishReq(serviceName, msg);
                else
                    AlertDialogUtils.showOkDialog(MainActivity.this,
                            "WARNING",
                            "A message must be specified");
            }

            @Override
            public void onCancel() {}
        };

        AlertDialogUtils.showSingleInputDialog(MainActivity.this,
                "Publish",
                "Insert message to publish in the service: " + serviceName,
                "message",
                publishMsgInput);
    }

    private void processUserNewServiceSelection(final IOnServiceSelection onServiceSelection)
    {
        AlertDialogUtils.ISingleInputDialog newServiceInput = new AlertDialogUtils.ISingleInputDialog() {

            @Override
            public void onOk(String input) throws IOException, NoSuchAlgorithmException {
                String serviceName = processUserServiceNameInput(input);
                uiData.addAvailableService(MainActivity.this, serviceName);
                uiData.addUnsubscribedService(MainActivity.this, serviceName);

                onServiceSelection.action(serviceName);
            }

            @Override
            public void onCancel() {}
        };

        AlertDialogUtils.showSingleInputDialog(MainActivity.this,
                "New Service",
                "Specify new service",
                "service",
                newServiceInput);
    }

    //////////////////////////////////////////////////////////////////////////////
    // Utilities
    //////////////////////////////////////////////////////////////////////////////

    private boolean isHypeSdkReady()
    {
        if(hypeSdk.hasHypeFailed){
            AlertDialogUtils.showOkDialog(MainActivity.this,
                    "Error", "Hype SDK could not be started.\n" + hypeSdk.hypeFailedMsg);
            return false;
        }
        else if(hypeSdk.hasHypeStopped){
            AlertDialogUtils.showOkDialog(MainActivity.this,
                    "Error", "Hype SDK stopped.\n" + hypeSdk.hypeStoppedMsg);
            return false;
        }
        else if( ! hypeSdk.hasHypeStarted){
            AlertDialogUtils.showOkDialog(MainActivity.this,
                    "Warning", "Hype SDK is not ready yet.");
            return false;
        }

        return true;
    }

    static String processUserServiceNameInput(String input)
    {
        return input.toLowerCase().trim();
    }

}
