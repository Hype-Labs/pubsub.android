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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class HypeSdkInterface implements NetworkObserver, StateObserver, MessageObserver
{
    private static final String TAG = HypeSdkInterface.class.getName();
    private static final String HYPE_SDK_INTERFACE_LOG_PREFIX = HpsConstants.LOG_PREFIX + "<HypeSdkInterface> ";

    //////////////////////////////////////////////////////////////////////////////
    // Members
    //////////////////////////////////////////////////////////////////////////////

    final private HypePubSub hps = HypePubSub.getInstance();
    final private Network network = Network.getInstance();

    private static HypeSdkInterface hypeSdkInterface = new HypeSdkInterface();
    static boolean isHypeReady = false;
    static boolean isHypeFail = false;

    //////////////////////////////////////////////////////////////////////////////
    // Methods
    //////////////////////////////////////////////////////////////////////////////

    public static HypeSdkInterface getInstance() {
        return hypeSdkInterface;
    }

    protected void requestHypeToStart(Context context) throws UnsupportedEncodingException {
        Hype.setUserIdentifier(0L);
        Hype.setAppIdentifier(HpsConstants.APP_IDENTIFIER);
        Hype.setContext(context);
        Hype.setAnnouncement((android.os.Build.MODEL).getBytes(HpsConstants.ENCODING_STANDARD));

        Hype.addStateObserver(this);
        Hype.addNetworkObserver(this);
        Hype.addMessageObserver(this);

        Hype.start();

        Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Requested Hype SDK start.");
    }

    protected void requestHypeToStop()
    {
        Hype.stop();
        Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Requested Hype SDK stop.");
    }

    //////////////////////////////////////////////////////////////////////////////
    // State Observer Methods
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onHypeStart()
    {
        try {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK started! Host Instance: "
                    + HpsGenericUtils.buildInstanceLogIdStr(Hype.getHostInstance()));
            isHypeReady = true;
            network.setOwnClient(Hype.getHostInstance());
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeStop(Error var1)
    {
        Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK stopped!");
        requestHypeToStop();
    }

    @Override
    public void onHypeFailedStarting(Error var1)
    {
        isHypeFail = true;

        Log.e(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK start failed. Suggestion: " + var1.getSuggestion());
        Log.e(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK start failed. Description: " + var1.getDescription());
        Log.e(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK start failed. Reason: " + var1.getReason());
    }

    @Override
    public void onHypeReady()
    {
        Log.i( TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK is ready");
    }


    @Override
    public void onHypeStateChange()
    {
        Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK state has changed to " + Hype.getState());
    }


    //////////////////////////////////////////////////////////////////////////////
    // Network Observer Methods
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onHypeInstanceFound(Instance var1)
    {
        String instanceLogIdStr = "";
        try {
            instanceLogIdStr = HpsGenericUtils.buildInstanceLogIdStr(var1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(!var1.isResolved())
        {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK unresolved instance found: " + instanceLogIdStr);
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Resolving Hype SDK instance: " + instanceLogIdStr);
            Hype.resolve(var1);
        }
        else
        {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK resolved instance found: " + instanceLogIdStr);

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
    public void onHypeInstanceLost(Instance var1, Error var2)
    {
        try
        {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK instance lost:" + HpsGenericUtils.buildInstanceLogIdStr(var1));

            // Remove the instance lost in a separate thread to release the lock of the
            // Hype instance object preventing possible deadlock
            final Instance instanceToRemove = var1;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        removeInstance(instanceToRemove);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e)
                    {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeInstanceResolved(Instance var1, byte[] var2)
    {
        try
        {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK instance resolved: " + HpsGenericUtils.buildInstanceLogIdStr(var1));

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
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeInstanceFailResolving(Instance var1, Error var2)
    {
        try
        {
            Log.e(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK instance fail resolving: " + HpsGenericUtils.buildInstanceLogIdStr(var1));

            Log.e(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK instance fail resolving. Suggestion: " + var2.getSuggestion());
            Log.e(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK instance fail resolving. Description: " + var2.getDescription());
            Log.e(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK instance fail resolving. Reason: " + var2.getReason());
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Message Observer Methods
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onHypeMessageReceived(Message var1, Instance var2)
    {

        final Message receivedMsg = var1;
        final Instance originatorInstance = var2;

        // Process the received message in a separate thread to release the lock of the
        // Hype instance object preventing possible deadlock
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Protocol.receiveMsg(originatorInstance, receivedMsg.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    @Override
    public void onHypeMessageFailedSending(MessageInfo var1, Instance var2, Error var3)
    {
        try
        {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK message failed sending to: "
                    + HpsGenericUtils.buildInstanceLogIdStr(var2));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        Log.e(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK message failed sending error. Suggestion: " + var3.getSuggestion());
        Log.e(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK message failed sending error. Description: " + var3.getDescription());
        Log.e(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK message failed sending error. Reason: " + var3.getReason());
    }

    @Override
    public void onHypeMessageSent(MessageInfo var1, Instance var2, float var3, boolean var4)
    {
        if(! var4) {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK message " + var1.getIdentifier() + " sending percentage: " + (var3 * 100) + "%");
        }
        else {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK message " + var1.getIdentifier() + " fully sent");
        }
    }

    @Override
    public void onHypeMessageDelivered(MessageInfo var1, Instance var2, float var3, boolean var4)
    {
        if(! var4) {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK message " + var1.getIdentifier() + " delivered percentage: " + (var3 * 100) + "%");
        }
        else {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK message " + var1.getIdentifier() + " fully delivered");
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Add and Remove Instances on Founds, Resolved and Losts
    //////////////////////////////////////////////////////////////////////////////

    public void addInstanceAlreadyResolved(Instance instance)
    {
        try
        {
            Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Adding Hype SDK instance already resolved: "
                    + HpsGenericUtils.buildInstanceLogIdStr(instance));

            synchronized (network) // Add thread safety to adding procedure
            {
                network.networkClients.add(instance);
                hps.updateManagedServices();
                hps.updateOwnSubscriptions();
                updateClientsUI(); // Updated UI after adding a new instance
            }
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeInstance(Instance instance) throws IOException, NoSuchAlgorithmException
    {
        Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Removing Hype SDK instance already lost: "
                + HpsGenericUtils.buildInstanceLogIdStr(instance));

        synchronized (network) // Add thread safety to removal procedure
        {
            network.networkClients.remove(instance);
            hps.updateOwnSubscriptions();
            updateClientsUI(); // Updated UI after removing an instance
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Calls to Hype Send
    //////////////////////////////////////////////////////////////////////////////

    public void sendMsg(HpsMessage hpsMsg, Instance destination) throws IOException
    {
        Message sdkMsg = Hype.send(hpsMsg.toByteArray(), destination, true);
        Log.i(TAG, HYPE_SDK_INTERFACE_LOG_PREFIX + "Hype SDK send message with ID:"
                + sdkMsg.getIdentifier());
    }

    //////////////////////////////////////////////////////////////////////////////
    // Other methods
    //////////////////////////////////////////////////////////////////////////////

    private void updateClientsUI()
    {
        ClientsListActivity clientsListActivity = ClientsListActivity.getDefaultInstance();
        if (clientsListActivity != null) {
            clientsListActivity.updateUI();
        }
    }
}
