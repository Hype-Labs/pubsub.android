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
    private static final String PROTOCOL_LOG_PREFIX = HpbConstants.LOG_PREFIX + "<Protocol> ";

    //////////////////////////////////////////////////////////////////////////////
    // Message Sending Processing Methods
    //////////////////////////////////////////////////////////////////////////////

    /**
     * This method builds a SubscribeService HpbMessage from a given service key and calls the Hype
     * SDK to send it to the specified instance.
     *
     * @param serviceKey Key of the service to which the message is destined.
     * @param destInstance Hype instance to which the message should be sent.
     * @return Returns the HpbMessage created
     * @throws IOException
     */
    static byte[] sendSubscribeMsg(byte serviceKey[], Instance destInstance) throws IOException
    {
        HpbMessage hpbMsg = new HpbMessage(HpbMessageType.SUBSCRIBE_SERVICE, serviceKey);
        printMsgSendLog(hpbMsg, destInstance);
        HypeSdkInterface.getInstance().sendMsg(hpbMsg, destInstance);
        return hpbMsg.toByteArray();
    }

    /**
     * This method builds a UnsubscribeService HpbMessage from a given service key and calls the Hype
     * SDK to send it to the specified instance.
     *
     * @param serviceKey Key of the service to which the message is destined.
     * @param destInstance Hype instance to which the message should be sent.
     * @return Returns the HpbMessage created
     * @throws IOException
     */
    static byte[] sendUnsubscribeMsg(byte serviceKey[], Instance destInstance) throws IOException
    {
        HpbMessage hpbMsg = new HpbMessage(HpbMessageType.UNSUBSCRIBE_SERVICE, serviceKey);
        printMsgSendLog(hpbMsg, destInstance);
        HypeSdkInterface.getInstance().sendMsg(hpbMsg, destInstance);
        return hpbMsg.toByteArray();
    }

    /**
     * This method builds a Publish HpbMessage from a given service key and info data and calls
     * the Hype SDK to send it to the specified instance.
     *
     * @param serviceKey Key of the service to which the message is destined.
     * @param destInstance Hype instance to which the message should be sent.
     * @param info Info that should be sent in the publish packet.
     * @return Returns the HpbMessage created
     * @throws IOException
     */
    static byte[] sendPublishMsg(byte serviceKey[], Instance destInstance, String info) throws IOException
    {
        HpbMessage hpbMsg = new HpbMessage(HpbMessageType.PUBLISH, serviceKey, info);
        printMsgSendLog(hpbMsg, destInstance);
        HypeSdkInterface.getInstance().sendMsg(hpbMsg, destInstance);
        return hpbMsg.toByteArray();
    }

    /**
     * This method builds a Info HpbMessage from a given service key and info data and calls the
     * Hype SDK to send it to the specified instance.
     *
     * @param serviceKey Key of the service to which the message is destined.
     * @param destInstance Hype instance to which the message should be sent.
     * @param info Info that should be sent in the publish packet.
     * @return Returns the HpbMessage created
     * @throws IOException
     */
    static byte[] sendInfoMsg(byte serviceKey[], Instance destInstance, String info) throws IOException
    {
        HpbMessage hpbMsg = new HpbMessage(HpbMessageType.INFO, serviceKey, info);
        printMsgSendLog(hpbMsg, destInstance);
        HypeSdkInterface.getInstance().sendMsg(hpbMsg, destInstance);
        return hpbMsg.toByteArray();
    }

    //////////////////////////////////////////////////////////////////////////////
    // Received Message Processing Methods
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Analyzes a received message and it calls the appropriate method to process it given its
     * HpbMessageType
     *
     * @param originInstance Hype instance that sent the message
     * @param packet Packet received from the Hype SDK
     * @return Returns 0 in case of sucess and <0 in case of error.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    static int receiveMsg(Instance originInstance, byte packet[]) throws IOException, NoSuchAlgorithmException
    {
        if(packet.length <= 0) {
            Log.e(TAG, PROTOCOL_LOG_PREFIX + "Received message has an invalid length");
            return -1;
        }

        switch (extractHpbMessageTypeFromReceivedPacket(packet))
        {
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
                Log.e(TAG, PROTOCOL_LOG_PREFIX + "Received message has an invalid MessageType");
                return -2; // HpbMessage type not recognized. Discard
        }

        return 0;
    }

    /**
     * Processes a subscribe message and forwards it to {@link HypePubSub#processSubscribeReq(byte[], Instance)}
     *
     * @param originInstance Hype instance that sent the message
     * @param packet Packet received from the Hype SDK
     * @return Returns 0 in case of sucess and <0 in case of error.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static int receiveSubscribeMsg(Instance originInstance, byte packet[]) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        if(packet.length != (MESSAGE_TYPE_BYTE_SIZE + HpbConstants.HASH_ALGORITHM_DIGEST_LENGTH)) {
            Log.e(TAG, PROTOCOL_LOG_PREFIX + "Received Subscribe message with an invalid length");
            return -1;
        }

        HpbMessage hpbMsg = new HpbMessage(HpbMessageType.SUBSCRIBE_SERVICE, extractServiceKeyFromReceivedPacket(packet));
        printMsgReceivedLog(hpbMsg, originInstance);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processSubscribeReq(hpbMsg.getServiceKey(), originInstance);
        return 0;
    }

    /**
     * Processes an unsubscribe message and forwards it to {@link HypePubSub#processUnsubscribeReq(byte[], Instance)}
     *
     * @param originInstance Hype instance that sent the message
     * @param packet Packet received from the Hype SDK
     * @return Returns 0 in case of sucess and <0 in case of error.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static int receiveUnsubscribeMsg(Instance originInstance, byte packet[]) throws UnsupportedEncodingException
    {
        if(packet.length != (MESSAGE_TYPE_BYTE_SIZE + HpbConstants.HASH_ALGORITHM_DIGEST_LENGTH)) {
            Log.e(TAG, PROTOCOL_LOG_PREFIX + "Received Unsubscribe message with an invalid length");
            return -1;
        }

        HpbMessage hpbMsg = new HpbMessage(HpbMessageType.UNSUBSCRIBE_SERVICE, extractServiceKeyFromReceivedPacket(packet));
        printMsgReceivedLog(hpbMsg, originInstance);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processUnsubscribeReq(hpbMsg.getServiceKey(), originInstance);
        return 0;
    }

    /**
     * Processes a publish message and forwards it to {@link HypePubSub#processPublishReq(byte[], String)}
     *
     * @param originInstance Hype instance that sent the message
     * @param packet Packet received from the Hype SDK
     * @return Returns 0 in case of sucess and <0 in case of error.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static int receivePublishMsg(Instance originInstance, byte packet[]) throws IOException
    {
        if(packet.length <= (MESSAGE_TYPE_BYTE_SIZE + HpbConstants.HASH_ALGORITHM_DIGEST_LENGTH)) {
            Log.e(TAG, PROTOCOL_LOG_PREFIX + "Received Publish message with an invalid length");
            return -1;
        }

        HpbMessage hpbMsg = new HpbMessage(HpbMessageType.PUBLISH, extractServiceKeyFromReceivedPacket(packet), new String(extractInfoFromReceivedPacket(packet), HpbConstants.ENCODING_STANDARD));
        printMsgReceivedLog(hpbMsg, originInstance);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processPublishReq(hpbMsg.getServiceKey(), hpbMsg.getInfo());
        return 0;
    }

    /**
     * Processes an info message and forwards it to {@link HypePubSub#processInfoMsg(byte[], String)}
     *
     * @param originInstance Hype instance that sent the message
     * @param packet Packet received from the Hype SDK
     * @return Returns 0 in case of sucess and <0 in case of error.
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    private static int receiveInfoMsg(Instance originInstance, byte packet[]) throws UnsupportedEncodingException
    {
        if(packet.length <= (MESSAGE_TYPE_BYTE_SIZE + HpbConstants.HASH_ALGORITHM_DIGEST_LENGTH)) {
            Log.e(TAG, PROTOCOL_LOG_PREFIX + "Received Info message with an invalid length");
            return -1;
        }

        HpbMessage hpbMsg = new HpbMessage(HpbMessageType.INFO, extractServiceKeyFromReceivedPacket(packet), new String(extractInfoFromReceivedPacket(packet), HpbConstants.ENCODING_STANDARD));
        printMsgReceivedLog(hpbMsg, originInstance);
        HypePubSub hpb = HypePubSub.getInstance();
        hpb.processInfoMsg(hpbMsg.getServiceKey(), hpbMsg.getInfo());
        return 0;
    }

    //////////////////////////////////////////////////////////////////////////////
    // Received Message Data Extraction Methods
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Extracts the HpbMessageType from a packet received from the Hype SDK
     *
     * @param packet Received packet
     * @return Returns the HpbMessageType
     */
    public static HpbMessageType extractHpbMessageTypeFromReceivedPacket(byte packet[])
    {
        if(packet.length <= 0) {
            return HpbMessageType.INVALID;
        }

        if(packet[0] == ((byte) HpbMessageType.SUBSCRIBE_SERVICE.ordinal())) {
            return HpbMessageType.SUBSCRIBE_SERVICE;
        }
        else if(packet[0] == ((byte) HpbMessageType.UNSUBSCRIBE_SERVICE.ordinal())) {
            return HpbMessageType.UNSUBSCRIBE_SERVICE;
        }
        else if(packet[0] == ((byte) HpbMessageType.PUBLISH.ordinal())) {
            return HpbMessageType.PUBLISH;
        }
        else if(packet[0] == ((byte) HpbMessageType.INFO.ordinal())) {
            return HpbMessageType.INFO;
        }

        return HpbMessageType.INVALID;
    }

    /**
     * Extracts the ServiceKey from a packet received from the Hype SDK
     *
     * @param packet Received packet
     * @return Returns the ServiceKey specified on the packet
     */
    private static byte[] extractServiceKeyFromReceivedPacket(byte packet[])
    {
        return Arrays.copyOfRange(packet, MESSAGE_TYPE_BYTE_SIZE,
                MESSAGE_TYPE_BYTE_SIZE + HpbConstants.HASH_ALGORITHM_DIGEST_LENGTH);
    }

    /**
     * Extracts the info from a packet received from the Hype SDK
     *
     * @param packet Received packet
     * @return Returns the Info specified on the packet
     */
    private static byte[] extractInfoFromReceivedPacket(byte packet[])
    {
        return Arrays.copyOfRange(packet,MESSAGE_TYPE_BYTE_SIZE + HpbConstants.HASH_ALGORITHM_DIGEST_LENGTH,
                packet.length);
    }

    //////////////////////////////////////////////////////////////////////////////
    // Logging Methods
    //////////////////////////////////////////////////////////////////////////////

    /**
     * Prints a info log message when a message is sent through the Hype SDK
     *
     * @param hpbMsg HpbMessage that will be sent
     * @param destination Hype instance destination of the message
     * @throws UnsupportedEncodingException
     */
    static void printMsgSendLog(HpbMessage hpbMsg, Instance destination) throws UnsupportedEncodingException
    {
        Log.i(TAG, PROTOCOL_LOG_PREFIX
                + "Sending " + hpbMsg.toLogString()
                + " Destination " + HpbGenericUtils.getInstanceLogIdStr(destination));
    }

    /**
     * Prints a info log message when a message is received through the Hype SDK
     *
     * @param hpbMsg HpbMessage that was received
     * @param originator Originator Hype instance of the message
     * @throws UnsupportedEncodingException
     */
    static void printMsgReceivedLog(HpbMessage hpbMsg, Instance originator) throws UnsupportedEncodingException
    {
        Log.i(TAG, PROTOCOL_LOG_PREFIX
                + "Received " + hpbMsg.toLogString()
                + " Originator " + HpbGenericUtils.getInstanceLogIdStr(originator));
    }
}
