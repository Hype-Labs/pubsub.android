package hypelabs.com.hypepubsub;

import android.util.Log;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;

public class HypePubSub
{
    static HypePubSub hpb = null; // Singleton

    ListSubscriptions ownSubscriptions;
    ListServiceManagers managedServices;

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
        this.ownSubscriptions = new ListSubscriptions();
        this.managedServices = new ListServiceManagers();
    }

    int issueSubscribeReq(String serviceName) throws NoSuchAlgorithmException, IOException
    {
        Network network = Network.getInstance();
        Protocol protocol = Protocol.getInstance();
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        byte serviceKey[] = md.digest(serviceName.getBytes());
        byte managerId[] = network.getServiceManagerId(serviceKey);

        // Add subscription to the list of own subscriptions. Only adds if it doesn't exist yet.
        ownSubscriptions.add(serviceName, managerId);

        // if this client is the manager of the service we don't need to send the subscribe message to
        // the protocol manager
        if(network.ownClient.id.equals(managerId))
            this.processSubscribeReq(serviceKey, network.ownClient.id);
        else
            protocol.sendSubscribeMsg(serviceKey, managerId);

        return 0;
    }

    int issueUnsubscribeReq(String serviceName) throws NoSuchAlgorithmException, IOException {
        Network network = Network.getInstance();
        Protocol protocol = Protocol.getInstance();
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        byte serviceKey[] = md.digest(serviceName.getBytes());
        byte managerId[] = network.getServiceManagerId(serviceKey);

        if(ownSubscriptions.find(serviceKey) == null)
        {
            //printf("Trying to unsubscribe a service that was not previously subscribed: %s.\n", service_name);
            return -2;
        }

        // Remove the subscription from the list of own subscriptions
        ownSubscriptions.remove(serviceName);

        // if this client is the manager of the service we don't need to send the unsubscribe message
        // to the protocol manager
        if(network.ownClient.id.equals(managerId))
            this.processUnsubscribeReq(serviceKey, network.ownClient.id);
        else
            protocol.sendUnsubscribeMsg(serviceKey, managerId);

        return 0;
    }

    int issuePublishReq(String serviceName, String msg) throws NoSuchAlgorithmException, IOException {
        Network network = Network.getInstance();
        Protocol protocol = Protocol.getInstance();
        MessageDigest md = MessageDigest.getInstance("SHA-1");

        byte serviceKey[] = md.digest(serviceName.getBytes());
        byte managerId[] = network.getServiceManagerId(serviceKey);

        // if this client is the manager of the service we don't need to send the publish message
        // to the protocol manager
        if(network.ownClient.id.equals(managerId))
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
        if(serviceManager == null) // If the service does not exist we create it.
        {
            return -1;
        }

        serviceManager.subscribers.remove(requesterClientId);
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

            if(network.ownClient.id.equals(client.id))
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

    boolean removeSubscription(String serviceName)
    {
        ListIterator<Subscription> it = this.ownSubscriptions.listIterator();
        while(it.hasNext())
        {
            if(it.next().serviceName.equals(serviceName)) {
                it.remove();
            }
        }
        return false;
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
            if(newManagerId.equals(network.ownClient.id) == false)
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
            if (newManagerId.equals(subscription.managerId) == false)
            {
                subscription.managerId = newManagerId;
                this.issueSubscribeReq(subscription.serviceName); // re-send the subscribe request to the new manager
            }
        }
        return 0;
    }
}
