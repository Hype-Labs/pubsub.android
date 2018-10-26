package hypelabs.com.hypepubsub;

import android.content.Context;
import android.util.Log;

import com.hypelabs.hype.Error;
import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.Message;
import com.hypelabs.hype.MessageInfo;
import com.hypelabs.hype.MessageObserver;
import com.hypelabs.hype.NetworkObserver;
import com.hypelabs.hype.StateObserver;

public class HypeSdkInterface implements NetworkObserver, StateObserver, MessageObserver
{
    // Members
    boolean hasHypeStarted = false;
    boolean hasHypeFailed = false;
    boolean hasHypeStopped = false;
    String hypeFailedMsg = "";
    String hypeStoppedMsg = "";

    // Private
    private static final String TAG = HypeSdkInterface.class.getName();
    private static final String HYPE_SDK_INTERFACE_LOG_PREFIX = HpsConstants.LOG_PREFIX + "<HypeSdkInterface> ";
    final private HypePubSub hps = HypePubSub.getInstance();
    final private Network network = Network.getInstance();

    private static HypeSdkInterface hypeSdkInterface = new HypeSdkInterface();
    public static HypeSdkInterface getInstance() { return hypeSdkInterface; }

    protected void requestHypeToStart(Context context) {
        Hype.setAppIdentifier(HpsConstants.APP_IDENTIFIER);
        Hype.setContext(context);
        Hype.setAnnouncement(HpsGenericUtils.getAndroidBuildModel());

        Hype.addStateObserver(this);
        Hype.addNetworkObserver(this);
        Hype.addMessageObserver(this);

        Hype.start();
        Log.i(TAG, String.format("%s Requested Hype SDK start.", HYPE_SDK_INTERFACE_LOG_PREFIX));
    }

    protected void requestHypeToStop() {
        Hype.stop();
        Log.i(TAG, String.format("%s Requested Hype SDK stop.", HYPE_SDK_INTERFACE_LOG_PREFIX));
    }

    //////////////////////////////////////////////////////////////////////////////
    // State Observer Methods
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onHypeStart() {
        Log.i(TAG, String.format("%s Hype SDK started! Host Instance: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX,
                HpsGenericUtils.getLogStrFromInstance(Hype.getHostInstance())));
        hasHypeStarted = true;
        network.setOwnClient(Hype.getHostInstance());
    }

    @Override
    public void onHypeStop(Error var1) {
        hasHypeStopped = true;
        hypeStoppedMsg = "";
        if(var1 != null) {
            hypeStoppedMsg = String.format("Suggestion: %s\nDescription: %s\nReason: %s",
                    var1.getSuggestion(), var1.getDescription(), var1.getReason());
        }

        Log.i(TAG,  String.format("%s Hype SDK stopped!", HYPE_SDK_INTERFACE_LOG_PREFIX));
        requestHypeToStop();
    }

    @Override
    public void onHypeFailedStarting(Error var1) {
        hasHypeFailed = true;
        hypeFailedMsg = String.format("Suggestion: %s\nDescription: %s\nReason: %s",
                var1.getSuggestion(), var1.getDescription(), var1.getReason());

        Log.e(TAG, String.format("%s Hype SDK start failed. Suggestion: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, var1.getSuggestion()));
        Log.e(TAG, String.format("%s Hype SDK start failed. Description: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, var1.getDescription()));
        Log.e(TAG, String.format("%s Hype SDK start failed. Reason: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, var1.getReason()));
    }

    @Override
    public void onHypeReady() {
        Log.i( TAG, String.format("%s Hype SDK is ready", HYPE_SDK_INTERFACE_LOG_PREFIX));
    }

    @Override
    public void onHypeStateChange() {
        Log.i(TAG, String.format("%s Hype SDK state has changed to %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, Hype.getState()));
    }

    @Override
    public String onHypeRequestAccessToken(int var1) {
        return HpsConstants.ACCESS_TOKEN;
    }

    //////////////////////////////////////////////////////////////////////////////
    // Network Observer Methods
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onHypeInstanceFound(Instance var1) {
        String instanceLogIdStr = HpsGenericUtils.getLogStrFromInstance(var1);

        if(!var1.isResolved()) {
            Log.i(TAG, String.format("%s Hype SDK unresolved instance found: %s",
                    HYPE_SDK_INTERFACE_LOG_PREFIX, instanceLogIdStr));
            Log.i(TAG, String.format("%s Resolving Hype SDK instance: %s",
                    HYPE_SDK_INTERFACE_LOG_PREFIX, instanceLogIdStr));
            Hype.resolve(var1);
        }
        else {
            Log.i(TAG, String.format("%s Hype SDK resolved instance found: %s",
                    HYPE_SDK_INTERFACE_LOG_PREFIX, instanceLogIdStr));

            // Add the instance found in a separate thread to release the lock of the
            // Hype instance object preventing possible deadlock
            final Instance instanceFound = var1;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    addInstanceAlreadyResolved(instanceFound);
                }
            });
            t.start();
        }
    }

    @Override
    public void onHypeInstanceLost(Instance var1, Error var2) {
        Log.i(TAG, String.format("%s Hype SDK instance lost: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, HpsGenericUtils.getLogStrFromInstance(var1)));

        // Remove the instance lost in a separate thread to release the lock of the
        // Hype instance object preventing possible deadlock
        final Instance instanceToRemove = var1;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                removeInstance(instanceToRemove);
            }
        });
        t.start();
    }

    @Override
    public void onHypeInstanceResolved(Instance var1) {
        Log.i(TAG, String.format("%s Hype SDK instance resolved: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, HpsGenericUtils.getLogStrFromInstance(var1)));

        // Add instance in a separate thread to prevent deadlock
        final Instance instanceFound = var1;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                addInstanceAlreadyResolved(instanceFound);
            }
        });
        t.start();
    }

    @Override
    public void onHypeInstanceFailResolving(Instance var1, Error var2) {
        Log.e(TAG, String.format("%s Hype SDK instance fail resolving: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, HpsGenericUtils.getLogStrFromInstance(var1)));
        Log.e(TAG, String.format("%s Hype SDK instance fail resolving. Suggestion: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, var2.getSuggestion()));
        Log.e(TAG, String.format("%s Hype SDK instance fail resolving. Description: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, var2.getDescription()));
        Log.e(TAG, String.format("%s Hype SDK instance fail resolving. Reason: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, var2.getReason()));
    }

    //////////////////////////////////////////////////////////////////////////////
    // Message Observer Methods
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onHypeMessageReceived(Message var1, Instance var2) {
        final Message receivedMsg = var1;
        final Instance originatorInstance = var2;

        // Process the received message in a separate thread to release the lock of the
        // Hype instance object preventing possible deadlock
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Protocol.receiveMsg(originatorInstance, receivedMsg.getData());
            }
        });
        t.start();
    }

    @Override
    public void onHypeMessageFailedSending(MessageInfo var1, Instance var2, Error var3) {
        Log.i(TAG, String.format("%s Hype SDK message failed sending to: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, HpsGenericUtils.getLogStrFromInstance(var2)));
        Log.e(TAG, String.format("%s Hype SDK message failed sending error. Suggestion: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, var3.getSuggestion()));
        Log.e(TAG, String.format("%s Hype SDK message failed sending error. Description: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, var3.getDescription()));
        Log.e(TAG, String.format("%s Hype SDK message failed sending error. Reason: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, var3.getReason()));
    }

    @Override
    public void onHypeMessageSent(MessageInfo var1, Instance var2, float var3, boolean var4) {
        if(! var4) {
            Log.i(TAG, String.format("%s Hype SDK message %d sending percentage: %f",
                    HYPE_SDK_INTERFACE_LOG_PREFIX, var1.getIdentifier(), (var3 * 100)));
        }
        else {
            Log.i(TAG, String.format("%s Hype SDK message %d fully sent",
                    HYPE_SDK_INTERFACE_LOG_PREFIX, var1.getIdentifier()));
        }
    }

    @Override
    public void onHypeMessageDelivered(MessageInfo var1, Instance var2, float var3, boolean var4) {
        if(! var4) {
            Log.i(TAG, String.format("%s Hype SDK message %d delivered percentage: %f",
                    HYPE_SDK_INTERFACE_LOG_PREFIX, var1.getIdentifier(), (var3 * 100)));
        }
        else {
            Log.i(TAG, String.format("%s Hype SDK message %d fully delivered",
                    HYPE_SDK_INTERFACE_LOG_PREFIX, var1.getIdentifier()));
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Add and Remove Instances on Founds, Resolved and Lost
    //////////////////////////////////////////////////////////////////////////////

    public void addInstanceAlreadyResolved(Instance instance) {
        Log.i(TAG, String.format("%s Adding Hype SDK instance already resolved: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, HpsGenericUtils.getLogStrFromInstance(instance)));

        synchronized (network) { // Add thread safety to adding procedure
            network.networkClients.addClient(new Client(instance));
            hps.updateManagedServices();
            hps.updateOwnSubscriptions();
            updateClientsUI();
        }
    }

    public void removeInstance(Instance instance) {
        Log.i(TAG, String.format("%s Removing Hype SDK instance already lost: %s",
                HYPE_SDK_INTERFACE_LOG_PREFIX, HpsGenericUtils.getLogStrFromInstance(instance)));

        synchronized (network) { // Add thread safety to removal procedure
            network.networkClients.removeClientWithInstance(instance);
            hps.updateOwnSubscriptions();
            hps.removeSubscriptionsFromLostInstance(instance);
            updateClientsUI();
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Calls to Hype Send
    //////////////////////////////////////////////////////////////////////////////

    public void sendMsg(HpsMessage hpsMsg, Instance destination) {
        Message sdkMsg = Hype.send(hpsMsg.toByteArray(), destination, true);
        Log.i(TAG, String.format("%s Hype SDK send message with ID: %d",
                HYPE_SDK_INTERFACE_LOG_PREFIX, sdkMsg.getIdentifier()));
    }

    //////////////////////////////////////////////////////////////////////////////
    // UI methods
    //////////////////////////////////////////////////////////////////////////////

    private void updateClientsUI() {
        ClientsListActivity clientsListActivity = ClientsListActivity.getDefaultInstance();
        if (clientsListActivity != null) {
            clientsListActivity.updateUI();
        }
    }
}
