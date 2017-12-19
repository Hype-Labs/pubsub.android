package hypelabs.com.hypepubsub;


import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class HpsMessage
{
    private HpsMessageType type;
    private byte[] serviceKey;
    private String info;

    public HpsMessage(HpsMessageType type, byte[] serviceKey, String info) {
        this.type = type;
        this.serviceKey = serviceKey;
        this.info = info;
    }

    public HpsMessage(HpsMessageType type, byte[] serviceKey) {
        this.type = type;
        this.serviceKey = serviceKey;
        this.info = null;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        outputStream.write((byte) type.ordinal());

        try {
            outputStream.write(serviceKey);
        }
        catch (IOException e){
            throw new RuntimeException("Could not write message ServiceKey to byte array" + e.getMessage(),e);
        }

        if(info != null) {
            try {
                outputStream.write(info.getBytes(HpsConstants.ENCODING_STANDARD));
            }
            catch (IOException e){
                throw new RuntimeException("Could not write message Info to byte array" + e.getMessage(),e);
            }

        }

        return outputStream.toByteArray();
    }

    public String toLogString() {
        String logString = String.format("%s message for service 0x%s.",
                type.toString(), BinaryUtils.byteArrayToHexString(serviceKey));
        if(info != null) {
            logString += String.format(" Info: %s.", info);
        }

        return logString;
    }

    public byte[] getServiceKey(){
        return serviceKey;
    }

    public String getInfo(){
        return info;
    }
}
