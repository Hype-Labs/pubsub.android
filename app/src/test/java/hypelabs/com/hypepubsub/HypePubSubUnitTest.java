package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class HypePubSubUnitTest
{
    byte HPS_TEST_OWN_CLIENT[] = new byte[] {(byte) 0x11, (byte) 0x12, (byte) 0x12, (byte) 0x11, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};

    byte HPS_TEST_CLIENT1[] = new byte[] {(byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};
    byte HPS_TEST_CLIENT2[] = new byte[] {(byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
    byte HPS_TEST_CLIENT3[] = new byte[] {(byte) 0x6a, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
    byte HPS_TEST_CLIENT4[] = new byte[] {(byte) 0xf2, (byte) 0x66, (byte) 0xd8, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
    byte HPS_TEST_CLIENT5[] = new byte[] {(byte) 0xd8, (byte) 0x66, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
    byte HPS_TEST_CLIENT6[] = new byte[] {(byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56};
    byte HPS_TEST_CLIENT7[] = new byte[] {(byte) 0x23, (byte) 0x3b, (byte) 0xc2, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91};
    byte HPS_TEST_CLIENT8[] = new byte[] {(byte) 0xc2, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b};
    byte HPS_TEST_CLIENT9[] = new byte[] {(byte) 0x56, (byte) 0xdb, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
    byte HPS_TEST_CLIENT10[] = new byte[] {(byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};

    byte HPS_TEST_SERVICE1[] = new byte[] {(byte) 0x8b, (byte) 0xa1, (byte) 0x04, (byte) 0x94, (byte) 0xc2, (byte) 0x9d, (byte) 0x24, (byte) 0x76, (byte) 0x04, (byte) 0xb1, (byte) 0x5c, (byte) 0xd2, (byte) 0x40, (byte) 0x01, (byte) 0x32, (byte) 0x33, (byte) 0x58, (byte) 0xa8, (byte) 0x9b, (byte) 0xf5};
    byte HPS_TEST_SERVICE2[] = new byte[] {(byte) 0xf2, (byte) 0x95, (byte) 0xa7, (byte) 0x85, (byte) 0x27, (byte) 0x72, (byte) 0xfd, (byte) 0x6c, (byte) 0x88, (byte) 0xb5, (byte) 0x14, (byte) 0x37, (byte) 0xf3, (byte) 0x5e, (byte) 0x5e, (byte) 0x73, (byte) 0x08, (byte) 0x9f, (byte) 0xad, (byte) 0x3e};
    
    @Test
    public void testProcessSubscribeAndUnsubscribe() throws NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException, UnsupportedEncodingException
    {
        FakeHypeInstance ownInstance = new FakeHypeInstance(HPS_TEST_OWN_CLIENT, null, false);

        FakeHypeInstance instance1 = new FakeHypeInstance(HPS_TEST_CLIENT1, null, false);
        FakeHypeInstance instance2 = new FakeHypeInstance(HPS_TEST_CLIENT2, null, false);
        FakeHypeInstance instance3 = new FakeHypeInstance(HPS_TEST_CLIENT3, null, false);
        FakeHypeInstance instance4 = new FakeHypeInstance(HPS_TEST_CLIENT4, null, false);
        FakeHypeInstance instance5 = new FakeHypeInstance(HPS_TEST_CLIENT5, null, false);
        FakeHypeInstance instance6 = new FakeHypeInstance(HPS_TEST_CLIENT6, null, false);
        FakeHypeInstance instance7 = new FakeHypeInstance(HPS_TEST_CLIENT7, null, false);
        FakeHypeInstance instance8 = new FakeHypeInstance(HPS_TEST_CLIENT8, null, false);
        FakeHypeInstance instance9 = new FakeHypeInstance(HPS_TEST_CLIENT9, null, false);
        FakeHypeInstance instance10 = new FakeHypeInstance(HPS_TEST_CLIENT10, null, false);

        HypePubSub hps = HypePubSub.getInstance();
        Network network = Network.getInstance();
        network.setOwnClient(ownInstance);

        assertEquals(0, hps.managedServices.size());

        // Basic test with 2 services with 3 clients
        hps.processSubscribeReq(HPS_TEST_SERVICE1, instance1);
        assertEquals(1, hps.managedServices.size());
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance2);
        assertEquals(2, hps.managedServices.size());
        hps.processSubscribeReq(HPS_TEST_SERVICE1, instance3);
        assertEquals(2, hps.managedServices.size());

        ServiceManager service1 = hps.managedServices.findServiceManagerWithKey(HPS_TEST_SERVICE1);
        assertNotNull(service1);
        assertEquals(2, service1.subscribers.size());
        ServiceManager service2 = hps.managedServices.findServiceManagerWithKey(HPS_TEST_SERVICE2);
        assertNotNull(service2);
        assertEquals(1, service2.subscribers.size());

        assertNotNull(service1.subscribers.findClientWithInstance(instance1));
        assertNull(service1.subscribers.findClientWithInstance(instance2));
        assertNotNull(service1.subscribers.findClientWithInstance(instance3));
        assertNull(service2.subscribers.findClientWithInstance(instance1));
        assertNotNull(service2.subscribers.findClientWithInstance(instance2));
        assertNull(service2.subscribers.findClientWithInstance(instance3));

        // Test unsubscriptions on the 1st managed service
        hps.processUnsubscribeReq(HPS_TEST_SERVICE1, instance1);
        assertEquals(1, service1.subscribers.size());
        hps.processUnsubscribeReq(HPS_TEST_SERVICE1, instance2);
        assertEquals(1, service1.subscribers.size());
        hps.processUnsubscribeReq(HPS_TEST_SERVICE1, instance3);
        assertEquals(1, hps.managedServices.size());

        // Test subscriptions on the 2nd managed service
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance1);
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance2);
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance3);
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance4);
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance5);
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance6);
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance7);
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance8);
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance9);
        hps.processSubscribeReq(HPS_TEST_SERVICE2, instance10);
        assertEquals(10, service2.subscribers.size());
        assertNotNull(service2.subscribers.findClientWithInstance(instance1));
        assertNotNull(service2.subscribers.findClientWithInstance(instance2));
        assertNotNull(service2.subscribers.findClientWithInstance(instance3));
        assertNotNull(service2.subscribers.findClientWithInstance(instance4));
        assertNotNull(service2.subscribers.findClientWithInstance(instance5));
        assertNotNull(service2.subscribers.findClientWithInstance(instance6));
        assertNotNull(service2.subscribers.findClientWithInstance(instance7));
        assertNotNull(service2.subscribers.findClientWithInstance(instance8));
        assertNotNull(service2.subscribers.findClientWithInstance(instance9));
        assertNotNull(service2.subscribers.findClientWithInstance(instance10));

        // Test unsubscriptions on the 2nd managed service
        hps.processUnsubscribeReq(HPS_TEST_SERVICE2, instance3);
        hps.processUnsubscribeReq(HPS_TEST_SERVICE2, instance7);
        hps.processUnsubscribeReq(HPS_TEST_SERVICE2, instance9);
        assertEquals(7, service2.subscribers.size());
        assertNull(service2.subscribers.findClientWithInstance(instance3));
        assertNull(service2.subscribers.findClientWithInstance(instance7));
        assertNull(service2.subscribers.findClientWithInstance(instance9));
    }
}
