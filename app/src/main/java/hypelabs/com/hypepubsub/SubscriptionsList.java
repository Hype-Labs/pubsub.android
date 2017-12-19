package hypelabs.com.hypepubsub;

import android.content.Context;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;


public class SubscriptionsList
{
    // Used composition instead of inheritance to hide the methods that shouldn't be called in
    // a SubscriptionsList.
    final private LinkedList<Subscription> subscriptions = new LinkedList<>();
    private SubscriptionsAdapter subscriptionsAdapter = null;

    public synchronized boolean addSubscription(Subscription subscription) {
        if (containsSubscriptionWithServiceKey(subscription.serviceKey)) {
            return false;
        }
        return subscriptions.add(subscription);
    }

    public synchronized boolean removeSubscriptionWithServiceName(String serviceName) {
        byte serviceKey[] = HpsGenericUtils.stringHash(serviceName);
        Subscription subscription = findSubscriptionWithServiceKey(serviceKey);
        if(subscription == null) {
            return false;
        }
        return subscriptions.remove(subscription);
    }

    public synchronized Subscription findSubscriptionWithServiceKey(byte serviceKey[]) {
        ListIterator<Subscription> it = listIterator();
        while(it.hasNext()) {
            Subscription currentSubs = it.next();
            if(Arrays.equals(currentSubs.serviceKey, serviceKey)) {
                return currentSubs;
            }
        }
        return null;
    }

    public synchronized boolean containsSubscriptionWithServiceKey(byte serviceKey[]) {
        if(findSubscriptionWithServiceKey(serviceKey) == null) {
            return false;
        }
        return true;
    }

    public synchronized boolean containsSubscriptionWithServiceName(String serviceName) {
        byte serviceKey[] = HpsGenericUtils.stringHash(serviceName);
        return containsSubscriptionWithServiceKey(serviceKey);
    }

    public synchronized SubscriptionsAdapter getSubscriptionsAdapter(Context context) {
        if(subscriptionsAdapter == null) {
            subscriptionsAdapter = new SubscriptionsAdapter(context, subscriptions);
        }
        return  subscriptionsAdapter;
    }

    //////////////////////////////////////////////////////////////////////////////
    //  LinkedList methods to enable
    //////////////////////////////////////////////////////////////////////////////

    public synchronized ListIterator<Subscription> listIterator() {
        return subscriptions.listIterator();
    }

    public synchronized int size()
    {
        return subscriptions.size();
    }

    public synchronized Subscription get(int index)
    {
        return subscriptions.get(index);
    }

}