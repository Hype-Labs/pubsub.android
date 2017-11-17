package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ProtocolUnitTest
{
    @Test
    public void testProtocol() throws IOException
    {
        testMessageSending();
    }

    void testMessageSending() throws IOException
    {
        testSendingSubscribe();
        testSendingUnsubscribe();
        testSendingPublish();
        testSendingInfo();
    }

    void testSendingSubscribe() throws IOException
    {
        Protocol protocol = Protocol.getInstance();

        byte DEST_ID1[] = new byte[] {(byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};
        byte SERVICE_KEY1[] = new byte[] {(byte) 0x9a, (byte) 0xc1, (byte) 0xb0, (byte) 0x41, (byte) 0x5e, (byte) 0x0a, (byte) 0x97, (byte) 0x73, (byte) 0x8c, (byte) 0x57, (byte) 0xe7, (byte) 0xe6, (byte) 0x3f, (byte) 0x68, (byte) 0x50, (byte) 0xab, (byte) 0x21, (byte) 0xe4, (byte) 0x7e, (byte) 0xb4};
        byte DEST_ID2[] = new byte[] {(byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
        byte SERVICE_KEY2[] = new byte[] {(byte) 0xe4, (byte) 0x9a, (byte) 0xa7, (byte) 0x79, (byte) 0x2c, (byte) 0xf4, (byte) 0xfd, (byte) 0x09, (byte) 0x6c, (byte) 0x10, (byte) 0x3f, (byte) 0x4b, (byte) 0xa4, (byte) 0x63, (byte) 0xe2, (byte) 0x7b, (byte) 0x91, (byte) 0x60, (byte) 0x9e, (byte) 0x6b};

        byte packet[] = protocol.sendSubscribeMsg(SERVICE_KEY1, DEST_ID1);
        assertEquals(packet[0], (byte) Protocol.MessageType.SUBSCRIBE_SERVICE.ordinal());
        assertArrayEquals(Arrays.copyOfRange(packet, 1, packet.length), SERVICE_KEY1);

        packet = protocol.sendSubscribeMsg(SERVICE_KEY2, DEST_ID2);
        assertEquals(packet[0], (byte) Protocol.MessageType.SUBSCRIBE_SERVICE.ordinal());
        assertArrayEquals(Arrays.copyOfRange(packet, 1, packet.length), SERVICE_KEY2);
    }

    void testSendingUnsubscribe() throws IOException
    {

    }

    void testSendingPublish() throws IOException
    {

    }

    void testSendingInfo() throws IOException
    {

    }


}
