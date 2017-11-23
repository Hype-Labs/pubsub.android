package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class HypePubSubUnitTest
{

    byte HPB_TEST_CLIENT1[] = new byte[] {(byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};
    byte HPB_TEST_CLIENT2[] = new byte[] {(byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
    byte HPB_TEST_CLIENT3[] = new byte[] {(byte) 0x6a, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
    byte HPB_TEST_CLIENT4[] = new byte[] {(byte) 0xf2, (byte) 0x66, (byte) 0xd8, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
    byte HPB_TEST_CLIENT5[] = new byte[] {(byte) 0xd8, (byte) 0x66, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
    byte HPB_TEST_CLIENT6[] = new byte[] {(byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56};
    byte HPB_TEST_CLIENT7[] = new byte[] {(byte) 0x23, (byte) 0x3b, (byte) 0xc2, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91};
    byte HPB_TEST_CLIENT8[] = new byte[] {(byte) 0xc2, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b};
    byte HPB_TEST_CLIENT9[] = new byte[] {(byte) 0x56, (byte) 0xdb, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
    byte HPB_TEST_CLIENT10[] = new byte[] {(byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};

    byte HPB_TEST_SERVICE1[] = new byte[] {(byte) 0x8b, (byte) 0xa1, (byte) 0x04, (byte) 0x94, (byte) 0xc2, (byte) 0x9d, (byte) 0x24, (byte) 0x76, (byte) 0x04, (byte) 0xb1, (byte) 0x5c, (byte) 0xd2, (byte) 0x40, (byte) 0x01, (byte) 0x32, (byte) 0x33, (byte) 0x58, (byte) 0xa8, (byte) 0x9b, (byte) 0xf5};
    byte HPB_TEST_SERVICE2[] = new byte[] {(byte) 0xf2, (byte) 0x95, (byte) 0xa7, (byte) 0x85, (byte) 0x27, (byte) 0x72, (byte) 0xfd, (byte) 0x6c, (byte) 0x88, (byte) 0xb5, (byte) 0x14, (byte) 0x37, (byte) 0xf3, (byte) 0x5e, (byte) 0x5e, (byte) 0x73, (byte) 0x08, (byte) 0x9f, (byte) 0xad, (byte) 0x3e};
    
    @Test
    public void testProcessSubscribeAndUnsubscribe() throws NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException, UnsupportedEncodingException
    {
        FakeHypeInstance instance1 = new FakeHypeInstance(HPB_TEST_CLIENT1, null, false);
        FakeHypeInstance instance2 = new FakeHypeInstance(HPB_TEST_CLIENT2, null, false);
        FakeHypeInstance instance3 = new FakeHypeInstance(HPB_TEST_CLIENT3, null, false);
        FakeHypeInstance instance4 = new FakeHypeInstance(HPB_TEST_CLIENT4, null, false);
        FakeHypeInstance instance5 = new FakeHypeInstance(HPB_TEST_CLIENT5, null, false);
        FakeHypeInstance instance6 = new FakeHypeInstance(HPB_TEST_CLIENT6, null, false);
        FakeHypeInstance instance7 = new FakeHypeInstance(HPB_TEST_CLIENT7, null, false);
        FakeHypeInstance instance8 = new FakeHypeInstance(HPB_TEST_CLIENT8, null, false);
        FakeHypeInstance instance9 = new FakeHypeInstance(HPB_TEST_CLIENT9, null, false);
        FakeHypeInstance instance10 = new FakeHypeInstance(HPB_TEST_CLIENT10, null, false);

        HypePubSub hpb = HypePubSub.getInstance();

        assertEquals(0, hpb.managedServices.size());

        assertEquals(-1, hpb.processUnsubscribeReq(HPB_TEST_SERVICE1, instance1));

        // Basic test with 2 services with 3 clients
        hpb.processSubscribeReq(HPB_TEST_SERVICE1, instance1);
        assertEquals(1, hpb.managedServices.size());
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance2);
        assertEquals(2, hpb.managedServices.size());
        hpb.processSubscribeReq(HPB_TEST_SERVICE1, instance3);
        assertEquals(2, hpb.managedServices.size());

        ServiceManager service1 = hpb.managedServices.find(HPB_TEST_SERVICE1);
        assertNotNull(service1);
        assertEquals(2, service1.subscribers.size());
        ServiceManager service2 = hpb.managedServices.find(HPB_TEST_SERVICE2);
        assertNotNull(service2);
        assertEquals(1, service2.subscribers.size());

        assertNotNull(service1.subscribers.find(instance1));
        assertNull(service1.subscribers.find(instance2));
        assertNotNull(service1.subscribers.find(instance3));
        assertNull(service2.subscribers.find(instance1));
        assertNotNull(service2.subscribers.find(instance2));
        assertNull(service2.subscribers.find(instance3));

        // Test unsubscriptions on the 1st managed service
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE1, instance1);
        assertEquals(1, service1.subscribers.size());
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE1, instance2);
        assertEquals(1, service1.subscribers.size());
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE1, instance3);
        assertEquals(1, hpb.managedServices.size());

        // Test subscriptions on the 2nd managed service
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance1);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance2);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance3);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance4);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance5);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance6);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance7);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance8);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance9);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, instance10);
        assertEquals(10, service2.subscribers.size());
        assertNotNull(service2.subscribers.find(instance1));
        assertNotNull(service2.subscribers.find(instance2));
        assertNotNull(service2.subscribers.find(instance3));
        assertNotNull(service2.subscribers.find(instance4));
        assertNotNull(service2.subscribers.find(instance5));
        assertNotNull(service2.subscribers.find(instance6));
        assertNotNull(service2.subscribers.find(instance7));
        assertNotNull(service2.subscribers.find(instance8));
        assertNotNull(service2.subscribers.find(instance9));
        assertNotNull(service2.subscribers.find(instance10));

        // Test unsubscriptions on the 2nd managed service
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE2, instance3);
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE2, instance7);
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE2, instance9);
        assertEquals(7, service2.subscribers.size());
        assertNull(service2.subscribers.find(instance3));
        assertNull(service2.subscribers.find(instance7));
        assertNull(service2.subscribers.find(instance9));
    }
}
