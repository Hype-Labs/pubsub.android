package hypelabs.com.hypepubsub;


import com.hypelabs.hype.Instance;

import java.io.UnsupportedEncodingException;

public class ConfirmationQueueElement
{
    HpbMessage hpbMessage;
    int identifier;
    Instance destination;
    int nRetransmissions;

    public ConfirmationQueueElement(HpbMessage hpbMessage, int identifier, Instance destination)
    {
        this.hpbMessage = hpbMessage;
        this.identifier = identifier;
        this.destination = destination;
        this.nRetransmissions = 1;
    }

    public String toLogString() throws UnsupportedEncodingException
    {
        String logString;

        if(hpbMessage.getType() == Protocol.MessageType.SUBSCRIBE_SERVICE)
            logString = "Subscribe message (id: " + identifier + ") to "
                    + GenericUtils.getInstanceLogIdStr(destination)
                    + " for service 0x" + BinaryUtils.byteArrayToHexString(hpbMessage.getServiceKey());
        else if(hpbMessage.getType() == Protocol.MessageType.UNSUBSCRIBE_SERVICE)
            logString = "Unsubscribe message (id: " + identifier + ") to "
                    + GenericUtils.getInstanceLogIdStr(destination)
                    + " for service 0x" + BinaryUtils.byteArrayToHexString(hpbMessage.getServiceKey());
        else if(hpbMessage.getType() == Protocol.MessageType.PUBLISH)
            logString = "Publish message (id: " + identifier + ") to "
                    + GenericUtils.getInstanceLogIdStr(destination)
                    + " for service 0x" + BinaryUtils.byteArrayToHexString(hpbMessage.getServiceKey())
                    + ". HpbMessage: " + hpbMessage.getInfo();
        else if(hpbMessage.getType() == Protocol.MessageType.INFO)
            logString = "Info message (id: " + identifier + ") to "
                    + GenericUtils.getInstanceLogIdStr(destination)
                    + " for service 0x" + BinaryUtils.byteArrayToHexString(hpbMessage.getServiceKey())
                    + ". HpbMessage: " + hpbMessage.getInfo();
        else
            logString = "Invalid message to "
                    + GenericUtils.getInstanceAnnouncementStr(destination)
                    + " (0x" + BinaryUtils.byteArrayToHexString(destination.getIdentifier()) + ")";

        return logString;
    }
}
