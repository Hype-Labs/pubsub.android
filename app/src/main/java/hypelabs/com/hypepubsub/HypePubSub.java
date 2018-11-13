package hypelabs.com.hypepubsub;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.ListIterator;

import com.hypelabs.hype.Instance;

public class HypePubSub
{
    // Members
    final SubscriptionsList ownSubscriptions;
    final ServiceManagersList managedServices;

    // Private
    private static final String TAG =  HypePubSub.class.getName();
    private static final String HYPE_PUB_SUB_LOG_PREFIX = HpsConstants.LOG_PREFIX + "<HypePubSub> ";
    final private Network network = Network.getInstance();
    private int notificationID = 1;

    final private static HypePubSub hps = new HypePubSub(); // Early loading to avoid thread-safety issues
    public static HypePubSub getInstance()
    {
        return hps;
    }

    private HypePubSub()
    {
        this.ownSubscriptions = new SubscriptionsList();
        this.managedServices = new ServiceManagersList();
    }

    //////////////////////////////////////////////////////////////////////////////
    // Request Issuing
    //////////////////////////////////////////////////////////////////////////////

    boolean issueSubscribeReq(String serviceName) {
        byte serviceKey[] = HpsGenericUtils.stringHash(serviceName);
        Client managerClient = network.determineClientResponsibleForService(serviceKey);

        boolean wasSubscriptionAdded = ownSubscriptions.addSubscription(new Subscription(serviceName, managerClient));
        if(!wasSubscriptionAdded) {
            return false;
        }

        if(HpsGenericUtils.areClientsEqual(network.ownClient, managerClient)) {
            printIssueReqToHostInstanceLog("Subscribe", serviceName);
            processSubscribeReq(serviceKey, network.ownClient.instance); // bypass protocol manager
        }
        else {
            Protocol.sendSubscribeMsg(serviceKey, managerClient.instance);
        }
        return true;
    }

    boolean issueUnsubscribeReq(String serviceName) {
        byte serviceKey[] = HpsGenericUtils.stringHash(serviceName);
        Client managerClient = network.determineClientResponsibleForService(serviceKey);

        boolean wasSubscriptionRemoved =  ownSubscriptions.removeSubscriptionWithServiceName(serviceName);
        if(!wasSubscriptionRemoved) {
            return false;
        }

        if(HpsGenericUtils.areClientsEqual(network.ownClient, managerClient)) {
            printIssueReqToHostInstanceLog("Unsubscribe", serviceName);
            processUnsubscribeReq(serviceKey, network.ownClient.instance); // bypass protocol manager
        }
        else {
            Protocol.sendUnsubscribeMsg(serviceKey, managerClient.instance);
        }
        return true;
    }

    void issuePublishReq(String serviceName, String msg) {
        byte serviceKey[] = HpsGenericUtils.stringHash(serviceName);
        Client managerClient = network.determineClientResponsibleForService(serviceKey);

        if(HpsGenericUtils.areClientsEqual(network.ownClient, managerClient)) {
            printIssueReqToHostInstanceLog("Publish", serviceName);
            processPublishReq(serviceKey, msg); // bypass protocol manager
        }
        else {
            Protocol.sendPublishMsg(serviceKey, managerClient.instance, msg);
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // Request Processing
    //////////////////////////////////////////////////////////////////////////////

    synchronized void processSubscribeReq(byte serviceKey[], Instance requesterInstance) {
        Client managerClient = network.determineClientResponsibleForService(serviceKey);

        if( ! HpsGenericUtils.areClientsEqual(managerClient, network.ownClient)) {
            Log.i(TAG, String.format("%s Another instance should be responsible for the service 0x%s: %s",
                    HYPE_PUB_SUB_LOG_PREFIX, BinaryUtils.byteArrayToHexString(serviceKey),
                    HpsGenericUtils.getLogStrFromClient(managerClient)));
            return;
        }

        ServiceManager serviceManager = managedServices.findServiceManagerWithKey(serviceKey);
        if(serviceManager == null ) { // If the service does not exist we create it.
            Log.i(TAG, String.format("%s Processing Subscribe request for non-existent ServiceManager 0x%s. ServiceManager will be created.",
                    HYPE_PUB_SUB_LOG_PREFIX, BinaryUtils.byteArrayToHexString(serviceKey)));

            managedServices.addServiceManager(new ServiceManager(serviceKey));
            serviceManager = managedServices.getLast();
            updateManagedServicesUI();
        }

        Log.i(TAG, String.format("%s Adding instance %s to the list of subscribers of the service 0x%s",
                HYPE_PUB_SUB_LOG_PREFIX, HpsGenericUtils.getLogStrFromInstance(requesterInstance),
                BinaryUtils.byteArrayToHexString(serviceKey)));

        serviceManager.subscribers.addClient(new Client(requesterInstance));
    }

    synchronized void processUnsubscribeReq(byte serviceKey[], Instance requesterInstance) {
        ServiceManager serviceManager = managedServices.findServiceManagerWithKey(serviceKey);

        if(serviceManager == null) {
            Log.i(TAG, String.format("%s Processing Unsubscribe request for non-existent ServiceManager 0x%s. Nothing will be done",
                    HYPE_PUB_SUB_LOG_PREFIX, BinaryUtils.byteArrayToHexString(serviceKey)));
            return;
        }

        Log.i(TAG, String.format("%s Removing instance %s from the list of subscribers of the service 0x%s",
                HYPE_PUB_SUB_LOG_PREFIX, HpsGenericUtils.getLogStrFromInstance(requesterInstance),
                BinaryUtils.byteArrayToHexString(serviceKey)));

        serviceManager.subscribers.removeClientWithInstance(requesterInstance);

        if(serviceManager.subscribers.size() == 0) {
            managedServices.removeServiceManagerWithKey(serviceKey);
            updateManagedServicesUI(); // Updated UI after removing a managed service
        }
    }

    synchronized void processPublishReq(byte serviceKey[], String msg) {
        ServiceManager serviceManager = managedServices.findServiceManagerWithKey(serviceKey);

        if(serviceManager == null) {
            Log.i(TAG, String.format("%s Processing Publish request for non-existent ServiceManager 0x%s. Nothing will be done.",
                    HYPE_PUB_SUB_LOG_PREFIX, BinaryUtils.byteArrayToHexString(serviceKey)));
            return;
        }

        ListIterator<Client> it = serviceManager.subscribers.listIterator();
        while(it.hasNext()) {
            Client client = it.next();
            if(client == null)
                continue;

            if(HpsGenericUtils.areClientsEqual(network.ownClient, client)) {
                Log.i(TAG, String.format("%s Publishing info from service 0x%s to Host instance",
                        HYPE_PUB_SUB_LOG_PREFIX, BinaryUtils.byteArrayToHexString(serviceKey)));
                processInfoMsg(serviceKey, msg);
            }
            else {
                Log.i(TAG, String.format("%s Publishing info from service 0x%s to %s",
                        HYPE_PUB_SUB_LOG_PREFIX, BinaryUtils.byteArrayToHexString(serviceKey),
                        HpsGenericUtils.getLogStrFromClient(client)));
                Protocol.sendInfoMsg(serviceKey, client.instance, msg);
            }
        }
    }

    void processInfoMsg(byte serviceKey[], String msg) {
        Subscription subscription = ownSubscriptions.findSubscriptionWithServiceKey(serviceKey);

        if(subscription == null) {
            Log.i(TAG, String.format("%s Info received from the unsubscribed service 0x%s: %s",
                    HYPE_PUB_SUB_LOG_PREFIX, BinaryUtils.byteArrayToHexString(serviceKey),
                    msg));
            return;
        }

        String msgWithTimeStamp = HpsGenericUtils.getTimeStamp() + ": " + msg;
        subscription.receivedMsg.add(0, msgWithTimeStamp);

        updateMessagesUI();
        String notificationText = subscription.serviceName + ": " + msg;
        displayNotification(MainActivity.getContext(), notificationText, notificationID);

        Log.i(TAG, String.format("%s Info received from the subscribed service '%s': %s",
                HYPE_PUB_SUB_LOG_PREFIX, subscription.serviceName, msg));
    }

    synchronized void updateManagedServices() {
        Log.i(TAG, String.format("%s Executing updateManagedServices (%d services managed)",
                HYPE_PUB_SUB_LOG_PREFIX, managedServices.size()));

        ListIterator<ServiceManager> it = managedServices.listIterator();

        while(it.hasNext()) {
            ServiceManager managedService = it.next();

            // Check if a new Hype client with a closer key to this service key has appeared. If this happens
            // we removeClientWithInstance the service from the list of managed services of this Hype client.
            Client newManagerClient = network.determineClientResponsibleForService(managedService.serviceKey);

            Log.i(TAG, String.format("%s Analyzing ServiceManager from service 0x%s",
                    HYPE_PUB_SUB_LOG_PREFIX, BinaryUtils.byteArrayToHexString(managedService.serviceKey)));

            if( ! HpsGenericUtils.areClientsEqual(newManagerClient, network.ownClient)) {
                Log.i(TAG, String.format("%s The service 0x%s will be managed by: %s. ServiceManager will be removed",
                        HYPE_PUB_SUB_LOG_PREFIX, BinaryUtils.byteArrayToHexString(managedService.serviceKey),
                        HpsGenericUtils.getLogStrFromClient(newManagerClient)));

                it.remove();
                updateManagedServicesUI(); // Updated UI after removing a managed service
            }
        }
    }

    synchronized void updateOwnSubscriptions() {
        Log.i(TAG, String.format("%s Executing updateManagedServices (%d subscriptions)",
                HYPE_PUB_SUB_LOG_PREFIX, ownSubscriptions.size()));

        ListIterator<Subscription> it = ownSubscriptions.listIterator();
        while(it.hasNext()) {
            Subscription subscription = it.next();
            Client newManagerClient = network.determineClientResponsibleForService(subscription.serviceKey);

            Log.i(TAG, String.format("%s Analyzing subscription %s",
                    HYPE_PUB_SUB_LOG_PREFIX, HpsGenericUtils.getLogStrFromSubscription(subscription)));

            // If there is a node with a closer key to the service key we change the manager
            if( ! HpsGenericUtils.areClientsEqual(newManagerClient, subscription.manager)) {
                Log.i(TAG, String.format("%s The manager of the subscribed service '%s' has changed: %s. A new Subscribe message will be issued",
                        HYPE_PUB_SUB_LOG_PREFIX, subscription.serviceName,
                        HpsGenericUtils.getLogStrFromClient(newManagerClient)));

                subscription.manager = newManagerClient;

                if(HpsGenericUtils.areClientsEqual(network.ownClient, newManagerClient)){
                    processSubscribeReq(subscription.serviceKey, network.ownClient.instance); // bypass protocol manager
                }
                else {
                    Protocol.sendSubscribeMsg(subscription.serviceKey, newManagerClient.instance);
                }
            }
        }
    }

    synchronized void  removeSubscriptionsFromLostInstance(Instance instance) {
        Log.i(TAG, "Executing removeSubscriptionsFromLostInstance");
        ArrayList<byte[]> keysOfServicesToUnsubscribe = new ArrayList<>();
        for (int i=0; i < managedServices.size(); i++) {
            keysOfServicesToUnsubscribe.add(managedServices.get(i).serviceKey);
        }

        for (int i=0; i < keysOfServicesToUnsubscribe.size(); i++) {
            processUnsubscribeReq(keysOfServicesToUnsubscribe.get(i), instance);
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    // UI Methods
    //////////////////////////////////////////////////////////////////////////////

    private void updateManagedServicesUI() {
        ServiceManagersListActivity serviceManagersListActivity = ServiceManagersListActivity.getDefaultInstance();
        if (serviceManagersListActivity != null) {
            serviceManagersListActivity.updateInterface();
        }
    }

    private void updateMessagesUI() {
        MessagesActivity messagesActivity = MessagesActivity.getDefaultInstance();
        if (messagesActivity != null) {
            messagesActivity.updateInterface();
        }
    }

    private void displayNotification(Context context, String content, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HpsConstants.NOTIFICATIONS_CHANNEL);
        builder.setContentTitle(HpsConstants.NOTIFICATIONS_TITLE);
        builder.setContentText(content);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC); // to show content in lock screen

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // In newer Android version we must create the channel first
            NotificationChannel mChannel = new NotificationChannel(HpsConstants.NOTIFICATIONS_CHANNEL, HpsConstants.NOTIFICATIONS_CHANNEL, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(id, builder.build());

        notificationID++;
    }

    //////////////////////////////////////////////////////////////////////////////
    // Logging Methods
    //////////////////////////////////////////////////////////////////////////////

    private static void printIssueReqToHostInstanceLog(String msgType, String serviceName) {
        Log.i(TAG, String.format("%s Issuing %s for service '%s' to HOST instance",
                HYPE_PUB_SUB_LOG_PREFIX, msgType, serviceName));
    }
}
