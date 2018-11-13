package hypelabs.com.hypepubsub;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButtonsFromResourceIDs();
        setButtonListeners();

        if(uiData.isToInitializeSdk) {
            initHypeSdk();
            uiData.isToInitializeSdk = false;
        }
    }

    private void initHypeSdk() {
        requestHypeRequiredPermissions(this);
    }

    public void requestHypeRequiredPermissions(Activity activity) {

        // Request AccessCoarseLocation permissions if the Android version of the device
        // requires it. Otherwise it starts the Hype SDK immediately. If the permissions are
        // requested the framework only starts if the permissions are granted (see
        // MainActivity.onRequestPermissionsResult()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    HpsConstants.REQUEST_ACCESS_COARSE_LOCATION_ID);
        }
        else {
            HypeSdkInterface hypeSdkInterface = HypeSdkInterface.getInstance();
            hypeSdkInterface.requestHypeToStart(getApplicationContext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case HpsConstants.REQUEST_ACCESS_COARSE_LOCATION_ID:
                // If the permission is not granted the Hype SDK starts but BLE transport
                // will not be active. Regardless of this, this callback must be implemented
                // because the Hype SDK should only start after the permission request being
                // concluded.
                HypeSdkInterface hypeSdkInterface = HypeSdkInterface.getInstance();
                hypeSdkInterface.requestHypeToStart(getApplicationContext());
                break;
        }
    }

    private void setButtonListeners() {
        setListenerSubscribeButton();
        setListenerUnsubscribeButton();
        setListenerPublishButton();
        setListenerCheckOwnIdButton();
        setListenerCheckHypeDevicesButton();
        setListenerCheckOwnSubscriptionsButton();
        setListenerCheckManagedServicesButton();
    }

    private void initButtonsFromResourceIDs() {
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

    private void setListenerSubscribeButton() {
        subscribeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( !isHypeSdkReady()) {
                    showHypeNotReadyDialog();
                    return;
                }

                displayServicesNamesList("Subscribe",
                        "Select a service to subscribe",
                        uiData.getUnsubscribedServicesAdapter(MainActivity.this),
                        new subscribeServiceAction(),
                        true);
            }
        });
    }

    private void setListenerUnsubscribeButton() {
        unsubscribeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( !isHypeSdkReady()) {
                    showHypeNotReadyDialog();
                    return;
                }
                if( isNoServiceSubscribed()) {
                    showNoServicesSubscribedDialog();
                    return;
                }

                displayServicesNamesList("Unsubscribe",
                        "Select a service to unsubscribe",
                        uiData.getSubscribedServicesAdapter(MainActivity.this),
                        new unsubscribeServiceAction(),
                        false);
            }
        });
    }

    private void setListenerPublishButton() {
        publishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( !isHypeSdkReady()) {
                    showHypeNotReadyDialog();
                    return;
                }

                displayServicesNamesList("Publish",
                        "Select a service in which to publish",
                        uiData.getAvailableServicesAdapter(MainActivity.this),
                        new publishServiceAction(),
                        true);
            }
        });
    }

    private void setListenerCheckOwnIdButton() {
        checkOwnIdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( !isHypeSdkReady()) {
                    showHypeNotReadyDialog();
                    return;
                }

                AlertDialogUtils.showInfoDialog(MainActivity.this,"Own Device",
                        HpsGenericUtils.getInstanceAnnouncementStr(network.ownClient.instance) + "\n"
                                + HpsGenericUtils.getIdStringFromClient(network.ownClient) + "\n"
                                + HpsGenericUtils.getKeyStringFromClient(network.ownClient));
            }
        });
    }

    private void setListenerCheckHypeDevicesButton() {
        final Intent intent = new Intent(this, ClientsListActivity.class);

        checkHypeDevicesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( !isHypeSdkReady()) {
                    showHypeNotReadyDialog();
                    return;
                }

                startActivity(intent);
            }
        });
    }

    private void setListenerCheckOwnSubscriptionsButton() {
        final Intent intent = new Intent(this, SubscriptionsListActivity.class);

        checkOwnSubscriptionsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( !isHypeSdkReady()) {
                    showHypeNotReadyDialog();
                    return;
                }
                if( isNoServiceSubscribed()) {
                    showNoServicesSubscribedDialog();
                    return;
                }

                startActivity(intent);
            }
        });
    }

    private void setListenerCheckManagedServicesButton() {
        final Intent intent = new Intent(this, ServiceManagersListActivity.class);

        checkManagedServicesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0)
            {
                if( !isHypeSdkReady()) {
                    showHypeNotReadyDialog();
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

    private void displayServicesNamesList(String title,
                                          String message,
                                          ListAdapter serviceNamesAdapter,
                                          final IServiceAction serviceAction,
                                          Boolean isNewServiceSelectionAllowed) {
        final ListView listView = new ListView(MainActivity.this);
        listView.setAdapter(serviceNamesAdapter);

        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listView);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setView(layout);
        builder.setMessage(message);
        builder.setNegativeButton("Cancel",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });

        if(isNewServiceSelectionAllowed) {
            builder.setNeutralButton("New Service",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processNewServiceSelection(serviceAction);
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
                serviceAction.action(listItem);
                dialog.dismiss();
            }
        });
    }

    private interface IServiceAction {
        void action(String userInput);
    }

    private class subscribeServiceAction implements IServiceAction {
        @Override
        public void action(String userInput) {
            String serviceName = processUserServiceNameInput(userInput);
            if (serviceName.length() == 0) {
                return;
            }

            if (hps.ownSubscriptions.containsSubscriptionWithServiceName(serviceName)) {
                AlertDialogUtils.showInfoDialog(MainActivity.this,
                        "INFO", "Service already subscribed");
                return;
            }

            boolean wasSubscribed = hps.issueSubscribeReq(serviceName);
            if (wasSubscribed) {
                uiData.addSubscribedService(MainActivity.this, serviceName);
                uiData.removeUnsubscribedService(MainActivity.this, serviceName);
            }
        }
    }

    private class unsubscribeServiceAction implements IServiceAction {
        @Override
        public void action(String userInput) {
            String serviceName = processUserServiceNameInput(userInput);
            if (serviceName.length() == 0) {
                return;
            }

            boolean wasUnsubscribed = hps.issueUnsubscribeReq(serviceName);
            if (wasUnsubscribed) {
                uiData.addUnsubscribedService(MainActivity.this, serviceName);
                uiData.removeSubscribedService(MainActivity.this, serviceName);
            }
        }
    }

    private class publishServiceAction implements IServiceAction {
        @Override
        public void action(String userInput) {
            final String serviceName = processUserServiceNameInput(userInput);
            if (serviceName.length() == 0) {
                return;
            }

            AlertDialogUtils.ISingleInputDialog publishMsgInput = new AlertDialogUtils.ISingleInputDialog() {

                @Override
                public void onOk(String msg) {
                    msg = msg.trim();
                    if (msg.length() == 0) {
                        AlertDialogUtils.showInfoDialog(MainActivity.this,
                                "WARNING",
                                "A message must be specified");
                        return;
                    }

                    hps.issuePublishReq(serviceName, msg);
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
    }

    private void processNewServiceSelection(final IServiceAction serviceAction) {
        AlertDialogUtils.ISingleInputDialog newServiceInput = new AlertDialogUtils.ISingleInputDialog() {

            @Override
            public void onOk(String input) {
                String serviceName = processUserServiceNameInput(input);
                uiData.addAvailableService(MainActivity.this, serviceName);
                uiData.addUnsubscribedService(MainActivity.this, serviceName);
                serviceAction.action(serviceName);
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

    private boolean isHypeSdkReady() {
        if(!hypeSdk.hasHypeFailed  && !hypeSdk.hasHypeStopped && hypeSdk.hasHypeStarted) {
            return true;
        }
        return false;
    }

    private void showHypeNotReadyDialog() {
        if(hypeSdk.hasHypeFailed) {
            AlertDialogUtils.showInfoDialog(MainActivity.this,
                    "Error", "Hype SDK could not be started.\n" + hypeSdk.hypeFailedMsg);
        }
        else if(hypeSdk.hasHypeStopped) {
            AlertDialogUtils.showInfoDialog(MainActivity.this,
                    "Error", "Hype SDK stopped.\n" + hypeSdk.hypeStoppedMsg);
        }
        else if( !hypeSdk.hasHypeStarted) {
            AlertDialogUtils.showInfoDialog(MainActivity.this,
                    "Warning", "Hype SDK is not ready yet.");
        }
    }

    private boolean isNoServiceSubscribed()
    {
        return uiData.getNumberOfSubscribedServices() == 0;
    }

    private void showNoServicesSubscribedDialog() {
        AlertDialogUtils.showInfoDialog(MainActivity.this,
                "INFO", "No services subscribed");

    }

    static String processUserServiceNameInput(String input)
    {
        return input.toLowerCase().trim();
    }

}
