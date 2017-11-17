package hypelabs.com.hypepubsub;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class ServiceManagersListUnitTest
{
    @Test
    public void testListOperations() throws NoSuchAlgorithmException
    {
        byte SERVICE_KEY1[] = {(byte) 0xfe, (byte) 0xb5, (byte) 0xc6, (byte) 0xae, (byte) 0x8a, (byte) 0xb9, (byte) 0x7a, (byte) 0xdf, (byte) 0x53, (byte) 0xf8, (byte) 0xbc, (byte) 0x92, (byte) 0xe5, (byte) 0x51, (byte) 0x69, (byte) 0x82, (byte) 0xb6, (byte) 0x20, (byte) 0x0e, (byte) 0xa4};
        byte SERVICE_KEY2[] = {(byte) 0x24, (byte) 0x62, (byte) 0xc4, (byte) 0x5a, (byte) 0x65, (byte) 0xd5, (byte) 0x91, (byte) 0x31, (byte) 0x86, (byte) 0xc9, (byte) 0xb3, (byte) 0x10, (byte) 0xa6, (byte) 0x90, (byte) 0x91, (byte) 0x64, (byte) 0xf5, (byte) 0x5e, (byte) 0xf6, (byte) 0x77};
        byte SERVICE_KEY3[] = {(byte) 0x86, (byte) 0xc9, (byte) 0xb3, (byte) 0x10, (byte) 0xa6, (byte) 0x90, (byte) 0x91, (byte) 0x64, (byte) 0xf5, (byte) 0x5e, (byte) 0xf6, (byte) 0x77, (byte) 0x24, (byte) 0x62, (byte) 0xc4, (byte) 0x5a, (byte) 0x65, (byte) 0xd5, (byte) 0x91, (byte) 0x31};

        // Test the creation of the service managers list
        ServiceManagersList serviceManagers = new ServiceManagersList();
        assertNotNull(serviceManagers);
        assertEquals(serviceManagers.size(), 0);

        // Add 3 service managers to the list
        serviceManagers.add(SERVICE_KEY2);
        serviceManagers.add(SERVICE_KEY1);
        serviceManagers.add(SERVICE_KEY3);

        // Validate that the service managers are inserted in the right order
        assertArrayEquals(serviceManagers.get(0).serviceKey, SERVICE_KEY2);
        assertArrayEquals(serviceManagers.get(1).serviceKey, SERVICE_KEY1);
        assertArrayEquals(serviceManagers.get(2).serviceKey, SERVICE_KEY3);
        assertEquals(serviceManagers.size(), 3);

        // Test find against existent and non-existent service keys
        byte NON_EXISTENT_KEY[] = {(byte) 0x86, (byte) 0xc9, (byte) 0xb3, (byte) 0x10, (byte) 0x77, (byte) 0x24, (byte) 0x62, (byte) 0xc4, (byte) 0xa6, (byte) 0x90, (byte) 0x91, (byte) 0x64, (byte) 0xf5, (byte) 0x5e, (byte) 0xf6, (byte) 0x5a, (byte) 0x65, (byte) 0xd5, (byte) 0x91, (byte) 0x31};
        assertNotNull(serviceManagers.find(SERVICE_KEY1));
        assertNotNull(serviceManagers.find(SERVICE_KEY2));
        assertNotNull(serviceManagers.find(SERVICE_KEY3));
        assertNull(serviceManagers.find(NON_EXISTENT_KEY));

        // Remove a service manager from list and validate that the list is correctly
        // modified
        serviceManagers.remove(SERVICE_KEY1);
        assertArrayEquals(serviceManagers.get(0).serviceKey, SERVICE_KEY2);
        assertArrayEquals(serviceManagers.get(1).serviceKey, SERVICE_KEY3);
        assertEquals(serviceManagers.size(), 2);

        // Remove the service manager which is the header of the list and validate
        // that the list is correctly modified
        serviceManagers.remove(SERVICE_KEY2);
        assertArrayEquals(serviceManagers.get(0).serviceKey, SERVICE_KEY3);
        assertEquals(serviceManagers.size(), 1);

        // Remove the last service manager from the list and validate that the list
        // is correctly modified
        serviceManagers.remove(SERVICE_KEY3);
        assertEquals(serviceManagers.size(), 0);

    }
}