package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class ClientsListUnitTest
{
    @Test
    public void testClientsList() throws NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException {
        byte CLIENT_ID1[] = new byte[] {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11, (byte) 0x12};
        byte CLIENT_ID2[] = new byte[] {(byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11};
        byte CLIENT_ID3[] = new byte[] {(byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10};
        byte CLIENT_ID4[] = new byte[] {(byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};

        FakeHypeInstance instance1 = new FakeHypeInstance(CLIENT_ID1, null, false);
        FakeHypeInstance instance2 = new FakeHypeInstance(CLIENT_ID2, null, false);
        FakeHypeInstance instance3 = new FakeHypeInstance(CLIENT_ID3, null, false);
        FakeHypeInstance instance4 = new FakeHypeInstance(CLIENT_ID4, null, false);

        // Test the creation of a client's list
        ClientsList clients = new ClientsList();
        assertNotNull(clients);
        assertEquals(0, clients.size());

        // Add 4 clients to the list
        clients.addClient(new Client(instance3));
        clients.addClient(new Client(instance2));
        clients.addClient(new Client(instance4));
        clients.addClient(new Client(instance1));

        // Validate that the clients are inserted in the right order
        assertArrayEquals(CLIENT_ID3, clients.get(0).instance.getIdentifier());
        assertArrayEquals(CLIENT_ID2, clients.get(1).instance.getIdentifier());
        assertArrayEquals(CLIENT_ID4, clients.get(2).instance.getIdentifier());
        assertArrayEquals(CLIENT_ID1, clients.get(3).instance.getIdentifier());
        assertEquals(4, clients.size());

        // Remove the client which is the header of the list and validate
        // that the list is correctly modified
        clients.removeClientWithInstance(instance3);
        assertArrayEquals(CLIENT_ID2, clients.get(0).instance.getIdentifier());
        assertArrayEquals(CLIENT_ID4, clients.get(1).instance.getIdentifier());
        assertArrayEquals(CLIENT_ID1, clients.get(2).instance.getIdentifier());
        assertEquals(3, clients.size());

        // Remove client that was already removed and validate that nothing
        // happens
        clients.removeClientWithInstance(instance3);
        assertEquals(3, clients.size());

        // Remove another client and validate that the list is correctly
        // modified
        clients.removeClientWithInstance(instance4);
        assertArrayEquals(CLIENT_ID2, clients.get(0).instance.getIdentifier());
        assertArrayEquals(CLIENT_ID1, clients.get(1).instance.getIdentifier());
        assertEquals(2, clients.size());

        // Remove another client and validate that the list is correctly
        // modified
        clients.removeClientWithInstance(instance1);
        assertArrayEquals(CLIENT_ID2, clients.get(0).instance.getIdentifier());
        assertEquals(1, clients.size());

        // Remove last client of the list
        clients.removeClientWithInstance(instance2);
        assertEquals(0, clients.size());

        // Add a client that was previously removed
        clients.addClient(new Client(instance4));
        assertArrayEquals(CLIENT_ID4, clients.get(0).instance.getIdentifier());
        assertEquals(1, clients.size());

        // Add all 4 new clients again. Client 4 was already inserted so
        // we validate that it is not duplicated.
        clients.addClient(new Client(instance1));
        clients.addClient(new Client(instance2));
        clients.addClient(new Client(instance3));
        clients.addClient(new Client(instance4));
        assertArrayEquals(CLIENT_ID4, clients.get(0).instance.getIdentifier());
        assertArrayEquals(CLIENT_ID1, clients.get(1).instance.getIdentifier());
        assertArrayEquals(CLIENT_ID2, clients.get(2).instance.getIdentifier());
        assertArrayEquals(CLIENT_ID3, clients.get(3).instance.getIdentifier());
        assertEquals(4, clients.size());

        // Test findClientWithInstance against a non-existent ID
        byte NON_EXISTENT_CLIENT_ID[] = new byte[] {(byte) 0x16, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x12, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};
        FakeHypeInstance nonExistentInstance = new FakeHypeInstance(NON_EXISTENT_CLIENT_ID, null, false);

        Client auxCl = clients.findClientWithInstance(nonExistentInstance);
        assertNull(auxCl);

        // Test findClientWithInstance against a existent IDs. Validate client's IDs and keys.
        byte CLIENT_KEY3[] = new byte[] {(byte) 0x9a, (byte) 0xc1, (byte) 0xb0, (byte) 0x41, (byte) 0x5e, (byte) 0x0a, (byte) 0x97, (byte) 0x73, (byte) 0x8c, (byte) 0x57, (byte) 0xe7, (byte) 0xe6, (byte) 0x3f, (byte) 0x68, (byte) 0x50, (byte) 0xab, (byte) 0x21, (byte) 0xe4, (byte) 0x7e, (byte) 0xb4};
        byte CLIENT_KEY4[] = new byte[] {(byte) 0x44, (byte) 0x20, (byte) 0x01, (byte) 0xf9, (byte) 0x64, (byte) 0xd9, (byte) 0xfe, (byte) 0x34, (byte) 0x9a, (byte) 0x5f, (byte) 0x30, (byte) 0x8a, (byte) 0xb1, (byte) 0x41, (byte) 0x15, (byte) 0x0e, (byte) 0x05, (byte) 0x5b, (byte) 0xe5, (byte) 0x46};
        auxCl = clients.findClientWithInstance(instance3);
        assertNotNull(auxCl);
        assertArrayEquals(CLIENT_ID3, auxCl.instance.getIdentifier());
        assertArrayEquals(CLIENT_KEY3, auxCl.key);
        auxCl = clients.findClientWithInstance(instance4);
        assertNotNull(auxCl);
        assertArrayEquals(CLIENT_ID4, auxCl.instance.getIdentifier());
        assertArrayEquals(CLIENT_KEY4, auxCl.key);
    }
}