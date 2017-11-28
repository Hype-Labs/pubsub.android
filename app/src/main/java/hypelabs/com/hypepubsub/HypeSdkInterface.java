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
    private static final String TAG = Constants.GLOBAL_TAG_PREFIX + HypeSdkInterface.class.getName();

    //////////////////////////////////////////////////////////////////////////////
    // Members
    //////////////////////////////////////////////////////////////////////////////

    final private HypePubSub hpb = HypePubSub.getInstance();
    final private Network network = Network.getInstance();

    private static HypeSdkInterface hypeSdkInterface;
    static boolean isHypeReady = false;
    static boolean isHypeFail = false;

    //////////////////////////////////////////////////////////////////////////////
    // Methods
    //////////////////////////////////////////////////////////////////////////////

    public static HypeSdkInterface getInstance()
    {
        if(hypeSdkInterface == null){
            hypeSdkInterface = new HypeSdkInterface();
        }
        return hypeSdkInterface;
    }

    protected void requestHypeToStart(Context context) throws UnsupportedEncodingException {
        Hype.setUserIdentifier(0L);
        Hype.setAppIdentifier(Constants.HPB_APP_IDENTIFIER);
        Hype.setContext(context);
        Hype.setAnnouncement((android.os.Build.MODEL).getBytes(Constants.HPB_ENCODING_STANDARD));

        Hype.addStateObserver(this);
        Hype.addNetworkObserver(this);
        Hype.addMessageObserver(this);

        Hype.start();

        Log.i(TAG, "Requested Hype SDK start.");
    }

    protected void requestHypeToStop()
    {
        Hype.stop();
        Log.i(TAG, "Requested Hype SDK stop.");
    }

    //////////////////////////////////////////////////////////////////////////////
    // State Observer Methods
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onHypeStart()
    {
        try
        {
            Log.i(TAG, "Hype SDK started!");
            isHypeReady = true;
            network.setOwnClient(Hype.getHostInstance());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeStop(Error var1)
    {
        Log.i(TAG, "Hype SDK stopped!");
        requestHypeToStop();
    }

    @Override
    public void onHypeFailedStarting(Error var1)
    {
        isHypeFail = true;
        Log.e(TAG, "Hype SDK start failed. Error description: " + var1.getDescription());
    }

    @Override
    public void onHypeReady()
    {
        Log.i( TAG, "Hype SDK is ready");
    }


    @Override
    public void onHypeStateChange()
    {
        Log.i(TAG, "Hype SDK state has changed to " + Hype.getState()
                        + " [Idle(0), Starting(1), Running(2), Stopping(3)]");
    }


    //////////////////////////////////////////////////////////////////////////////
    // Network Observer Methods
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onHypeInstanceFound(Instance var1)
    {
        try
        {
            Log.i(TAG, "Hype SDK Instance Found " + GenericUtils.getInstanceAnnouncementStr(var1)
                    + " (0x" + BinaryUtils.byteArrayToHexString(var1.getIdentifier()) + ")");

            synchronized (network) // Add thread safety to adding procedure
            {
                network.networkClients.add(var1);
                hpb.updateManagedServices();
                updateClientsUI(); // Updated UI after adding a new instance
            }
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeInstanceLost(Instance var1, Error var2)
    {
        try
        {
            Log.i(TAG, "Hype SDK Instance Lost " + GenericUtils.getInstanceAnnouncementStr(var1)
                    + " (0x" + BinaryUtils.byteArrayToHexString(var1.getIdentifier()) + ")");

            synchronized (network) // Add thread safety to removal procedure
            {
                network.networkClients.remove(var1);
                hpb.updateOwnSubscriptions();
                updateClientsUI(); // Updated UI after removing an instance
            }
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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
            Log.i(TAG, "Hype SDK Instance Resolved " + GenericUtils.getInstanceAnnouncementStr(var1)
                    + " (0x" + BinaryUtils.byteArrayToHexString(var1.getIdentifier()) + ")");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeInstanceFailResolving(Instance var1, Error var2)
    {
        try
        {
            Log.i(TAG, "Hype SDK Instance Fail Resolving " + GenericUtils.getInstanceAnnouncementStr(var1)
                    + " (0x" + BinaryUtils.byteArrayToHexString(var1.getIdentifier()) + ")");
        } catch (UnsupportedEncodingException e)
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
        try {
            Protocol.receiveMsg(var2, var1.getData());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeMessageFailedSending(MessageInfo var1, Instance var2, Error var3)
    {
        try
        {
            Log.i(TAG, "Hype SDK message failed sending " + GenericUtils.getInstanceAnnouncementStr(var2)
                    + " (0x" + BinaryUtils.byteArrayToHexString(var2.getIdentifier()) + ")");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        Log.i(TAG, "Hype SDK message failed sending error. Suggestion" + var3.getSuggestion());
        Log.i(TAG, "Hype SDK message failed sending error. Description" + var3.getDescription());
        Log.i(TAG, "Hype SDK message failed sending error. Reason" + var3.getReason());
    }

    @Override
    public void onHypeMessageSent(MessageInfo var1, Instance var2, float var3, boolean var4)
    {
        Log.i( TAG, "Hype SDK message sent");

    }

    @Override
    public void onHypeMessageDelivered(MessageInfo var1, Instance var2, float var3, boolean var4)
    {
        Log.i( TAG, "Hype SDK message delivered");
    }

    private void updateClientsUI()
    {
        ClientsListActivity clientsListActivity = ClientsListActivity.getDefaultInstance();
        if (clientsListActivity != null) {
            clientsListActivity.updateInterface();
        }
    }
}
