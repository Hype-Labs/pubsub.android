package hypelabs.com.hypepubsub;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import hypelabs.com.hypepubsub.Protocol.MessageType;

public class HpbMessage
{
    private MessageType type;
    private byte[] serviceKey;
    private String info;

    public HpbMessage(MessageType type, byte[] serviceKey, String info)
    {
        this.type = type;
        this.serviceKey = serviceKey;
        this.info = info;
    }

    public byte[] toByteArray() throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) type.ordinal());
        outputStream.write(serviceKey);
        if(info != null) {
            outputStream.write(info.getBytes(Constants.HPB_ENCODING_STANDARD));
        }
        return outputStream.toByteArray();
    }

    public MessageType getType(){
        return type;
    }

    public byte[] getServiceKey(){
        return serviceKey;
    }

    public String getInfo(){
        return info;
    }
}
