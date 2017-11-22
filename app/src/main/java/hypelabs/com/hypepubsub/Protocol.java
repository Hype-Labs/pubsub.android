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
    private static final String TAG = Protocol.class.getName();

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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.SUBSCRIBE_SERVICE.ordinal());
        outputStream.write(serviceKey);
        byte packet[] = outputStream.toByteArray();

        Log.i(TAG, "Sending Subscribe message to 0x"
                         + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier())
                         + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        Hype.send(packet, destInstance);
        return packet; // TODO: Remove return in the future;
    }

    static byte[] sendUnsubscribeMsg(byte serviceKey[], Instance destInstance) throws IOException, NoSuchAlgorithmException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.UNSUBSCRIBE_SERVICE.ordinal());
        outputStream.write(serviceKey);
        byte packet[] = outputStream.toByteArray();

        Log.i(TAG, "Sending Unsubscribe message to 0x"
                + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));
        Hype.send(packet, destInstance);
        return packet; // TODO: Remove return in the future;
    }

    static byte[] sendPublishMsg(byte serviceKey[], Instance destInstance, String msg) throws IOException, NoSuchAlgorithmException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.PUBLISH.ordinal());
        outputStream.write(serviceKey);
        outputStream.write(msg.getBytes(Constants.HPB_ENCODING_STANDARD));
        byte packet[] = outputStream.toByteArray();

        Log.i(TAG, "Sending Publish message to 0x"
                + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". Message: " + msg);

        Hype.send(packet, destInstance);
        return packet; // TODO: Remove return in the future;
    }

    static byte[] sendInfoMsg(byte serviceKey[], Instance destInstance, String msg) throws IOException, NoSuchAlgorithmException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.INFO.ordinal());
        outputStream.write(serviceKey);
        outputStream.write(msg.getBytes(Constants.HPB_ENCODING_STANDARD));
        byte packet[] = outputStream.toByteArray();

        Log.i(TAG, "Sending Info message to 0x"
                + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". Message: " + msg);

        Hype.send(packet, destInstance);
        return packet;// TODO: Remove return in the future;
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

    static int receiveSubscribeMsg(Instance originInstance, byte msg[]) throws NoSuchAlgorithmException
    {
        if(msg.length != (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, "Received Subscribe message with an invalid length");
            return -1;
        }

        byte serviceKey[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE,msg.length-1);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processSubscribeReq(serviceKey, originInstance);

        Log.i(TAG, "Receive Subscribe message from 0x"
                + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        return 0;
    }

    static int receiveUnsubscribeMsg(Instance originInstance, byte msg[]) throws NoSuchAlgorithmException
    {
        if(msg.length != (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, "Received Unsubscribe message with an invalid length");
            return -1;
        }

        byte serviceKey[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE,msg.length-1);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processUnsubscribeReq(serviceKey, originInstance);

        Log.i(TAG, "Receive Unsubscribe message from 0x"
                + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        return 0;
    }

    static int receivePublishMsg(Instance originInstance, byte msg[]) throws IOException, NoSuchAlgorithmException
    {
        if(msg.length <= (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, "Received Publish message with an invalid length");
            return -1;
        }

        byte serviceKey[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE, MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE-1);
        byte publishedData[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE, msg.length-1);
        String publishedStr = new String(publishedData, Constants.HPB_ENCODING_STANDARD);

        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processPublishReq(serviceKey, publishedStr);

        Log.i(TAG, "Received Publish message from 0x"
                + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". Message: " + publishedStr);

        return 0;
    }

    static int receiveInfoMsg(Instance originInstance, byte msg[]) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        if(msg.length <= (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, "Received Info message with an invalid length");
            return -1;
        }

        byte serviceKey[] = Arrays.copyOfRange(msg, MESSAGE_TYPE_BYTE_SIZE, MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE-1);
        byte infoData[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE, msg.length-1);
        String infoStr = new String(infoData, Constants.HPB_ENCODING_STANDARD);

        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processInfoMsg(serviceKey, infoStr);

        Log.i(TAG, "Received Info message from 0x"
                + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier())
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". Message: " + infoStr);

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
}
