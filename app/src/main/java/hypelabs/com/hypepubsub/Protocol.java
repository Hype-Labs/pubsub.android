package hypelabs.com.hypepubsub;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.hypelabs.hype.Error;
import com.hypelabs.hype.Hype;
import com.hypelabs.hype.Instance;
import com.hypelabs.hype.Message;
import com.hypelabs.hype.MessageInfo;
import com.hypelabs.hype.MessageObserver;

public class Protocol
{
    //public final static String TAG = this.getClass().getName();

    public static final int MESSAGE_TYPE_BYTE_SIZE = 1;

    public enum MessageType {
        SUBSCRIBE_SERVICE, /**< Represents a packet which contains a subscribe message */
        UNSUBSCRIBE_SERVICE, /**< Represents a packet which contains a unsubscribe message */
        PUBLISH, /**< Represents a packet which contains a publish message */
        INFO, /**< Represents a packet which contains a info message */
        INVALID /**< Represents a invalid packet */
    }

    static byte[] sendSubscribeMsg(byte serviceKey[], Instance destInstance) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.SUBSCRIBE_SERVICE.ordinal());
        outputStream.write(serviceKey);
        byte packet[] = outputStream.toByteArray();

        Hype.send(packet, destInstance);
        return packet;
    }

    static byte[] sendUnsubscribeMsg(byte serviceKey[], Instance destInstance) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.UNSUBSCRIBE_SERVICE.ordinal());
        outputStream.write(serviceKey);
        byte packet[] = outputStream.toByteArray();

        Hype.send(packet, destInstance);
        return packet;
    }

    static byte[] sendPublishMsg(byte serviceKey[], Instance destInstance, String msg) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.PUBLISH.ordinal());
        outputStream.write(serviceKey);
        outputStream.write(msg.getBytes());
        byte packet[] = outputStream.toByteArray();

        Hype.send(packet, destInstance);
        return packet;
    }

    static byte[] sendInfoMsg(byte serviceKey[], Instance destInstance, String msg) throws IOException, NoSuchAlgorithmException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.INFO.ordinal());
        outputStream.write(serviceKey);
        outputStream.write(msg.getBytes());
        byte packet[] = outputStream.toByteArray();

        Hype.send(packet, destInstance);
        return packet;
    }

    static int receiveMsg(Instance originInstance, byte msg[]) throws IOException, NoSuchAlgorithmException {
        if(msg.length <= 0) {
            //Log.e(TAG, "Received message has invalid length");
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
                receiveInfoMsg(msg);
                break;
            case INVALID:
                return -1; // Message type not recognized. Discard
        }

        return 0;
    }

    static int receiveSubscribeMsg(Instance originInstance, byte msg[]) throws NoSuchAlgorithmException {
        if(msg.length != (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
            return -1;

        byte serviceKey[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE,msg.length-1);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processSubscribeReq(serviceKey, originInstance);
        return 0;
    }

    static int receiveUnsubscribeMsg(Instance originInstance, byte msg[]) throws NoSuchAlgorithmException {
        if(msg.length != (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
            return -1;

        byte serviceKey[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE,msg.length-1);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processUnsubscribeReq(serviceKey, originInstance);
        return 0;
    }

    static int receivePublishMsg(Instance originInstance, byte msg[]) throws IOException, NoSuchAlgorithmException {
        if(msg.length <= (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
            return -1;

        byte serviceKey[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE, MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE-1);
        byte msg_data[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE, msg.length-1);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processPublishReq(serviceKey, msg_data.toString());
        return 0;
    }

    static int receiveInfoMsg(byte msg[]) throws NoSuchAlgorithmException
    {
        if(msg.length <= (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
            return -1;

        byte serviceKey[] = Arrays.copyOfRange(msg, MESSAGE_TYPE_BYTE_SIZE, MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE-1);
        byte msg_data[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE, msg.length-1);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processInfoMsg(serviceKey, msg_data.toString());
        return 0;
    }

    static MessageType getMessageType(byte msg[])
    {
        if(msg.length <= 0)
            return MessageType.INVALID;

        if(msg[0] == ((byte) MessageType.SUBSCRIBE_SERVICE.ordinal()))
            return MessageType.SUBSCRIBE_SERVICE;
        else if(msg[0] == ((byte) MessageType.UNSUBSCRIBE_SERVICE.ordinal()))
            return MessageType.UNSUBSCRIBE_SERVICE;
        else if(msg[0] == ((byte) MessageType.PUBLISH.ordinal()))
            return MessageType.PUBLISH;
        else if(msg[0] == ((byte) MessageType.INFO.ordinal()))
            return MessageType.INFO;

        return MessageType.INVALID;
    }
}
