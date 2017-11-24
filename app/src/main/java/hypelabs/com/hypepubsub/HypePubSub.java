package hypelabs.com.hypepubsub;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ListIterator;

import com.hypelabs.hype.Instance;

public class HypePubSub
{
    private static final String TAG = Constants.GLOBAL_TAG_PREFIX + HypePubSub.class.getName();

    private static HypePubSub hpb = null; // Singleton

    final SubscriptionsList ownSubscriptions;
    final ServiceManagersList managedServices;

    private Network network = Network.getInstance();

    public static HypePubSub getInstance() throws NoSuchAlgorithmException
    {
        if (hpb == null) {
            hpb = new HypePubSub();
        }
        return hpb;
    }

    private HypePubSub()
    {
        this.ownSubscriptions = new SubscriptionsList();
        this.managedServices = new ServiceManagersList();
    }

    int issueSubscribeReq(String serviceName) throws NoSuchAlgorithmException, IOException
    {
        byte serviceKey[] = GenericUtils.getStrHash(serviceName);
        Instance managerInstance = network.getServiceManagerInstance(serviceKey);

        // Add subscription to the list of own subscriptions. Only adds if it doesn't exist yet.
        ownSubscriptions.add(serviceName, managerInstance);

        // if this client is the manager of the service we don't need to send the subscribe message to
        // the protocol manager
        if(GenericUtils.areInstancesEqual(network.ownClient.instance, managerInstance))
        {
            Log.i(TAG, "Issuing Subscribe message for service "
                    + serviceName + " to Host instance");

            this.processSubscribeReq(serviceKey, network.ownClient.instance);
            return 1;
        }
        else
        {
            Protocol.sendSubscribeMsg(serviceKey, managerInstance);
        }

        return 0;
    }

    int issueUnsubscribeReq(String serviceName) throws NoSuchAlgorithmException, IOException
    {
        byte serviceKey[] = GenericUtils.getStrHash(serviceName);
        Instance managerInstance = network.getServiceManagerInstance(serviceKey);

        if(ownSubscriptions.find(serviceKey) == null)
        {
            return -2;
        }

        // Remove the subscription from the list of own subscriptions
        ownSubscriptions.remove(serviceName);

        // if this client is the manager of the service we don't need to send the unsubscribe message
        // to the protocol manager
        if(GenericUtils.areInstancesEqual(network.ownClient.instance, managerInstance))
        {
            Log.i(TAG, "Issuing Unsubscribe message for service "
                    + serviceName + " to Host instance");

            this.processUnsubscribeReq(serviceKey, network.ownClient.instance);
        }
        else {
            Protocol.sendUnsubscribeMsg(serviceKey, managerInstance);
        }

        return 0;
    }

    int issuePublishReq(String serviceName, String msg) throws NoSuchAlgorithmException, IOException
    {
        byte serviceKey[] = GenericUtils.getStrHash(serviceName);
        Instance managerInstance = network.getServiceManagerInstance(serviceKey);

        // if this client is the manager of the service we don't need to send the publish message
        // to the protocol manager
        if(GenericUtils.areInstancesEqual(network.ownClient.instance, managerInstance))
        {
            Log.i(TAG, "Issuing Publish message for service "
                    + serviceName + " to Host instance. Msg: " + msg);

            this.processPublishReq(serviceKey, msg);
            return 1;
        }
        else
        {
            Protocol.sendPublishMsg(serviceKey, managerInstance, msg);
        }

        return 0;
    }

    synchronized void processSubscribeReq(byte serviceKey[], Instance requesterInstance) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        ServiceManager serviceManager = this.managedServices.find(serviceKey);
        if(serviceManager == null) // If the service does not exist we create it.
        {
            Log.i(TAG, "Processing Subscribe request for non-existent managed service 0x"
                    + BinaryUtils.byteArrayToHexString(serviceKey)
                    + " by " + GenericUtils.getInstanceAnnouncementStr(requesterInstance)
                    + " (0x" + BinaryUtils.byteArrayToHexString(requesterInstance.getIdentifier()) + ")"
                    + ". Managed service will be created.");

            this.managedServices.add(serviceKey);
            serviceManager = this.managedServices.getLast();
            updateManagedServicesUI(); // Updated UI after adding a new managed service
        }
        serviceManager.subscribers.add(requesterInstance);
    }

    synchronized void processUnsubscribeReq(byte serviceKey[], Instance requesterInstance) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        ServiceManager serviceManager = this.managedServices.find(serviceKey);

        if(serviceManager == null) // If the service does not exist nothing is done
        {
            Log.e(TAG, "Processing Unsubscribe request for non-existent managed service 0x"
                    + BinaryUtils.byteArrayToHexString(serviceKey)
                    + " by " + GenericUtils.getInstanceAnnouncementStr(requesterInstance)
                    + " (0x" + BinaryUtils.byteArrayToHexString(requesterInstance.getIdentifier()) + ")"
                    + ". Nothing will be done.");
            return;
        }
        serviceManager.subscribers.remove(requesterInstance);

        if(serviceManager.subscribers.size() == 0)
        { // Remove the service if there is no subscribers
            this.managedServices.remove(serviceKey);
            updateManagedServicesUI(); // Updated UI after removing a managed service
        }
    }

    synchronized void processPublishReq(byte serviceKey[], String msg) throws NoSuchAlgorithmException, IOException
    {
        ServiceManager serviceManager = this.managedServices.find(serviceKey);
        if(serviceManager == null)
            return;

        ListIterator<Client> it = serviceManager.subscribers.listIterator();
        while(it.hasNext())
        {
            Client client = it.next();
            if(client == null)
                continue;

            if(GenericUtils.areInstancesEqual(network.ownClient.instance, client.instance))
            {
                Log.i(TAG, "Processing Info message from Host instance. Msg: " + msg);
                this.processInfoMsg(serviceKey, msg);
            }
            else{
                Protocol.sendInfoMsg(serviceKey, client.instance, msg);
            }
        }
    }

    int processInfoMsg(byte serviceKey[], String msg)
    {
        Subscription subscription = ownSubscriptions.find(serviceKey);

        if(subscription == null){
            Log.e(TAG, "Message received from unsubscribed service: " + msg);
            return -1;
        }

        subscription.receivedMsg.add(msg);

        Log.i(TAG, "Received message from service " + subscription.serviceName
                                        + ": " + msg);
        return 0;
    }

    synchronized void updateManagedServices() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        Log.i(TAG, "Executing updateManagedServices");

        ListIterator<ServiceManager> it = this.managedServices.listIterator();
        while(it.hasNext())
        {
            ServiceManager servMan = it.next();
            // Check if a new Hype client with a closer key to this service key has appeared. If this happens
            // we remove the service from the list of managed services of this Hype client.
            Instance newManagerInstance = network.getServiceManagerInstance(servMan.serviceKey);
            if( ! GenericUtils.areInstancesEqual(newManagerInstance, network.ownClient.instance))
            {
                Log.i(TAG, "Passing the service management for the service 0x "
                                + BinaryUtils.byteArrayToHexString(servMan.serviceKey)
                                + " to " + GenericUtils.getInstanceAnnouncementStr(newManagerInstance)
                                + " (0x" + BinaryUtils.byteArrayToHexString(newManagerInstance.getIdentifier()) + ")");
                this.managedServices.remove(servMan.serviceKey);
                updateManagedServicesUI(); // Updated UI after removing a managed service
            }
        };
    }

    synchronized void updateOwnSubscriptions() throws IOException, NoSuchAlgorithmException
    {
        Log.i(TAG, "Executing updateOwnSubscriptions");

        ListIterator<Subscription> it = this.ownSubscriptions.listIterator();
        while(it.hasNext())
        {
            Subscription subscription = it.next();

            Instance newManagerInstance = network.getServiceManagerInstance(subscription.serviceKey);

            // If there is a node with a closer key to the service key we change the manager
            if( ! GenericUtils.areInstancesEqual(newManagerInstance, subscription.manager))
            {
                Log.i(TAG, "Update the subscription manager for the service 0x "
                        + BinaryUtils.byteArrayToHexString(subscription.serviceKey)
                        + " to " + GenericUtils.getInstanceAnnouncementStr(newManagerInstance)
                        + " (0x" + BinaryUtils.byteArrayToHexString(newManagerInstance.getIdentifier()) + ")");

                subscription.manager = newManagerInstance;
                this.issueSubscribeReq(subscription.serviceName); // re-send the subscribe request to the new manager
            }
        }
    }

    private void updateManagedServicesUI()
    {
        ServiceManagersListActivity serviceManagersListActivity = ServiceManagersListActivity.getDefaultInstance();
        if (serviceManagersListActivity != null) {
            serviceManagersListActivity.updateInterface();
        }
    }
}
