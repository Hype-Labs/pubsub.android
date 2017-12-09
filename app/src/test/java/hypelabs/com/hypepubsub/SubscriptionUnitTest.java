package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class SubscriptionUnitTest
{
    @Test
    public void testSubscription() throws NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException {
        String SUBS1_SERVICE_NAME = "CoffeService";
        String SUBS2_SERVICE_NAME = "HypeSports";
        String SUBS3_SERVICE_NAME = "HypeTech";
        byte SUBS1_MANAGER_ID[] = new byte[] {(byte) 0x85, (byte) 0xa9, (byte) 0xd4, (byte) 0xc4, (byte) 0xde, (byte) 0xd2, (byte) 0x87, (byte) 0x75, (byte) 0x0f, (byte) 0xc0, (byte) 0xed, (byte) 0x32};
        byte SUBS2_MANAGER_ID[] = new byte[] {(byte) 0xd5, (byte) 0x5b, (byte) 0x68, (byte) 0x8c, (byte) 0xe5, (byte) 0xe8, (byte) 0x3f, (byte) 0xd4, (byte) 0x5e, (byte) 0x5d, (byte) 0xe1, (byte) 0xae};
        byte SUBS3_MANAGER_ID[] = new byte[] {(byte) 0xdd, (byte) 0x52, (byte) 0x54, (byte) 0xe5, (byte) 0xe7, (byte) 0x7c, (byte) 0x07, (byte) 0xfa, (byte) 0x43, (byte) 0xb2, (byte) 0x70, (byte) 0x3d};
        byte SUBS1_SERVICE_KEY[] = new byte[] {(byte) 0x49, (byte) 0x48, (byte) 0xf4, (byte) 0xe7, (byte) 0x11, (byte) 0x80, (byte) 0x98, (byte) 0x0f, (byte) 0xd7, (byte) 0xb9, (byte) 0x6b, (byte) 0x22, (byte) 0xbe, (byte) 0x91, (byte) 0x54, (byte) 0x20, (byte) 0xe4, (byte) 0xcd, (byte) 0x7e, (byte) 0x2b};
        byte SUBS2_SERVICE_KEY[] = new byte[] {(byte) 0x2a, (byte) 0x0f, (byte) 0x55, (byte) 0x8c, (byte) 0x63, (byte) 0x6f, (byte) 0x89, (byte) 0x18, (byte) 0x4c, (byte) 0x64, (byte) 0xe2, (byte) 0x5b, (byte) 0xc3, (byte) 0x7b, (byte) 0x86, (byte) 0x39, (byte) 0xf5, (byte) 0xad, (byte) 0x8f, (byte) 0x69};
        byte SUBS3_SERVICE_KEY[] = new byte[] {(byte) 0x0f, (byte) 0x20, (byte) 0xf1, (byte) 0x8b, (byte) 0x65, (byte) 0xbf, (byte) 0x1e, (byte) 0xa0, (byte) 0xcb, (byte) 0x21, (byte) 0xda, (byte) 0x6f, (byte) 0xd8, (byte) 0xf9, (byte) 0xe5, (byte) 0x5b, (byte) 0x0b, (byte) 0xcb, (byte) 0x54, (byte) 0x84};

        FakeHypeInstance instance1 = new FakeHypeInstance(SUBS1_MANAGER_ID, null, false);
        FakeHypeInstance instance2 = new FakeHypeInstance(SUBS2_MANAGER_ID, null, false);
        FakeHypeInstance instance3 = new FakeHypeInstance(SUBS3_MANAGER_ID, null, false);

        Subscription subsc1 = new Subscription(SUBS1_SERVICE_NAME, new Client(instance1));
        Subscription subsc2 = new Subscription(SUBS2_SERVICE_NAME, new Client(instance2));
        Subscription subsc3 = new Subscription(SUBS3_SERVICE_NAME, new Client(instance3));

        assertNotNull(subsc1.serviceKey);
        assertNotNull(subsc1.manager.instance.getIdentifier());
        assertNotNull(subsc2.serviceKey);
        assertNotNull(subsc2.manager.instance.getIdentifier());
        assertNotNull(subsc3.serviceKey);
        assertNotNull(subsc3.manager.instance.getIdentifier());

        assertEquals(SUBS1_SERVICE_NAME, subsc1.serviceName);
        assertEquals(SUBS2_SERVICE_NAME, subsc2.serviceName);
        assertEquals(SUBS3_SERVICE_NAME, subsc3.serviceName);

        assertArrayEquals(SUBS1_SERVICE_KEY, subsc1.serviceKey);
        assertArrayEquals(SUBS2_SERVICE_KEY, subsc2.serviceKey);
        assertArrayEquals(SUBS3_SERVICE_KEY, subsc3.serviceKey);

        assertArrayEquals(SUBS1_MANAGER_ID, subsc1.manager.instance.getIdentifier());
        assertArrayEquals(SUBS2_MANAGER_ID, subsc2.manager.instance.getIdentifier());
        assertArrayEquals(SUBS3_MANAGER_ID, subsc3.manager.instance.getIdentifier());
    }
}