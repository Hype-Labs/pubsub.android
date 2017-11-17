package hypelabs.com.hypepubsub;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.ListIterator;


public class SubscriptionsList extends LinkedList<Subscription>
{
    public int add(String serviceName, byte managerId[]) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte serviceKey[] = md.digest(serviceName.getBytes());

        if(this.find(serviceKey) != null)
            return -1;

        super.add(new Subscription(serviceName, managerId));
        return 0;
    }

    public int remove(String serviceName) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte serviceKey[] = md.digest(serviceName.getBytes());

        Subscription subscription = this.find(serviceKey);
        if(subscription == null)
            return -1;

        super.remove(subscription);
        return 0;
    }

    public Subscription find(byte serviceKey[])
    {
        ListIterator<Subscription> it = this.listIterator();
        while(it.hasNext())
        {
            Subscription currentSubs = it.next();
            if(currentSubs.serviceKey.equals(serviceKey)) {
                return currentSubs;
            }
        }
        return null;
    }
}