package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class ServiceManagerUnitTest
{
    @Test
    public void testServiceManager() throws NoSuchAlgorithmException
    {
        byte SERVICE1_KEY[] = {(byte) 0x49, (byte) 0x48, (byte) 0xf4, (byte) 0xe7, (byte) 0x11, (byte) 0x80, (byte) 0x98, (byte) 0x0f, (byte) 0xd7, (byte) 0xb9, (byte) 0x6b, (byte) 0x22, (byte) 0xbe, (byte) 0x91, (byte) 0x54, (byte) 0x20, (byte) 0xe4, (byte) 0xcd, (byte) 0x7e, (byte) 0x2b};
        byte SERVICE2_KEY[] = {(byte) 0x0f, (byte) 0x20, (byte) 0xf1, (byte) 0x8b, (byte) 0x65, (byte) 0xbf, (byte) 0x1e, (byte) 0xa0, (byte) 0xcb, (byte) 0x21, (byte) 0xda, (byte) 0x6f, (byte) 0xd8, (byte) 0xf9, (byte) 0xe5, (byte) 0x5b, (byte) 0x0b, (byte) 0xcb, (byte) 0x54, (byte) 0x84};

        ServiceManager serv1 = new ServiceManager(SERVICE1_KEY);
        ServiceManager serv2 = new ServiceManager(SERVICE2_KEY);

        assertNotNull(serv1.serviceKey);
        assertNotNull(serv2.serviceKey);
        assertNotNull(serv1.subscribers);
        assertNotNull(serv2.subscribers);

        assertArrayEquals(serv1.serviceKey, SERVICE1_KEY);
        assertArrayEquals(serv2.serviceKey, SERVICE2_KEY);
        assertEquals(serv1.subscribers.size(), 0);
        assertEquals(serv2.subscribers.size(),0);

        // Test add_subscriber
        byte SUBSCRIBER_ID1[] = {(byte) 0x85, (byte) 0xa9, (byte) 0xd4, (byte) 0xc4, (byte) 0xde, (byte) 0xd2, (byte) 0x87, (byte) 0x75, (byte) 0x0f, (byte) 0xc0, (byte) 0xed, (byte) 0x32};
        byte SUBSCRIBER_ID2[] = {(byte) 0xe7, (byte) 0x79, (byte) 0x34, (byte) 0x6c, (byte) 0x66, (byte) 0x9c, (byte) 0x17, (byte) 0xf4, (byte) 0x34, (byte) 0xc8, (byte) 0xce, (byte) 0x0e};
        serv1.subscribers.add(SUBSCRIBER_ID1);
        assertEquals(serv1.subscribers.size(), 1);
        serv1.subscribers.add(SUBSCRIBER_ID2);
        assertEquals(serv1.subscribers.size(), 2);
        serv1.subscribers.add(SUBSCRIBER_ID1); // Add duplicated subscriber
        assertEquals(serv1.subscribers.size(), 2);
        assertArrayEquals(serv1.subscribers.get(0).id, SUBSCRIBER_ID1);
        assertArrayEquals(serv1.subscribers.get(1).id, SUBSCRIBER_ID2);

        // Test remove_subscriber
        serv1.subscribers.remove(SUBSCRIBER_ID2);
        assertEquals(serv1.subscribers.size(), 1);
        assertArrayEquals(serv1.subscribers.get(0).id, SUBSCRIBER_ID1);
        serv1.subscribers.remove(SUBSCRIBER_ID2); // Remove subscriber that was already removed
        assertEquals(serv1.subscribers.size(), 1);
        assertArrayEquals(serv1.subscribers.get(0).id, SUBSCRIBER_ID1);
        serv1.subscribers.remove(SUBSCRIBER_ID1);
        assertEquals(serv1.subscribers.size(), 0);
    }
}