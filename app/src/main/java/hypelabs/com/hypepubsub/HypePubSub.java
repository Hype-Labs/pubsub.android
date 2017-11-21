package hypelabs.com.hypepubsub;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.app.Activity;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ListIterator;

import com.hypelabs.hype.Error;
import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.Message;
import com.hypelabs.hype.MessageInfo;
import com.hypelabs.hype.MessageObserver;
import com.hypelabs.hype.StateObserver;

public class HypePubSub implements StateObserver, MessageObserver
{
    static HypePubSub hpb = null; // Singleton

    SubscriptionsList ownSubscriptions;
    ServiceManagersList managedServices;
    private static Context context;
    private static MainActivity mainActivity;

    public static HypePubSub getInstance() throws NoSuchAlgorithmException
    {
        if (hpb == null)
        {
            hpb = new HypePubSub();
        }
        return hpb;
    }

    private HypePubSub() throws NoSuchAlgorithmException
    {
        this.ownSubscriptions = new SubscriptionsList();
        this.managedServices = new ServiceManagersList();
        requestHypeToStart();
    }

    //////////////////////////////////////////////////
    // Methods from StateObserver
    //////////////////////////////////////////////////

    @Override
    public void onHypeStart()
    {
        try
        {
            Hype.addNetworkObserver(Network.getInstance());
            Log.i("HypePubSub", "Hype started!");
            mainActivity.addButtonListeners();
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
    public void onHypeFailedStarting(Error var1){
        Log.e("HypePubSub", "Hype Start Failed. Error description: " + var1.getDescription());
    }

    @Override
    public void onHypeReady(){}

    @Override
    public void onHypeStateChange(){}

    //////////////////////////////////////////////////
    // Methods from MessageObserver
    //////////////////////////////////////////////////

    @Override
    public void onHypeMessageReceived(Message var1, Instance var2){}

    @Override
    public void onHypeMessageFailedSending(MessageInfo var1, Instance var2, Error var3){}

    @Override
    public void onHypeMessageSent(MessageInfo var1, Instance var2, float var3, boolean var4){}

    @Override
    public void onHypeMessageDelivered(MessageInfo var1, Instance var2, float var3, boolean var4){}

    //////////////////////////////////////////////////

    protected void requestHypeToStart()
    {
        Hype.setUserIdentifier(0l);
        Hype.setAppIdentifier("00000000");
        Hype.setContext(this.context);
        Hype.addStateObserver(this);
        Hype.start();

        Log.i("HypePubSub", "Invoked Hype start.");
    }

    protected void requestHypeToStop()
    {
        Hype.stop();
    }

    static public void setContext(Context c){
        context = c;
    }

    static public void setMainActivity(MainActivity mainAct){
        mainActivity = mainAct;
    }

    int issueSubscribeReq(String serviceName) throws NoSuchAlgorithmException, IOException
    {
        Network network = Network.getInstance();
        Protocol protocol = Protocol.getInstance();
        MessageDigest md = MessageDigest.getInstance(Constants.HPB_HASH_ALGORITHM);

        byte serviceKey[] = md.digest(serviceName.getBytes());
        byte managerId[] = network.getServiceManagerId(serviceKey);

        // Add subscription to the list of own subscriptions. Only adds if it doesn't exist yet.
        ownSubscriptions.add(serviceName, managerId);

        // if this client is the manager of the service we don't need to send the subscribe message to
        // the protocol manager
        if(Arrays.equals(network.ownClient.id, managerId))
            this.processSubscribeReq(serviceKey, network.ownClient.id);
        else
            protocol.sendSubscribeMsg(serviceKey, managerId);

        return 0;
    }

    int issueUnsubscribeReq(String serviceName) throws NoSuchAlgorithmException, IOException {
        Network network = Network.getInstance();
        Protocol protocol = Protocol.getInstance();
        MessageDigest md = MessageDigest.getInstance(Constants.HPB_HASH_ALGORITHM);

        byte serviceKey[] = md.digest(serviceName.getBytes());
        byte managerId[] = network.getServiceManagerId(serviceKey);

        if(ownSubscriptions.find(serviceKey) == null)
        {
            return -2;
        }

        // Remove the subscription from the list of own subscriptions
        ownSubscriptions.remove(serviceName);

        // if this client is the manager of the service we don't need to send the unsubscribe message
        // to the protocol manager
        if(Arrays.equals(network.ownClient.id, managerId))
            this.processUnsubscribeReq(serviceKey, network.ownClient.id);
        else
            protocol.sendUnsubscribeMsg(serviceKey, managerId);

        return 0;
    }

    int issuePublishReq(String serviceName, String msg) throws NoSuchAlgorithmException, IOException {
        Network network = Network.getInstance();
        Protocol protocol = Protocol.getInstance();
        MessageDigest md = MessageDigest.getInstance(Constants.HPB_HASH_ALGORITHM);

        byte serviceKey[] = md.digest(serviceName.getBytes());
        byte managerId[] = network.getServiceManagerId(serviceKey);

        // if this client is the manager of the service we don't need to send the publish message
        // to the protocol manager
        if(Arrays.equals(network.ownClient.id, managerId))
            this.processPublishReq(serviceKey, msg);
        else
            protocol.sendPublishMsg(serviceKey, managerId, msg);

        return 0;
    }

    int processSubscribeReq(byte serviceKey[], byte requesterClientId[]) throws NoSuchAlgorithmException
    {
        ServiceManager serviceManager = this.managedServices.find(serviceKey);
        if(serviceManager == null) // If the service does not exist we create it.
        {
            this.managedServices.add(serviceKey);
            serviceManager = this.managedServices.getLast();
        }
        serviceManager.subscribers.add(requesterClientId);
        return 0;
    }

    int processUnsubscribeReq(byte serviceKey[], byte requesterClientId[]) throws NoSuchAlgorithmException {
        ServiceManager serviceManager = this.managedServices.find(serviceKey);
        if(serviceManager == null) { // If the service does not exist nothing is done
            return -1;
        }
        serviceManager.subscribers.remove(requesterClientId);

        if(serviceManager.subscribers.size() == 0) // Remove the service if there is no subscribers
            this.managedServices.remove(serviceKey);

        return 0;
    }

    int processPublishReq(byte serviceKey[], String msg) throws NoSuchAlgorithmException, IOException {
        ServiceManager serviceManager = this.managedServices.find(serviceKey);
        if(serviceManager == null)
            return -2;

        Network network = Network.getInstance();
        Protocol protocol = Protocol.getInstance();
        ListIterator<Client> it = serviceManager.subscribers.listIterator();
        while(it.hasNext())
        {
            Client client = it.next();
            if(client == null)
                continue;

            if(Arrays.equals(network.ownClient.id, client.id))
                this.processInfoMsg(serviceKey, msg);
            else
                protocol.sendInfoMsg(serviceKey, client.id, msg);
        }

        return 0;
    }

    int processInfoMsg(byte serviceKey[], String msg)
    {
        Subscription subscription = ownSubscriptions.find(serviceKey);

        if(subscription == null){
            Log.d("HypePubSub", "Message received from unknown service: " + msg);
            return -1;
        }

        Log.d("HypePubSub", "Message received from service " + subscription.serviceName);
        Log.d("HypePubSub", "Message: " + msg);
        return 0;
    }

    int updateManagedServices() throws NoSuchAlgorithmException
    {
        Network network = Network.getInstance();

        ListIterator<ServiceManager> it = this.managedServices.listIterator();
        while(it.hasNext())
        {
            ServiceManager servMan = it.next();
            // Check if a new Hype client with a closer key to this service key has appeared. If this happens
            // we remove the service from the list of managed services of this Hype client.
            byte newManagerId[] = network.getServiceManagerId(servMan.serviceKey);
            if(Arrays.equals(newManagerId, network.ownClient.id) == false)
                this.managedServices.remove(servMan.serviceKey);
        };
        return 0;
    }

    int updateOwnSubscriptions() throws IOException, NoSuchAlgorithmException
    {
        Network network = Network.getInstance();
        Protocol protocol = Protocol.getInstance();

        ListIterator<Subscription> it = this.ownSubscriptions.listIterator();
        while(it.hasNext())
        {
            Subscription subscription = it.next();

            byte newManagerId[] = network.getServiceManagerId(subscription.serviceKey);

            // If there is a node with a closer key to the service key we change the manager
            if (Arrays.equals(newManagerId, subscription.managerId) == false)
            {
                subscription.managerId = newManagerId;
                this.issueSubscribeReq(subscription.serviceName); // re-send the subscribe request to the new manager
            }
        }
        return 0;
    }
}
