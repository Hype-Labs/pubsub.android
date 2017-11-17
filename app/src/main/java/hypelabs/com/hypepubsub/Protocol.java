package hypelabs.com.hypepubsub;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static hypelabs.com.hypepubsub.Protocol.MessageType.SUBSCRIBE_SERVICE;

/**
 * Created by xavier on 15/11/2017.
 */

public class Protocol {

    public static final int MESSAGE_TYPE_BYTE_SIZE = 1;

    static Protocol protocol = null; // Singleton

    public static Protocol getInstance()
    {
        if (protocol == null) {
            protocol = new Protocol();
        }

        return protocol;
    }

    public enum MessageType {
        SUBSCRIBE_SERVICE, /**< Represents a packet which contains a subscribe message */
        UNSUBSCRIBE_SERVICE, /**< Represents a packet which contains a unsubscribe message */
        PUBLISH, /**< Represents a packet which contains a publish message */
        INFO, /**< Represents a packet which contains a info message */
        INVALID /**< Represents a invalid packet */
    }

    public void sendSubscribeMsg(byte serviceKey[], byte destNetworkId[]) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) SUBSCRIBE_SERVICE.ordinal());
        outputStream.write(serviceKey);
        byte packet[] = outputStream.toByteArray();
    }

    public void sendUnsubscribeMsg(byte serviceKey[], byte destNetworkId[]) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.UNSUBSCRIBE_SERVICE.ordinal());
        outputStream.write(serviceKey);
        byte packet[] = outputStream.toByteArray();
    }

    public void sendPublishMsg(byte serviceKey[], byte destNetworkId[], String msg) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.PUBLISH.ordinal());
        outputStream.write(serviceKey);
        outputStream.write(msg.getBytes());
        byte packet[] = outputStream.toByteArray();
    }

    public void sendInfoMsg(byte serviceKey[], byte destNetworkId[], String msg) throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) MessageType.INFO.ordinal());
        outputStream.write(serviceKey);
        outputStream.write(msg.getBytes());
        byte packet[] = outputStream.toByteArray();
    }

    public int receiveMsg(byte originNetworkId[], byte msg[]) throws IOException, NoSuchAlgorithmException {
        if(msg.length <= 0)
            return -1;

        MessageType m_type = getMessageType(msg);

        switch (m_type)
        {
            case SUBSCRIBE_SERVICE:
                receiveSubscribeMsg(originNetworkId, msg);
                break;
            case UNSUBSCRIBE_SERVICE:
                receiveUnsubscribeMsg(originNetworkId, msg);
                break;
            case PUBLISH:
                receivePublishMsg(originNetworkId, msg);
                break;
            case INFO:
                receiveInfoMsg(msg);
                break;
            case INVALID:
                return -1; // Message type not recognized. Discard
        }

        return 0;
    }

    public int receiveSubscribeMsg(byte originNetworkId[], byte msg[]) throws NoSuchAlgorithmException {
        if(msg.length != (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
            return -1;

        byte serviceKey[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE,msg.length-1);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processSubscribeReq(serviceKey, originNetworkId);
        return 0;
    }

    public int receiveUnsubscribeMsg(byte originNetworkId[], byte msg[]) throws NoSuchAlgorithmException {
        if(msg.length != (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
            return -1;

        byte serviceKey[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE,msg.length-1);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processUnsubscribeReq(serviceKey, originNetworkId);
        return 0;
    }

    public int receivePublishMsg(byte originNetworkId[], byte msg[]) throws IOException, NoSuchAlgorithmException {
        if(msg.length <= (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
            return -1;

        byte serviceKey[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE, MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE-1);
        byte msg_data[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE, msg.length-1);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processPublishReq(serviceKey, msg_data.toString());
        return 0;
    }

    int receiveInfoMsg(byte msg[])
    {
        if(msg.length <= (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
            return -1;

        byte serviceKey[] = Arrays.copyOfRange(msg, MESSAGE_TYPE_BYTE_SIZE, MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE-1);
        byte msg_data[] = Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE+Constants.SHA1_BYTE_SIZE, msg.length-1);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processInfoMsg(serviceKey, msg_data.toString());
        return 0;
    }

    public MessageType getMessageType(byte msg[])
    {
        if(msg.length <= 0)
            return MessageType.INVALID;

        if(msg[0] == ((byte) SUBSCRIBE_SERVICE.ordinal()))
            return SUBSCRIBE_SERVICE;
        else if(msg[0] == ((byte) MessageType.UNSUBSCRIBE_SERVICE.ordinal()))
            return MessageType.UNSUBSCRIBE_SERVICE;
        else if(msg[0] == ((byte) MessageType.PUBLISH.ordinal()))
            return MessageType.PUBLISH;
        else if(msg[0] == ((byte) MessageType.INFO.ordinal()))
            return MessageType.INFO;

        return MessageType.INVALID;
    }

}
