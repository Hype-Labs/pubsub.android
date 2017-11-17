package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class ClientsListUnitTest
{
    @Test
    public void testListOperations() throws NoSuchAlgorithmException
    {
        byte CLIENT_ID1[] = new byte[] {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11, (byte) 0x12};
        byte CLIENT_ID2[] = new byte[] {(byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10, (byte) 0x11};
        byte CLIENT_ID3[] = new byte[] {(byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x10};
        byte CLIENT_ID4[] = new byte[] {(byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};

        // Test the creation of a client's list
        ClientsList clients = new ClientsList();
        assertEquals(clients.size(), 0);

        // Add 4 clients to the list
        clients.add(CLIENT_ID3);
        clients.add(CLIENT_ID2);
        clients.add(CLIENT_ID4);
        clients.add(CLIENT_ID1);

        // Validate that the clients are inserted in the right order
        assertArrayEquals(clients.get(0).id, CLIENT_ID3);
        assertArrayEquals(clients.get(1).id, CLIENT_ID2);
        assertArrayEquals(clients.get(2).id, CLIENT_ID4);
        assertArrayEquals(clients.get(3).id, CLIENT_ID1);
        assertEquals(clients.size(), 4);

        // Remove the client which is the header of the list and validate
        // that the list is correctly modified
        clients.remove(CLIENT_ID3);
        assertArrayEquals(clients.get(0).id, CLIENT_ID2);
        assertArrayEquals(clients.get(1).id, CLIENT_ID4);
        assertArrayEquals(clients.get(2).id, CLIENT_ID1);
        assertEquals(clients.size(), 3);

        // Remove client that was already removed and validate that nothing
        // happens
        clients.remove(CLIENT_ID3);
        assertEquals(clients.size(), 3);

        // Remove another client and validate that the list is correctly
        // modified
        clients.remove(CLIENT_ID4);
        assertArrayEquals(clients.get(0).id, CLIENT_ID2);
        assertArrayEquals(clients.get(1).id, CLIENT_ID1);
        assertEquals(clients.size(), 2);

        // Remove another client and validate that the list is correctly
        // modified
        clients.remove(CLIENT_ID1);
        assertArrayEquals(clients.get(0).id, CLIENT_ID2);
        assertEquals(clients.size(), 1);

        // Remove last client of the list
        clients.remove(CLIENT_ID2);
        assertEquals(clients.size(), 0);

        // Add a client that was previously removed
        clients.add(CLIENT_ID4);
        assertArrayEquals(clients.get(0).id, CLIENT_ID4);
        assertEquals(clients.size(), 1);

        // Add all 4 new clients again. Client 4 was already inserted so
        // we validate that it is not duplicated.
        clients.add(CLIENT_ID1);
        clients.add(CLIENT_ID2);
        clients.add(CLIENT_ID3);
        clients.add(CLIENT_ID4);
        assertArrayEquals(clients.get(0).id, CLIENT_ID4);
        assertArrayEquals(clients.get(1).id, CLIENT_ID1);
        assertArrayEquals(clients.get(2).id, CLIENT_ID2);
        assertArrayEquals(clients.get(3).id, CLIENT_ID3);
        assertEquals(clients.size(), 4);

        // Test find against a non-existent ID
        byte NON_EXISTENT_CLIENT_ID[] = new byte[] {(byte) 0x16, (byte) 0x11, (byte) 0x12, (byte) 0x01, (byte) 0x12, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09};
        Client auxCl = clients.find(NON_EXISTENT_CLIENT_ID);
        assertEquals(auxCl, null);

        // Test find against a existent IDs. Validate client's IDs and keys.
        byte CLIENT_KEY3[] = new byte[] {(byte) 0x9a, (byte) 0xc1, (byte) 0xb0, (byte) 0x41, (byte) 0x5e, (byte) 0x0a, (byte) 0x97, (byte) 0x73, (byte) 0x8c, (byte) 0x57, (byte) 0xe7, (byte) 0xe6, (byte) 0x3f, (byte) 0x68, (byte) 0x50, (byte) 0xab, (byte) 0x21, (byte) 0xe4, (byte) 0x7e, (byte) 0xb4};
        byte CLIENT_KEY4[] = new byte[] {(byte) 0x44, (byte) 0x20, (byte) 0x01, (byte) 0xf9, (byte) 0x64, (byte) 0xd9, (byte) 0xfe, (byte) 0x34, (byte) 0x9a, (byte) 0x5f, (byte) 0x30, (byte) 0x8a, (byte) 0xb1, (byte) 0x41, (byte) 0x15, (byte) 0x0e, (byte) 0x05, (byte) 0x5b, (byte) 0xe5, (byte) 0x46};
        auxCl = clients.find(CLIENT_ID3);
        assertNotEquals(auxCl, null);
        assertArrayEquals(auxCl.id, CLIENT_ID3);
        assertArrayEquals(auxCl.key, CLIENT_KEY3);
        auxCl = clients.find(CLIENT_ID4);
        assertNotEquals(auxCl, null);
        assertArrayEquals(auxCl.id, CLIENT_ID4);
        assertArrayEquals(auxCl.key, CLIENT_KEY4);
    }
}