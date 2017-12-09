package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class ServiceManagerUnitTest
{
    @Test
    public void testServiceManager() throws NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException {
        byte SERVICE1_KEY[] = {(byte) 0x49, (byte) 0x48, (byte) 0xf4, (byte) 0xe7, (byte) 0x11, (byte) 0x80, (byte) 0x98, (byte) 0x0f, (byte) 0xd7, (byte) 0xb9, (byte) 0x6b, (byte) 0x22, (byte) 0xbe, (byte) 0x91, (byte) 0x54, (byte) 0x20, (byte) 0xe4, (byte) 0xcd, (byte) 0x7e, (byte) 0x2b};
        byte SERVICE2_KEY[] = {(byte) 0x0f, (byte) 0x20, (byte) 0xf1, (byte) 0x8b, (byte) 0x65, (byte) 0xbf, (byte) 0x1e, (byte) 0xa0, (byte) 0xcb, (byte) 0x21, (byte) 0xda, (byte) 0x6f, (byte) 0xd8, (byte) 0xf9, (byte) 0xe5, (byte) 0x5b, (byte) 0x0b, (byte) 0xcb, (byte) 0x54, (byte) 0x84};

        ServiceManager serv1 = new ServiceManager(SERVICE1_KEY);
        ServiceManager serv2 = new ServiceManager(SERVICE2_KEY);

        assertNotNull(serv1.serviceKey);
        assertNotNull(serv2.serviceKey);
        assertNotNull(serv1.subscribers);
        assertNotNull(serv2.subscribers);

        assertArrayEquals(SERVICE1_KEY, serv1.serviceKey);
        assertArrayEquals(SERVICE2_KEY, serv2.serviceKey);
        assertEquals(0, serv1.subscribers.size());
        assertEquals(0, serv2.subscribers.size());

        byte SUBSCRIBER_ID1[] = {(byte) 0x85, (byte) 0xa9, (byte) 0xd4, (byte) 0xc4, (byte) 0xde, (byte) 0xd2, (byte) 0x87, (byte) 0x75, (byte) 0x0f, (byte) 0xc0, (byte) 0xed, (byte) 0x32};
        byte SUBSCRIBER_ID2[] = {(byte) 0xe7, (byte) 0x79, (byte) 0x34, (byte) 0x6c, (byte) 0x66, (byte) 0x9c, (byte) 0x17, (byte) 0xf4, (byte) 0x34, (byte) 0xc8, (byte) 0xce, (byte) 0x0e};

        FakeHypeInstance instance1 = new FakeHypeInstance(SUBSCRIBER_ID1, null, false);
        FakeHypeInstance instance2 = new FakeHypeInstance(SUBSCRIBER_ID2, null, false);

        // Test add_subscriber
        serv1.subscribers.addClient(new Client(instance1));
        assertEquals(1, serv1.subscribers.size());
        serv1.subscribers.addClient(new Client(instance2));
        assertEquals(2, serv1.subscribers.size());
        serv1.subscribers.addClient(new Client(instance1)); // Add duplicated subscriber
        assertEquals(2, serv1.subscribers.size());
        assertArrayEquals(SUBSCRIBER_ID1, serv1.subscribers.get(0).instance.getIdentifier());
        assertArrayEquals(SUBSCRIBER_ID2, serv1.subscribers.get(1).instance.getIdentifier());

        // Test remove_subscriber
        serv1.subscribers.removeClientWithInstance(instance2);
        assertEquals(1, serv1.subscribers.size());
        assertArrayEquals(SUBSCRIBER_ID1, serv1.subscribers.get(0).instance.getIdentifier());
        serv1.subscribers.removeClientWithInstance(instance2); // Remove subscriber that was already removed
        assertEquals(1, serv1.subscribers.size());
        assertArrayEquals(SUBSCRIBER_ID1, serv1.subscribers.get(0).instance.getIdentifier());
        serv1.subscribers.removeClientWithInstance(instance1);
        assertEquals(0, serv1.subscribers.size());
    }
}