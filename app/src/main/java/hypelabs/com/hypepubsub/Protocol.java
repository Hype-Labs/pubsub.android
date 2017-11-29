package hypelabs.com.hypepubsub;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.hypelabs.hype.Instance;

public class Protocol
{
    private static final String TAG = Protocol.class.getName();

    public static final int MESSAGE_TYPE_BYTE_SIZE = 1;

    public enum MessageType {
        SUBSCRIBE_SERVICE,
        UNSUBSCRIBE_SERVICE,
        PUBLISH,
        INFO,
        INVALID
    }

    static byte[] sendSubscribeMsg(byte serviceKey[], Instance destInstance) throws IOException
    {
        Log.i(TAG, Constants.HPB_LOG_MSG_PREFIX + "Sending Subscribe message to " + GenericUtils.getInstanceAnnouncementStr(destInstance)
                         + " (0x" + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier()) + ")"
                         + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        HpbMessage hpbMsg = new HpbMessage(MessageType.SUBSCRIBE_SERVICE, serviceKey, null);

        HypeSdkInterface.getInstance().sendMsg(hpbMsg, destInstance);
        return hpbMsg.toByteArray(); // TODO: Remove return in the future;
    }

    static byte[] sendUnsubscribeMsg(byte serviceKey[], Instance destInstance) throws IOException
    {
        Log.i(TAG, Constants.HPB_LOG_MSG_PREFIX + "Sending Unsubscribe message to " + GenericUtils.getInstanceAnnouncementStr(destInstance)
                + " (0x" + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier()) + ")"
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        HpbMessage hpbMsg = new HpbMessage(MessageType.UNSUBSCRIBE_SERVICE, serviceKey, null);
        HypeSdkInterface.getInstance().sendMsg(hpbMsg, destInstance);
        return hpbMsg.toByteArray(); // TODO: Remove return in the future;
    }

    static byte[] sendPublishMsg(byte serviceKey[], Instance destInstance, String msg) throws IOException
    {
        Log.i(TAG, Constants.HPB_LOG_MSG_PREFIX + "Sending Publish message to " + GenericUtils.getInstanceAnnouncementStr(destInstance)
                + " (0x" + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier()) + ")"
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". HpbMessage: " + msg);

        HpbMessage hpbMsg = new HpbMessage(MessageType.PUBLISH, serviceKey, msg);
        HypeSdkInterface.getInstance().sendMsg(hpbMsg, destInstance);
        return hpbMsg.toByteArray(); // TODO: Remove return in the future;
    }

    static byte[] sendInfoMsg(byte serviceKey[], Instance destInstance, String msg) throws IOException
    {
        Log.i(TAG, Constants.HPB_LOG_MSG_PREFIX + "Sending Info message to " + GenericUtils.getInstanceAnnouncementStr(destInstance)
                + " (0x" + BinaryUtils.byteArrayToHexString(destInstance.getIdentifier()) + ")"
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". HpbMessage: " + msg);

        HpbMessage hpbMsg = new HpbMessage(MessageType.INFO, serviceKey, msg);
        HypeSdkInterface.getInstance().sendMsg(hpbMsg, destInstance);
        return hpbMsg.toByteArray(); // TODO: Remove return in the future;
    }

    static int receiveMsg(Instance originInstance, byte msg[]) throws IOException, NoSuchAlgorithmException
    {
        if(msg.length <= 0)
        {
            Log.e(TAG, Constants.HPB_LOG_MSG_PREFIX + "Received message has an invalid length");
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
                Log.e(TAG, Constants.HPB_LOG_MSG_PREFIX + "Received message has an invalid MessageType");
                return -1; // HpbMessage type not recognized. Discard
        }

        return 0;
    }

    private static int receiveSubscribeMsg(Instance originInstance, byte msg[]) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        if(msg.length != (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, Constants.HPB_LOG_MSG_PREFIX + "Received Subscribe message with an invalid length");
            return -1;
        }

        byte serviceKey[] = getServiceKey(msg);

        Log.i(TAG, Constants.HPB_LOG_MSG_PREFIX + "Received Subscribe message from " + GenericUtils.getInstanceAnnouncementStr(originInstance)
                + " (0x" + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier()) + ")"
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processSubscribeReq(serviceKey, originInstance);
        return 0;
    }

    private static int receiveUnsubscribeMsg(Instance originInstance, byte msg[]) throws UnsupportedEncodingException
    {
        if(msg.length != (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, Constants.HPB_LOG_MSG_PREFIX + "Received Unsubscribe message with an invalid length");
            return -1;
        }

        byte serviceKey[] = getServiceKey(msg);

        Log.i(TAG, Constants.HPB_LOG_MSG_PREFIX + "Received Unsubscribe message from " + GenericUtils.getInstanceAnnouncementStr(originInstance)
                + " (0x" + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier()) + ")"
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey));

        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processUnsubscribeReq(serviceKey, originInstance);
        return 0;
    }

    private static int receivePublishMsg(Instance originInstance, byte msg[]) throws IOException
    {
        if(msg.length <= (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, Constants.HPB_LOG_MSG_PREFIX + "Received Publish message with an invalid length");
            return -1;
        }

        byte serviceKey[] = getServiceKey(msg);
        byte publishedData[] = getInfo(msg);
        String publishedStr = new String(publishedData, Constants.HPB_ENCODING_STANDARD);

        Log.i(TAG, Constants.HPB_LOG_MSG_PREFIX + "Received Publish message from " + GenericUtils.getInstanceAnnouncementStr(originInstance)
                + " (0x" + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier()) + ")"
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". HpbMessage: " + publishedStr);

        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processPublishReq(serviceKey, publishedStr);
        return 0;
    }

    private static int receiveInfoMsg(Instance originInstance, byte msg[]) throws UnsupportedEncodingException
    {
        if(msg.length <= (MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE))
        {
            Log.e(TAG, Constants.HPB_LOG_MSG_PREFIX + "Received Info message with an invalid length");
            return -1;
        }

        byte serviceKey[] = getServiceKey(msg);
        byte infoData[] = getInfo(msg);
        String infoStr = new String(infoData, Constants.HPB_ENCODING_STANDARD);

        Log.i(TAG, Constants.HPB_LOG_MSG_PREFIX + "Received Info message from " + GenericUtils.getInstanceAnnouncementStr(originInstance)
                + " (0x" + BinaryUtils.byteArrayToHexString(originInstance.getIdentifier()) + ")"
                + " for service 0x" + BinaryUtils.byteArrayToHexString(serviceKey)
                + ". HpbMessage: " + infoStr);

        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processInfoMsg(serviceKey, infoStr);
        return 0;
    }

    public static MessageType getMessageType(byte msg[])
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

    private static byte[] getServiceKey(byte msg[])
    {
        return Arrays.copyOfRange(msg, MESSAGE_TYPE_BYTE_SIZE,
                                    MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE);
    }

    private static byte[] getInfo(byte msg[])
    {
        return Arrays.copyOfRange(msg,MESSAGE_TYPE_BYTE_SIZE + Constants.SHA1_BYTE_SIZE,
                                    msg.length);
    }
}
