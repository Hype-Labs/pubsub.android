package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class SubscriptionsListUnitTest
{
    @Test
    public void testSubscriptionsList() throws NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException {
        String SERVICE1_NAME = "HypeCoffe";
        String SERVICE2_NAME = "HypeTea";
        String SERVICE3_NAME = "HypeBeer";
        byte SERVICE1_KEY[] = new byte[] {(byte) 0x8b, (byte) 0xa1, (byte) 0x04, (byte) 0x94, (byte) 0xc2, (byte) 0x9d, (byte) 0x24, (byte) 0x76, (byte) 0x04, (byte) 0xb1, (byte) 0x5c, (byte) 0xd2, (byte) 0x40, (byte) 0x01, (byte) 0x32, (byte) 0x33, (byte) 0x58, (byte) 0xa8, (byte) 0x9b, (byte) 0xf5};
        byte SERVICE2_KEY[] = new byte[] {(byte) 0xf2, (byte) 0x95, (byte) 0xa7, (byte) 0x85, (byte) 0x27, (byte) 0x72, (byte) 0xfd, (byte) 0x6c, (byte) 0x88, (byte) 0xb5, (byte) 0x14, (byte) 0x37, (byte) 0xf3, (byte) 0x5e, (byte) 0x5e, (byte) 0x73, (byte) 0x08, (byte) 0x9f, (byte) 0xad, (byte) 0x3e};
        byte SERVICE3_KEY[] = new byte[] {(byte) 0xfb, (byte) 0x42, (byte) 0x14, (byte) 0x7b, (byte) 0xab, (byte) 0x42, (byte) 0xa4, (byte) 0xee, (byte) 0x5d, (byte) 0xa9, (byte) 0xde, (byte) 0x58, (byte) 0xa0, (byte) 0xa5, (byte) 0x07, (byte) 0x80, (byte) 0xdf, (byte) 0x94, (byte) 0x48, (byte) 0x88};
        byte MANAGER_ID1[] = new byte[] {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11, (byte) 0x12};
        byte MANAGER_ID2[] = new byte[] {(byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11};
        byte MANAGER_ID3[] = new byte[] {(byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10};

        FakeHypeInstance instance1 = new FakeHypeInstance(MANAGER_ID1, null, false);
        FakeHypeInstance instance2 = new FakeHypeInstance(MANAGER_ID2, null, false);
        FakeHypeInstance instance3 = new FakeHypeInstance(MANAGER_ID3, null, false);

        // Test the creation of the subscriptions list
        Subscription auxSubscr;
        SubscriptionsList subscriptions = new SubscriptionsList();
        assertNotNull(subscriptions);
        assertEquals(0, subscriptions.size());

        // Add 3 subscriptions to the list
        subscriptions.add(SERVICE3_NAME, instance3);
        subscriptions.add(SERVICE1_NAME, instance1);
        subscriptions.add(SERVICE2_NAME, instance2);

        // Validate that the subscriptions are inserted in the right order
        assertArrayEquals(MANAGER_ID3, subscriptions.get(0).manager.getIdentifier());
        assertEquals(SERVICE3_NAME, subscriptions.get(0).serviceName);
        assertArrayEquals(MANAGER_ID1, subscriptions.get(1).manager.getIdentifier());
        assertEquals(SERVICE1_NAME, subscriptions.get(1).serviceName);
        assertArrayEquals(MANAGER_ID2, subscriptions.get(2).manager.getIdentifier());
        assertEquals(SERVICE2_NAME, subscriptions.get(2).serviceName);
        assertEquals(3, subscriptions.size());

        // Test find against existent and non-existent service keys
        byte NON_EXISTENT_KEY[] = new byte[] {(byte) 0xee, (byte) 0x5d, (byte) 0xa9, (byte) 0xde, (byte) 0x58, (byte) 0xa0, (byte) 0xa5, (byte) 0xfb, (byte) 0x42, (byte) 0x14, (byte) 0x7b, (byte) 0xab, (byte) 0x42, (byte) 0xa4, (byte) 0x07, (byte) 0x80, (byte) 0xdf, (byte) 0x94, (byte) 0x48, (byte) 0x88};
        assertNotNull(subscriptions.find(SERVICE1_KEY));
        assertNotNull(subscriptions.find(SERVICE2_KEY));
        assertNotNull(subscriptions.find(SERVICE3_KEY));
        assertNull(subscriptions.find(NON_EXISTENT_KEY));

        // Test element removal
        subscriptions.remove(SERVICE3_NAME);
        assertArrayEquals(MANAGER_ID1, subscriptions.get(0).manager.getIdentifier());
        assertEquals(SERVICE1_NAME, subscriptions.get(0).serviceName);
        assertArrayEquals(MANAGER_ID2, subscriptions.get(1).manager.getIdentifier());
        assertEquals(SERVICE2_NAME, subscriptions.get(1).serviceName);
        assertEquals(2, subscriptions.size());

        // Test non-existent element removal
        subscriptions.remove("NonExistentService");
        assertEquals(2, subscriptions.size());

        // Test element removal
        subscriptions.remove(SERVICE2_NAME);
        assertArrayEquals(MANAGER_ID1, subscriptions.get(0).manager.getIdentifier());
        assertEquals(SERVICE1_NAME, subscriptions.get(0).serviceName);
        assertEquals(1, subscriptions.size());

        // Test last element removal
        subscriptions.remove(SERVICE1_NAME);
        assertEquals(0, subscriptions.size());
    }
}