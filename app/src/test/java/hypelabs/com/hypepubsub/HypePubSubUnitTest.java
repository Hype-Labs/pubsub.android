package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    public void testProcessSubscribeAndUnsubscribe() throws NoSuchAlgorithmException
    {
        HypePubSub hpb = HypePubSub.getInstance();

        assertEquals(hpb.managedServices.size(), 0);

        assertEquals(hpb.processUnsubscribeReq(HPB_TEST_SERVICE1, HPB_TEST_CLIENT1), -1);

        // Basic test with 2 services with 3 clients
        hpb.processSubscribeReq(HPB_TEST_SERVICE1, HPB_TEST_CLIENT1);
        assertEquals(hpb.managedServices.size(), 1);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT2);
        assertEquals(hpb.managedServices.size(), 2);
        hpb.processSubscribeReq(HPB_TEST_SERVICE1, HPB_TEST_CLIENT3);
        assertEquals(hpb.managedServices.size(), 2);

        ServiceManager service1 = hpb.managedServices.find(HPB_TEST_SERVICE1);
        assertNotNull(service1);
        assertEquals(service1.subscribers.size(), 2);
        ServiceManager service2 = hpb.managedServices.find(HPB_TEST_SERVICE2);
        assertNotNull(service2);
        assertEquals(service2.subscribers.size(), 1);

        assertNotNull(service1.subscribers.find(HPB_TEST_CLIENT1));
        assertNull(service1.subscribers.find(HPB_TEST_CLIENT2));
        assertNotNull(service1.subscribers.find(HPB_TEST_CLIENT3));
        assertNull(service2.subscribers.find(HPB_TEST_CLIENT1));
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT2));
        assertNull(service2.subscribers.find(HPB_TEST_CLIENT3));

        // Test unsubscriptions on the 1st managed service
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE1, HPB_TEST_CLIENT1);
        assertEquals(service1.subscribers.size(), 1);
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE1, HPB_TEST_CLIENT2);
        assertEquals(service1.subscribers.size(), 1);
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE1, HPB_TEST_CLIENT3);
        assertEquals(hpb.managedServices.size(), 1);

        // Test subscriptions on the 2nd managed service
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT1);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT2);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT3);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT4);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT5);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT6);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT7);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT8);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT9);
        hpb.processSubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT10);
        assertEquals(service2.subscribers.size(), 10);
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT1));
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT2));
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT3));
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT4));
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT5));
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT6));
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT7));
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT8));
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT9));
        assertNotNull(service2.subscribers.find(HPB_TEST_CLIENT10));

        // Test unsubscriptions on the 2nd managed service
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT3);
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT7);
        hpb.processUnsubscribeReq(HPB_TEST_SERVICE2, HPB_TEST_CLIENT9);
        assertEquals(service2.subscribers.size(), 7);
        assertNull(service2.subscribers.find(HPB_TEST_CLIENT3));
        assertNull(service2.subscribers.find(HPB_TEST_CLIENT7));
        assertNull(service2.subscribers.find(HPB_TEST_CLIENT9));
    }
}
