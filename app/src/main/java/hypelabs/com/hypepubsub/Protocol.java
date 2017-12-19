package hypelabs.com.hypepubsub;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.hypelabs.hype.Instance;

public class Protocol
{
    public static final int MESSAGE_TYPE_BYTE_SIZE = 1;

    private static final String TAG = Protocol.class.getName();
    private static final String PROTOCOL_LOG_PREFIX = HpsConstants.LOG_PREFIX + "<Protocol> ";

    //////////////////////////////////////////////////////////////////////////////
    // Message Sending Processing Methods
    //////////////////////////////////////////////////////////////////////////////

    static byte[] sendSubscribeMsg(byte serviceKey[], Instance destInstance) {
        HpsMessage hpsMsg = new HpsMessage(HpsMessageType.SUBSCRIBE_SERVICE, serviceKey);
        printMsgSendLog(hpsMsg, destInstance);
        HypeSdkInterface.getInstance().sendMsg(hpsMsg, destInstance);
        return hpsMsg.toByteArray();
    }

    static byte[] sendUnsubscribeMsg(byte serviceKey[], Instance destInstance) {
        HpsMessage hpsMsg = new HpsMessage(HpsMessageType.UNSUBSCRIBE_SERVICE, serviceKey);
        printMsgSendLog(hpsMsg, destInstance);
        HypeSdkInterface.getInstance().sendMsg(hpsMsg, destInstance);
        return hpsMsg.toByteArray();
    }

    static byte[] sendPublishMsg(byte serviceKey[], Instance destInstance, String info) {
        HpsMessage hpsMsg = new HpsMessage(HpsMessageType.PUBLISH, serviceKey, info);
        printMsgSendLog(hpsMsg, destInstance);
        HypeSdkInterface.getInstance().sendMsg(hpsMsg, destInstance);
        return hpsMsg.toByteArray();
    }

    static byte[] sendInfoMsg(byte serviceKey[], Instance destInstance, String info) {
        HpsMessage hpsMsg = new HpsMessage(HpsMessageType.INFO, serviceKey, info);
        printMsgSendLog(hpsMsg, destInstance);
        HypeSdkInterface.getInstance().sendMsg(hpsMsg, destInstance);
        return hpsMsg.toByteArray();
    }

    //////////////////////////////////////////////////////////////////////////////
    // Received Message Processing Methods
    //////////////////////////////////////////////////////////////////////////////

    static int receiveMsg(Instance originInstance, byte packet[]) {
        if(packet.length <= 0) {
            Log.e(TAG, String.format("%s Received message has an invalid length", PROTOCOL_LOG_PREFIX));
            return -1;
        }

        switch (extractHpsMessageTypeFromReceivedPacket(packet)) {
            case SUBSCRIBE_SERVICE:
                receiveSubscribeMsg(originInstance, packet);
                break;
            case UNSUBSCRIBE_SERVICE:
                receiveUnsubscribeMsg(originInstance, packet);
                break;
            case PUBLISH:
                receivePublishMsg(originInstance, packet);
                break;
            case INFO:
                receiveInfoMsg(originInstance, packet);
                break;
            case INVALID:
                Log.e(TAG, String.format("%s Received message has an invalid MessageType", PROTOCOL_LOG_PREFIX));
                return -2; // Discard
        }

        return 0;
    }

    private static int receiveSubscribeMsg(Instance originInstance, byte packet[]) {
        if(packet.length != (MESSAGE_TYPE_BYTE_SIZE + HpsConstants.HASH_ALGORITHM_DIGEST_LENGTH)) {
            Log.e(TAG, String.format("%s Received Subscribe message with an invalid length", PROTOCOL_LOG_PREFIX));
            return -1;
        }

        HpsMessage hpsMsg = new HpsMessage(HpsMessageType.SUBSCRIBE_SERVICE, extractServiceKeyFromReceivedPacket(packet));
        printMsgReceivedLog(hpsMsg, originInstance);
        HypePubSub hps = HypePubSub.getInstance();
        hps.processSubscribeReq(hpsMsg.getServiceKey(), originInstance);
        return 0;
    }

    private static int receiveUnsubscribeMsg(Instance originInstance, byte packet[]) {
        if(packet.length != (MESSAGE_TYPE_BYTE_SIZE + HpsConstants.HASH_ALGORITHM_DIGEST_LENGTH)) {
            Log.e(TAG, String.format("%s Received Unsubscribe message with an invalid length", PROTOCOL_LOG_PREFIX));
            return -1;
        }

        HpsMessage hpsMsg = new HpsMessage(HpsMessageType.UNSUBSCRIBE_SERVICE, extractServiceKeyFromReceivedPacket(packet));
        printMsgReceivedLog(hpsMsg, originInstance);
        HypePubSub hps = HypePubSub.getInstance();
        hps.processUnsubscribeReq(hpsMsg.getServiceKey(), originInstance);
        return 0;
    }

    private static int receivePublishMsg(Instance originInstance, byte packet[]) {
        if(packet.length <= (MESSAGE_TYPE_BYTE_SIZE + HpsConstants.HASH_ALGORITHM_DIGEST_LENGTH)) {
            Log.e(TAG, String.format("%s Received Publish message with an invalid length", PROTOCOL_LOG_PREFIX));
            return -1;
        }

        HpsMessage hpsMsg = new HpsMessage(HpsMessageType.PUBLISH, extractServiceKeyFromReceivedPacket(packet),
                HpsGenericUtils.byteArrayToString(extractInfoFromReceivedPacket(packet)));
        printMsgReceivedLog(hpsMsg, originInstance);
        HypePubSub hps = HypePubSub.getInstance();
        hps.processPublishReq(hpsMsg.getServiceKey(), hpsMsg.getInfo());
        return 0;
    }

    private static int receiveInfoMsg(Instance originInstance, byte packet[]) {
        if(packet.length <= (MESSAGE_TYPE_BYTE_SIZE + HpsConstants.HASH_ALGORITHM_DIGEST_LENGTH)) {
            Log.e(TAG, String.format("%s Received Info message with an invalid length", PROTOCOL_LOG_PREFIX));
            return -1;
        }

        HpsMessage hpsMsg = new HpsMessage(HpsMessageType.INFO, extractServiceKeyFromReceivedPacket(packet),
                HpsGenericUtils.byteArrayToString(extractInfoFromReceivedPacket(packet)));
        printMsgReceivedLog(hpsMsg, originInstance);
        HypePubSub hps = HypePubSub.getInstance();
        hps.processInfoMsg(hpsMsg.getServiceKey(), hpsMsg.getInfo());
        return 0;
    }

    //////////////////////////////////////////////////////////////////////////////
    // Packet Data Extraction Methods
    //////////////////////////////////////////////////////////////////////////////

    public static HpsMessageType extractHpsMessageTypeFromReceivedPacket(byte packet[]) {
        if(packet.length <= 0) {
            return HpsMessageType.INVALID;
        }

        if(packet[0] == ((byte) HpsMessageType.SUBSCRIBE_SERVICE.ordinal())) {
            return HpsMessageType.SUBSCRIBE_SERVICE;
        }
        else if(packet[0] == ((byte) HpsMessageType.UNSUBSCRIBE_SERVICE.ordinal())) {
            return HpsMessageType.UNSUBSCRIBE_SERVICE;
        }
        else if(packet[0] == ((byte) HpsMessageType.PUBLISH.ordinal())) {
            return HpsMessageType.PUBLISH;
        }
        else if(packet[0] == ((byte) HpsMessageType.INFO.ordinal())) {
            return HpsMessageType.INFO;
        }

        return HpsMessageType.INVALID;
    }

    private static byte[] extractServiceKeyFromReceivedPacket(byte packet[]) {
        return Arrays.copyOfRange(packet, MESSAGE_TYPE_BYTE_SIZE,
                MESSAGE_TYPE_BYTE_SIZE + HpsConstants.HASH_ALGORITHM_DIGEST_LENGTH);
    }

    private static byte[] extractInfoFromReceivedPacket(byte packet[]) {
        return Arrays.copyOfRange(packet,MESSAGE_TYPE_BYTE_SIZE + HpsConstants.HASH_ALGORITHM_DIGEST_LENGTH,
                packet.length);
    }

    //////////////////////////////////////////////////////////////////////////////
    // Logging Methods
    //////////////////////////////////////////////////////////////////////////////

    static void printMsgSendLog(HpsMessage hpsMsg, Instance destination) {
        Log.i(TAG, String.format("%s Sending %s Destination %s",
                PROTOCOL_LOG_PREFIX,
                hpsMsg.toLogString(),
                HpsGenericUtils.getLogStrFromInstance(destination)));
    }

    static void printMsgReceivedLog(HpsMessage hpsMsg, Instance originator) {
        Log.i(TAG, String.format("%s Received %s Originator %s",
                PROTOCOL_LOG_PREFIX,
                hpsMsg.toLogString(),
                HpsGenericUtils.getLogStrFromInstance(originator)));
    }
}
