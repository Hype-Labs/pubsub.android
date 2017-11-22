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

    //////////////////////////////////////////////////////////////////////////////
    // Members
    //////////////////////////////////////////////////////////////////////////////

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
        Hype.setUserIdentifier(0l);
        Hype.setAppIdentifier(Constants.HPB_APP_IDENTIFIER);
        Hype.setContext(context);
        Hype.setAnnouncement((android.os.Build.MODEL).getBytes(Constants.HPB_ENCODING_STANDARD));

        Hype.addStateObserver(this);
        Hype.addNetworkObserver(this);
        Hype.addMessageObserver(this);

        Hype.start();

        Log.i(TAG, "Requested Hype start.");
    }

    protected void requestHypeToStop()
    {
        Hype.stop();
        Log.i(TAG, "Requested Hype stop.");
    }

    //////////////////////////////////////////////////////////////////////////////
    // State Observer Methods
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onHypeStart()
    {
        try
        {
            Log.i("HypePubSub", "Hype started!");
            isHypeReady = true;
            Network network = Network.getInstance();
            network.setOwnClient(Hype.getHostInstance());
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeStop(Error var1) {
        requestHypeToStop();
    }

    @Override
    public void onHypeFailedStarting(Error var1)
    {
        isHypeFail = true;
        Log.e("HypePubSub", "Hype Start Failed. Error description: " + var1.getDescription());
    }

    @Override
    public void onHypeReady(){}

    @Override
    public void onHypeStateChange(){}

    //////////////////////////////////////////////////////////////////////////////
    // Network Observer Methods
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public void onHypeInstanceFound(Instance var1)
    {
        try
        {
            HypePubSub hpb = HypePubSub.getInstance();
            Network network = Network.getInstance();
            network.networkClients.add(var1);
            hpb.updateManagedServices();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeInstanceLost(Instance var1, Error var2)
    {
        try
        {
            HypePubSub hpb = HypePubSub.getInstance();
            Network network = Network.getInstance();
            network.networkClients.remove(var1);
            hpb.updateOwnSubscriptions();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHypeInstanceResolved(Instance var1, byte[] var2){}

    @Override
    public void onHypeInstanceFailResolving(Instance var1, Error var2){}

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
    public void onHypeMessageFailedSending(MessageInfo var1, Instance var2, Error var3){}

    @Override
    public void onHypeMessageSent(MessageInfo var1, Instance var2, float var3, boolean var4){}

    @Override
    public void onHypeMessageDelivered(MessageInfo var1, Instance var2, float var3, boolean var4){}

}
