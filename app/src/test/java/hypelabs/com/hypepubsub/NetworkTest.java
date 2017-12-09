package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class NetworkTest
{
    @Test
    public void testGetServiceManager() throws NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException
    {
        Network network = Network.getInstance();

        byte ID1[] = new byte[] {(byte) 0x85, (byte) 0xa9, (byte) 0xd4, (byte) 0xc4, (byte) 0xde, (byte) 0xd2, (byte) 0x87, (byte) 0x75, (byte) 0x0f, (byte) 0xc0, (byte) 0xed, (byte) 0x32};
        byte ID2[] = new byte[] {(byte) 0xe7, (byte) 0x79, (byte) 0x34, (byte) 0x6c, (byte) 0x66, (byte) 0x9c, (byte) 0x17, (byte) 0xf4, (byte) 0x34, (byte) 0xc8, (byte) 0xce, (byte) 0x0e};
        byte ID3[] = new byte[] {(byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};
        byte ID4[] = new byte[] {(byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};

        byte SERVICE_KEY1[] = new byte[] {(byte) 0xfe, (byte) 0xb5, (byte) 0xc6, (byte) 0xae, (byte) 0x8a, (byte) 0xb9, (byte) 0x7a, (byte) 0xdf, (byte) 0x53, (byte) 0xf8, (byte) 0xbc, (byte) 0x92, (byte) 0xe5, (byte) 0x51, (byte) 0x69, (byte) 0x82, (byte) 0xb6, (byte) 0x20, (byte) 0x0e, (byte) 0xa4};
        byte SERVICE_KEY2[] = new byte[] {(byte) 0x24, (byte) 0x62, (byte) 0xc4, (byte) 0x5a, (byte) 0x65, (byte) 0xd5, (byte) 0x91, (byte) 0x31, (byte) 0x86, (byte) 0xc9, (byte) 0xb3, (byte) 0x10, (byte) 0xa6, (byte) 0x90, (byte) 0x91, (byte) 0x64, (byte) 0xf5, (byte) 0x5e, (byte) 0xf6, (byte) 0x77};

        FakeHypeInstance instance1 = new FakeHypeInstance(ID1, null, false);
        FakeHypeInstance instance2 = new FakeHypeInstance(ID2, null, false);
        FakeHypeInstance instance3 = new FakeHypeInstance(ID3, null, false);
        FakeHypeInstance instance4 = new FakeHypeInstance(ID4, null, false);

        network.networkClients.addClient(new Client(instance1));
        network.networkClients.addClient(new Client(instance2));
        network.networkClients.addClient(new Client(instance3));
        network.networkClients.addClient(new Client(instance4));

        // Reset own client id
        network.setOwnClient(instance1);

        assertArrayEquals(ID4, network.determineClientResponsibleForService(SERVICE_KEY1).instance.getIdentifier());
        assertArrayEquals(ID1, network.determineClientResponsibleForService(SERVICE_KEY2).instance.getIdentifier());

        // Clear clients from Network singleton
        network.networkClients.removeClientWithInstance(instance1);
        network.networkClients.removeClientWithInstance(instance2);
        network.networkClients.removeClientWithInstance(instance3);
        network.networkClients.removeClientWithInstance(instance4);
    }

}
