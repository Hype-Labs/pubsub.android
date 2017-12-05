package hypelabs.com.hypepubsub;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * This class represents a message from the HypePubSub application
 */
public class HpsMessage
{
    private HpsMessageType type;
    private byte[] serviceKey;
    private String info;

    public HpsMessage(HpsMessageType type, byte[] serviceKey, String info)
    {
        this.type = type;
        this.serviceKey = serviceKey;
        this.info = info;
    }

    public HpsMessage(HpsMessageType type, byte[] serviceKey)
    {
        this.type = type;
        this.serviceKey = serviceKey;
        this.info = null;
    }

    public byte[] toByteArray() throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) type.ordinal());
        outputStream.write(serviceKey);
        if(info != null) {
            outputStream.write(info.getBytes(HpsConstants.ENCODING_STANDARD));
        }
        return outputStream.toByteArray();
    }

    public String toLogString()
    {
        String logString = type.toString() + " message for service 0x"
                + BinaryUtils.byteArrayToHexString(serviceKey) + ".";
        if(info != null) {
            logString += " Info: " + info + ".";
        }

        return logString;
    }

    public HpsMessageType getType(){
        return type;
    }

    public byte[] getServiceKey(){
        return serviceKey;
    }

    public String getInfo(){
        return info;
    }
}
