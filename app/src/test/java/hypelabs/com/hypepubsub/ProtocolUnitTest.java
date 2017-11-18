package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
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
        testMessageReceiving();
    }

    void testMessageSending() throws IOException
    {
        testSendingSubscribe();
        testSendingUnsubscribe();
        testSendingPublish();
        testSendingInfo();
    }

    void testMessageReceiving() throws IOException
    {
        testGetMessageType();
    }

    void testSendingSubscribe() throws IOException
    {
        Protocol protocol = Protocol.getInstance();

        int offset;
        byte DEST_ID1[] = new byte[] {(byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};
        byte SERVICE_KEY1[] = new byte[] {(byte) 0x9a, (byte) 0xc1, (byte) 0xb0, (byte) 0x41, (byte) 0x5e, (byte) 0x0a, (byte) 0x97, (byte) 0x73, (byte) 0x8c, (byte) 0x57, (byte) 0xe7, (byte) 0xe6, (byte) 0x3f, (byte) 0x68, (byte) 0x50, (byte) 0xab, (byte) 0x21, (byte) 0xe4, (byte) 0x7e, (byte) 0xb4};
        byte DEST_ID2[] = new byte[] {(byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
        byte SERVICE_KEY2[] = new byte[] {(byte) 0xe4, (byte) 0x9a, (byte) 0xa7, (byte) 0x79, (byte) 0x2c, (byte) 0xf4, (byte) 0xfd, (byte) 0x09, (byte) 0x6c, (byte) 0x10, (byte) 0x3f, (byte) 0x4b, (byte) 0xa4, (byte) 0x63, (byte) 0xe2, (byte) 0x7b, (byte) 0x91, (byte) 0x60, (byte) 0x9e, (byte) 0x6b};

        offset = Protocol.MESSAGE_TYPE_BYTE_SIZE;

        byte packet[] = protocol.sendSubscribeMsg(SERVICE_KEY1, DEST_ID1);
        assertEquals(packet[0], (byte) Protocol.MessageType.SUBSCRIBE_SERVICE.ordinal());
        assertArrayEquals(Arrays.copyOfRange(packet, offset, packet.length), SERVICE_KEY1);

        packet = protocol.sendSubscribeMsg(SERVICE_KEY2, DEST_ID2);
        assertEquals(packet[0], (byte) Protocol.MessageType.SUBSCRIBE_SERVICE.ordinal());
        assertArrayEquals(Arrays.copyOfRange(packet, offset, packet.length), SERVICE_KEY2);
    }

    void testSendingUnsubscribe() throws IOException
    {
        Protocol protocol = Protocol.getInstance();

        int offset;
        byte packet[];
        byte DEST_ID1[] = new byte[] {(byte) 0x85, (byte) 0xa9, (byte) 0xd4, (byte) 0xc4, (byte) 0xde, (byte) 0xd2, (byte) 0x87, (byte) 0x75, (byte) 0x0f, (byte) 0xc0, (byte) 0xed, (byte) 0x32};
        byte SERVICE_KEY1[] = new byte[] {(byte) 0x05, (byte) 0xeb, (byte) 0x63, (byte) 0x7c, (byte) 0xbd, (byte) 0x3f, (byte) 0x33, (byte) 0x69, (byte) 0x1d, (byte) 0x74, (byte) 0x3c, (byte) 0x2a, (byte) 0x39, (byte) 0xaf, (byte) 0xee, (byte) 0xda, (byte) 0x5e, (byte) 0xc9, (byte) 0x45, (byte) 0xad};
        byte DEST_ID2[] = new byte[] {(byte) 0xe7, (byte) 0x79, (byte) 0x34, (byte) 0x6c, (byte) 0x66, (byte) 0x9c, (byte) 0x17, (byte) 0xf4, (byte) 0x34, (byte) 0xc8, (byte) 0xce, (byte) 0x0e};
        byte SERVICE_KEY2[] = new byte[] {(byte) 0xf6, (byte) 0xcb, (byte) 0x6d, (byte) 0x9d, (byte) 0xb0, (byte) 0x98, (byte) 0x91, (byte) 0x9b, (byte) 0x2d, (byte) 0x39, (byte) 0x55, (byte) 0x11, (byte) 0x41, (byte) 0xc5, (byte) 0xcb, (byte) 0xe7, (byte) 0x67, (byte) 0xb5, (byte) 0x06, (byte) 0xd6};

        offset = Protocol.MESSAGE_TYPE_BYTE_SIZE;

        packet = protocol.sendUnsubscribeMsg(SERVICE_KEY1, DEST_ID1);
        assertEquals(packet[0], (byte) Protocol.MessageType.UNSUBSCRIBE_SERVICE.ordinal());
        assertArrayEquals(Arrays.copyOfRange(packet, offset, packet.length), SERVICE_KEY1);

        packet = protocol.sendUnsubscribeMsg(SERVICE_KEY2, DEST_ID2);
        assertEquals(packet[0], (byte) Protocol.MessageType.UNSUBSCRIBE_SERVICE.ordinal());
        assertArrayEquals(Arrays.copyOfRange(packet, offset, packet.length), SERVICE_KEY2);
    }

    void testSendingPublish() throws IOException
    {
        Protocol protocol = Protocol.getInstance();

        byte packet[];
        int offset;
        byte DEST_ID1[] = new byte[] {(byte) 0x85, (byte) 0xa9, (byte) 0xd4, (byte) 0xc4, (byte) 0xde, (byte) 0xd2, (byte) 0x87, (byte) 0x75, (byte) 0x0f, (byte) 0xc0, (byte) 0xed, (byte) 0x32};
        byte SERVICE_KEY1[] = new byte[] {(byte) 0x05, (byte) 0xeb, (byte) 0x63, (byte) 0x7c, (byte) 0xbd, (byte) 0x3f, (byte) 0x33, (byte) 0x69, (byte) 0x1d, (byte) 0x74, (byte) 0x3c, (byte) 0x2a, (byte) 0x39, (byte) 0xaf, (byte) 0xee, (byte) 0xda, (byte) 0x5e, (byte) 0xc9, (byte) 0x45, (byte) 0xad};
        String MSG1 = "HelloHypeWorld";
        byte DEST_ID2[] = new byte[] {(byte) 0xe7, (byte) 0x79, (byte) 0x34, (byte) 0x6c, (byte) 0x66, (byte) 0x9c, (byte) 0x17, (byte) 0xf4, (byte) 0x34, (byte) 0xc8, (byte) 0xce, (byte) 0x0e};
        byte SERVICE_KEY2[] = new byte[] {(byte) 0xf6, (byte) 0xcb, (byte) 0x6d, (byte) 0x9d, (byte) 0xb0, (byte) 0x98, (byte) 0x91, (byte) 0x9b, (byte) 0x2d, (byte) 0x39, (byte) 0x55, (byte) 0x11, (byte) 0x41, (byte) 0xc5, (byte) 0xcb, (byte) 0xe7, (byte) 0x67, (byte) 0xb5, (byte) 0x06, (byte) 0xd6};
        String MSG2 = "HypePubSubApp";

        packet = protocol.sendPublishMsg(SERVICE_KEY1, DEST_ID1, MSG1);
        assertEquals(packet[0], (byte) Protocol.MessageType.PUBLISH.ordinal());
        offset = Protocol.MESSAGE_TYPE_BYTE_SIZE;
        assertArrayEquals(Arrays.copyOfRange(packet, offset, offset+Constants.SHA1_BYTE_SIZE), SERVICE_KEY1);
        offset += Constants.SHA1_BYTE_SIZE;
        assertArrayEquals(Arrays.copyOfRange(packet, offset, packet.length), MSG1.getBytes());

        packet = protocol.sendPublishMsg(SERVICE_KEY2, DEST_ID2, MSG2);
        assertEquals(packet[0], (byte) Protocol.MessageType.PUBLISH.ordinal());
        offset = Protocol.MESSAGE_TYPE_BYTE_SIZE;
        assertArrayEquals(Arrays.copyOfRange(packet, offset, offset+Constants.SHA1_BYTE_SIZE), SERVICE_KEY2);
        offset += Constants.SHA1_BYTE_SIZE;
        assertArrayEquals(Arrays.copyOfRange(packet, offset, packet.length), MSG2.getBytes());
    }

    void testSendingInfo() throws IOException
    {
        Protocol protocol = Protocol.getInstance();

        byte packet[];
        int offset;
        byte DEST_ID1[] = new byte[] {(byte) 0x85, (byte) 0xa9, (byte) 0xd4, (byte) 0xc4, (byte) 0xde, (byte) 0xd2, (byte) 0x87, (byte) 0x75, (byte) 0x0f, (byte) 0xc0, (byte) 0xed, (byte) 0x32};
        byte SERVICE_KEY1[] = new byte[] {(byte) 0x05, (byte) 0xeb, (byte) 0x63, (byte) 0x7c, (byte) 0xbd, (byte) 0x3f, (byte) 0x33, (byte) 0x69, (byte) 0x1d, (byte) 0x74, (byte) 0x3c, (byte) 0x2a, (byte) 0x39, (byte) 0xaf, (byte) 0xee, (byte) 0xda, (byte) 0x5e, (byte) 0xc9, (byte) 0x45, (byte) 0xad};
        String MSG1 = "Info-HelloHypeWorld";
        byte DEST_ID2[] = new byte[] {(byte) 0xe7, (byte) 0x79, (byte) 0x34, (byte) 0x6c, (byte) 0x66, (byte) 0x9c, (byte) 0x17, (byte) 0xf4, (byte) 0x34, (byte) 0xc8, (byte) 0xce, (byte) 0x0e};
        byte SERVICE_KEY2[] = new byte[] {(byte) 0xf6, (byte) 0xcb, (byte) 0x6d, (byte) 0x9d, (byte) 0xb0, (byte) 0x98, (byte) 0x91, (byte) 0x9b, (byte) 0x2d, (byte) 0x39, (byte) 0x55, (byte) 0x11, (byte) 0x41, (byte) 0xc5, (byte) 0xcb, (byte) 0xe7, (byte) 0x67, (byte) 0xb5, (byte) 0x06, (byte) 0xd6};
        String MSG2 = "Info-HypePubSubApp";

        packet = protocol.sendInfoMsg(SERVICE_KEY1, DEST_ID1, MSG1);
        assertEquals(packet[0], (byte) Protocol.MessageType.INFO.ordinal());
        offset = Protocol.MESSAGE_TYPE_BYTE_SIZE;
        assertArrayEquals(Arrays.copyOfRange(packet, offset, offset+Constants.SHA1_BYTE_SIZE), SERVICE_KEY1);
        offset += Constants.SHA1_BYTE_SIZE;
        assertArrayEquals(Arrays.copyOfRange(packet, offset, packet.length), MSG1.getBytes());

        packet = protocol.sendInfoMsg(SERVICE_KEY2, DEST_ID2, MSG2);
        assertEquals(packet[0], (byte) Protocol.MessageType.INFO.ordinal());
        offset = Protocol.MESSAGE_TYPE_BYTE_SIZE;
        assertArrayEquals(Arrays.copyOfRange(packet, offset, offset+Constants.SHA1_BYTE_SIZE), SERVICE_KEY2);
        offset += Constants.SHA1_BYTE_SIZE;
        assertArrayEquals(Arrays.copyOfRange(packet, offset, packet.length), MSG2.getBytes());
    }

    void testGetMessageType() throws IOException
    {
        Protocol protocol = Protocol.getInstance();

        byte packet[];
        byte SERVICE_KEY[] = new byte[] {(byte) 0x9a, (byte) 0xc1, (byte) 0xb0, (byte) 0x41, (byte) 0x5e, (byte) 0x0a, (byte) 0x97, (byte) 0x73, (byte) 0x8c, (byte) 0x57, (byte) 0xe7, (byte) 0xe6, (byte) 0x3f, (byte) 0x68, (byte) 0x50, (byte) 0xab, (byte) 0x21, (byte) 0xe4, (byte) 0x7e, (byte) 0xb4};
        String MSG = "HelloHypeWorld";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) Protocol.MessageType.SUBSCRIBE_SERVICE.ordinal());
        outputStream.write(SERVICE_KEY);
        packet = outputStream.toByteArray();
        assertEquals(protocol.getMessageType(packet), Protocol.MessageType.SUBSCRIBE_SERVICE);

        outputStream.reset();
        outputStream.write((byte) Protocol.MessageType.UNSUBSCRIBE_SERVICE.ordinal());
        outputStream.write(SERVICE_KEY);
        packet = outputStream.toByteArray();
        assertEquals(protocol.getMessageType(packet), Protocol.MessageType.UNSUBSCRIBE_SERVICE);

        outputStream.reset();
        outputStream.write((byte) Protocol.MessageType.PUBLISH.ordinal());
        outputStream.write(SERVICE_KEY);
        outputStream.write(MSG.getBytes());
        packet = outputStream.toByteArray();
        assertEquals(protocol.getMessageType(packet), Protocol.MessageType.PUBLISH);

        outputStream.reset();
        outputStream.write((byte) Protocol.MessageType.INFO.ordinal());
        outputStream.write(SERVICE_KEY);
        outputStream.write(MSG.getBytes());
        packet = outputStream.toByteArray();
        assertEquals(protocol.getMessageType(packet), Protocol.MessageType.INFO);

        outputStream.reset();
        outputStream.write((byte) 0xFF);
        outputStream.write(SERVICE_KEY);
        packet = outputStream.toByteArray();
        assertEquals(protocol.getMessageType(packet), Protocol.MessageType.INVALID);
    }
}
