package hypelabs.com.hypepubsub;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;

public class Protocol
{
    private static final String TAG = Constants.GLOBAL_TAG_PREFIX + Protocol.class.getName();

    public static final int MESSAGE_TYPE_BYTE_SIZE = 1;

    public enum MessageType {
        SUBSCRIBE_SERVICE, /**< Represents a packet which contains a subscribe message */
        UNSUBSCRIBE_SERVICE, /**< Represents a packet which contains a unsubscribe message */
        PUBLISH, /**< Represents a packet which contains a publish message */
        INFO, /**< Represents a packet which contains a info message */
        INVALID /**< Represents a invalid packet */
    }

    static byte[] sendSubscribeMsg(byte serviceKey[], Instance destInstance) throws IOException, NoSuchAlgorithmException
    {
        Log.i(TAG, "Sending Subscribe message to 0x"
                         + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier())
                         + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        byte packet[] = buildPacket(MessageType.SUBSCRIBE_SERVICE, serviceKey, null);
        Hype.send(packet, destInstance);
        return packet; // TODO: Remove return in the future;
    }

    static byte[] sendUnsubscribeMsg(byte serviceKey[], Instance destInstance) throws IOException, NoSuchAlgorithmException
    {
        Log.i(TAG, "Sending Unsubscribe message to 0x"
                + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        byte packet[] = buildPacket(MessageType.UNSUBSCRIBE_SERVICE, serviceKey, null);
        Hype.send(packet, destInstance);
        return packet; // TODO: Remove return in the future;
    }

    static byte[] sendPublishMsg(byte serviceKey[], Instance destInstance, String msg) throws IOException, NoSuchAlgorithmException
    {
        Log.i(TAG, "Sending Publish message to 0x"
                + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". Message: " + msg);

        byte packet[] = buildPacket(MessageType.PUBLISH, serviceKey, msg);
        Hype.send(packet, destInstance);
        return packet; // TODO: Remove return in the future;
    }

    static byte[] sendInfoMsg(byte serviceKey[], Instance destInstance, String msg) throws IOException, NoSuchAlgorithmException
    {
        Log.i(TAG, "Sending Info message to 0x"
                + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". Message: " + msg);

        byte packet[] = buildPacket(MessageType.INFO, serviceKey, msg);
        Hype.send(packet, destInstance);
        return packet;// TODO: Remove return in the future;
    }

    static byte[] buildPacket(MessageType type, byte[] serviceKey, String infoMsg) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) type.ordinal());
        outputStream.write(serviceKey);
        if(infoMsg != null) {
            outputStream.write(infoMsg.getBytes(Constants.HPB_ENCODING_STANDARD));
        }
        return outputStream.toByteArray();
    }

    static int receiveMsg(Instance originInstance, byte msg[]) throws IOException, NoSuchAlgorithmException
    {
        if(msg.length <= 0)
        {
            Log.e(TAG, "Received message has an invalid length");
            return -1;
        }

        MessageType m_type = getMessageType(msg);

        switch (m_type)
        {
            case SUBSCRIBE_SERVICE:
                receiveSubscribeMsg(originInstance, msg);
                break;
            case UNSUBSCRIBE_SERVICE:
                receiveUnsubscribeMsg(originInstance, msg);
                break;
            case PUBLISH:
                receivePublishMsg(originInstance, msg);
                break;
            case INFO:
                receiveInfoMsg(originInstance, msg);
                break;
            case INVALID:
                Log.e(TAG, "Received message has an invalid MessageType");
                return -1; // Message type not recognized. Discard
        }

        return 0;
    }

    static int receiveSubscribeMsg(Instance originInstance, byte msg[]) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        if(msg.length != (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, "Received Subscribe message with an invalid length");
            return -1;
        }

        byte serviceKey[] = getServiceKey(msg);

        Log.i(TAG, "Received Subscribe message from 0x"
                + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processSubscribeReq(serviceKey, originInstance);
        return 0;
    }

    static int receiveUnsubscribeMsg(Instance originInstance, byte msg[]) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        if(msg.length != (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, "Received Unsubscribe message with an invalid length");
            return -1;
        }

        byte serviceKey[] = getServiceKey(msg);

        Log.i(TAG, "Received Unsubscribe message from 0x"
                + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processUnsubscribeReq(serviceKey, originInstance);
        return 0;
    }

    static int receivePublishMsg(Instance originInstance, byte msg[]) throws IOException, NoSuchAlgorithmException
    {
        if(msg.length <= (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, "Received Publish message with an invalid length");
            return -1;
        }

        byte serviceKey[] = getServiceKey(msg);
        byte publishedData[] = getInfo(msg);
        String publishedStr = new String(publishedData, Constants.HPB_ENCODING_STANDARD);

        Log.i(TAG, "Received Publish message from 0x"
                + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". Message: " + publishedStr);

        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processPublishReq(serviceKey, publishedStr);
        return 0;
    }

    static int receiveInfoMsg(Instance originInstance, byte msg[]) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        if(msg.length <= (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, "Received Info message with an invalid length");
            return -1;
        }

        byte serviceKey[] = getServiceKey(msg);
        byte infoData[] = getInfo(msg);
        String infoStr = new String(infoData, Constants.HPB_ENCODING_STANDARD);

        Log.i(TAG, "Received Info message from 0x"
                + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". Message: " + infoStr);

        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processInfoMsg(serviceKey, infoStr);
        return 0;
    }

    static MessageType getMessageType(byte msg[])
    {
        if(msg.length <= 0) {
            return MessageType.INVALID;
        }

        if(msg[0] == ((byte) MessageType.SUBSCRIBE_SERVICE.ordinal())) {
            return MessageType.SUBSCRIBE_SERVICE;
        }
        else if(msg[0] == ((byte) MessageType.UNSUBSCRIBE_SERVICE.ordinal())) {
            return MessageType.UNSUBSCRIBE_SERVICE;
        }
        else if(msg[0] == ((byte) MessageType.PUBLISH.ordinal())) {
            return MessageType.PUBLISH;
        }
        else if(msg[0] == ((byte) MessageType.INFO.ordinal())) {
            return MessageType.INFO;
        }

        return MessageType.INVALID;
    }

    static byte[] getServiceKey(byte msg[])
    {
        return Arrays.copyOfRange(msg, MESSAGE_TYPE_BYTE_SIZE,
                                    MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE);
    }

    static byte[] getInfo(byte msg[])
    {
        return Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE,
                                    msg.length);
    }
}
