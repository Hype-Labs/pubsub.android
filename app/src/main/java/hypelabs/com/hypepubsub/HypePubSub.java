package hypelabs.com.hypepubsub;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ListIterator;
import java.util.Locale;

import com.hypelabs.hype.Instance;

public class HypePubSub
{
    private static final String TAG =  HypePubSub.class.getName();

    final private static HypePubSub hpb = new HypePubSub(); // Early loading to avoid thread-safety issues

    final SubscriptionsList ownSubscriptions;
    final ServiceManagersList managedServices;

    final private Network network = Network.getInstance();
    private int notificationID = 1;

    public static HypePubSub getInstance()
    {
        return hpb;
    }

    private HypePubSub()
    {
        this.ownSubscriptions = new SubscriptionsList();
        this.managedServices = new ServiceManagersList();
    }

    int issueSubscribeReq(String serviceName) throws NoSuchAlgorithmException, IOException
    {
        byte serviceKey[] = HpbGenericUtils.getStrHash(serviceName);
        Instance managerInstance = network.determineInstanceResponsibleForService(serviceKey);

        // Add subscription to the list of own subscriptions. Only adds if it doesn't exist yet.
        ownSubscriptions.add(serviceName, managerInstance);

        // if this client is the manager of the service we don't need to send the subscribe message to
        // the protocol manager
        if(HpbGenericUtils.areInstancesEqual(network.ownClient.instance, managerInstance))
        {
            Log.i(TAG, Constants.HPB_LOG_PREFIX + "Issuing Subscribe message for service "
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
        byte serviceKey[] = HpbGenericUtils.getStrHash(serviceName);
        Instance managerInstance = network.determineInstanceResponsibleForService(serviceKey);

        if(ownSubscriptions.find(serviceKey) == null)
        {
            return -2;
        }

        // Remove the subscription from the list of own subscriptions
        ownSubscriptions.remove(serviceName);

        // if this client is the manager of the service we don't need to send the unsubscribe message
        // to the protocol manager
        if(HpbGenericUtils.areInstancesEqual(network.ownClient.instance, managerInstance))
        {
            Log.i(TAG, Constants.HPB_LOG_PREFIX + "Issuing Unsubscribe message for service "
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
        byte serviceKey[] = HpbGenericUtils.getStrHash(serviceName);
        Instance managerInstance = network.determineInstanceResponsibleForService(serviceKey);

        // if this client is the manager of the service we don't need to send the publish message
        // to the protocol manager
        if(HpbGenericUtils.areInstancesEqual(network.ownClient.instance, managerInstance))
        {
            Log.i(TAG, Constants.HPB_LOG_PREFIX + "Issuing Publish message for service "
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

    synchronized void processSubscribeReq(byte serviceKey[], Instance requesterInstance) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        Instance managerInstance = network.determineInstanceResponsibleForService(serviceKey);
        if( ! HpbGenericUtils.areInstancesEqual(managerInstance, network.ownClient.instance))
        {
            Log.i(TAG, Constants.HPB_LOG_PREFIX
                    + "Received Subscribe request for service 0x"
                    + BinaryUtils.byteArrayToHexString(serviceKey)
                    + " by " + HpbGenericUtils.getInstanceLogIdStr(requesterInstance)
                    + ". However another instance should be responsible for this service: "
                    + HpbGenericUtils.getInstanceLogIdStr(managerInstance));
            return;
        }

        ServiceManager serviceManager = this.managedServices.find(serviceKey);
        if(serviceManager == null ) // If the service does not exist we create it.
        {
            Log.i(TAG, Constants.HPB_LOG_PREFIX
                    + "Processing Subscribe request for non-existent managed service 0x"
                    + BinaryUtils.byteArrayToHexString(serviceKey)
                    + HpbGenericUtils.getInstanceLogIdStr(requesterInstance)
                    + ". Managed service will be created.");

            this.managedServices.add(serviceKey);
            serviceManager = this.managedServices.getLast();
            updateManagedServicesUI(); // Updated UI after adding a new managed service
        }

        Log.i(TAG, Constants.HPB_LOG_PREFIX
                + "Adding instance " + HpbGenericUtils.getInstanceLogIdStr(requesterInstance)
                + " to list of subscriber of service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        serviceManager.subscribers.add(requesterInstance);
    }

    synchronized void processUnsubscribeReq(byte serviceKey[], Instance requesterInstance) throws UnsupportedEncodingException
    {
        ServiceManager serviceManager = this.managedServices.find(serviceKey);

        if(serviceManager == null) // If the service does not exist nothing is done
        {
            Log.e(TAG, Constants.HPB_LOG_PREFIX
                    + "Processing Unsubscribe request for non-existent managed service 0x"
                    + BinaryUtils.byteArrayToHexString(serviceKey)
                    + " by " + HpbGenericUtils.getInstanceLogIdStr(requesterInstance)
                    + ". Nothing will be done.");
            return;
        }

        Log.i(TAG, Constants.HPB_LOG_PREFIX
                + "Removing instance " + HpbGenericUtils.getInstanceLogIdStr(requesterInstance)
                + " from list of subscriber of service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        serviceManager.subscribers.remove(requesterInstance);

        if(serviceManager.subscribers.size() == 0)
        { // Remove the service if there is no subscribers
            this.managedServices.remove(serviceKey);
            updateManagedServicesUI(); // Updated UI after removing a managed service
        }
    }

    synchronized void processPublishReq(byte serviceKey[], String msg) throws IOException
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

            if(HpbGenericUtils.areInstancesEqual(network.ownClient.instance, client.instance))
            {
                Log.i(TAG, Constants.HPB_LOG_PREFIX
                        + "Processing Info message from Host instance. Msg: " + msg);
                this.processInfoMsg(serviceKey, msg);
            }
            else{

                Log.i(TAG, Constants.HPB_LOG_PREFIX
                        + "Sending info message of service 0x"
                        + BinaryUtils.byteArrayToHexString(serviceKey)
                        + " to " + HpbGenericUtils.getInstanceLogIdStr(client.instance));

                Protocol.sendInfoMsg(serviceKey, client.instance, msg);
            }
        }
    }

    int processInfoMsg(byte serviceKey[], String msg)
    {
        Subscription subscription = ownSubscriptions.find(serviceKey);

        if(subscription == null){
            Log.e(TAG, Constants.HPB_LOG_PREFIX
                    + "HpbMessage received from unsubscribed service: " + msg);
            return -1;
        }

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("k'h'mm", Locale.getDefault());
        String timestamp = sdf.format(now);
        String msgWithTimeStamp = timestamp + ": " + msg;

        subscription.receivedMsg.add(0, msgWithTimeStamp);
        updateMessagesUI();
        String notificationText = subscription.serviceName + ": " + msg;
        displayNotification(MainActivity.getContext(), Constants.HPB_NOTIFICATIONS_CHANNEL, Constants.HPB_NOTIFICATIONS_TITLE, notificationText, notificationID);
        notificationID++;

        Log.i(TAG, Constants.HPB_LOG_PREFIX
                + "Received message from service " + subscription.serviceName
                + ": " + msg);

        return 0;
    }

    synchronized void updateManagedServices() throws UnsupportedEncodingException
    {
        Log.i(TAG, Constants.HPB_LOG_PREFIX + "Executing updateManagedServices");

        ListIterator<ServiceManager> it = this.managedServices.listIterator();

        while(it.hasNext())
        {
            ServiceManager managedService = it.next();
            // Check if a new Hype client with a closer key to this service key has appeared. If this happens
            // we remove the service from the list of managed services of this Hype client.
            Instance newManagerInstance = network.determineInstanceResponsibleForService(managedService.serviceKey);
            if( ! HpbGenericUtils.areInstancesEqual(newManagerInstance, network.ownClient.instance))
            {
                Log.i(TAG, Constants.HPB_LOG_PREFIX + "Passing the service management for the service 0x "
                                + BinaryUtils.byteArrayToHexString(managedService.serviceKey)
                                + " to " + HpbGenericUtils.getInstanceLogIdStr(newManagerInstance));

                it.remove();
            }
        }
    }

    synchronized void updateOwnSubscriptions() throws IOException, NoSuchAlgorithmException
    {
        Log.i(TAG, Constants.HPB_LOG_PREFIX + "Executing updateOwnSubscriptions");

        ListIterator<Subscription> it = this.ownSubscriptions.listIterator();
        while(it.hasNext())
        {
            Subscription subscription = it.next();

            Instance newManagerInstance = network.determineInstanceResponsibleForService(subscription.serviceKey);

            // If there is a node with a closer key to the service key we change the manager
            if( ! HpbGenericUtils.areInstancesEqual(newManagerInstance, subscription.manager))
            {
                Log.i(TAG, Constants.HPB_LOG_PREFIX + "Update the subscription manager for the service 0x "
                        + BinaryUtils.byteArrayToHexString(subscription.serviceKey)
                        + " to " + HpbGenericUtils.getInstanceLogIdStr(newManagerInstance));

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

    private void updateMessagesUI()
    {
        MessagesActivity messagesActivity = MessagesActivity.getDefaultInstance();
        if (messagesActivity != null) {
            messagesActivity.updateInterface();
        }
    }

    private void displayNotification(Context context, String notificationChannel, String title, String content, int id)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, notificationChannel);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // to show content in lock screen

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(notificationManager != null)
            notificationManager.notify(id, builder.build());
    }

}
