package hypelabs.com.hypepubsub;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;


public class SubscriptionsList
{
    // Used composition instead of inheritance to hide the methods that shouldn't be called in
    // a SubscriptionsList.
    private LinkedList<Subscription> subscriptions = new LinkedList<>();

    public int add(String serviceName, byte managerId[]) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte serviceKey[] = md.digest(serviceName.getBytes());

        if(find(serviceKey) != null)
            return -1;

        subscriptions.add(new Subscription(serviceName, managerId));
        return 0;
    }

    public int remove(String serviceName) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte serviceKey[] = md.digest(serviceName.getBytes());

        Subscription subscription = find(serviceKey);
        if(subscription == null)
            return -1;

        subscriptions.remove(subscription);
        return 0;
    }

    public Subscription find(byte serviceKey[])
    {
        ListIterator<Subscription> it = listIterator();
        while(it.hasNext())
        {
            Subscription currentSubs = it.next();
            if(Arrays.equals(currentSubs.serviceKey, serviceKey)) {
                return currentSubs;
            }
        }
        return null;
    }

    // Methods from LinkedList that we want to enable.
    public ListIterator<Subscription> listIterator() {
        return subscriptions.listIterator();
    }

    public int size() {
        return subscriptions.size();
    }

    public Subscription get(int index) {
        return subscriptions.get(index);
    }
}