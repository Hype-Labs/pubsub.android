package hypelabs.com.hypepubsub;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class ClientTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("hypelabs.com.hypepubsub", appContext.getPackageName());
    }

    public void testObjectCreation() throws NoSuchAlgorithmException {
        byte CLIENT1_ID[] = new byte[] {(byte) 0x85, (byte) 0xa9, (byte) 0xd4, (byte) 0xc4, (byte) 0xde, (byte) 0xd2, (byte) 0x87, (byte) 0x75, (byte) 0x0f, (byte) 0xc0, (byte) 0xed, (byte) 0x32};
        byte CLIENT2_ID[] = new byte[] {(byte) 0x66, (byte) 0xd8, (byte) 0xf2, (byte) 0x20, (byte) 0x6a, (byte) 0x56, (byte) 0xdb, (byte) 0xe9, (byte) 0x91, (byte) 0x23, (byte) 0x3b, (byte) 0xc2};
        byte CLIENT3_ID[] = new byte[] {(byte) 0xe7, (byte) 0x79, (byte) 0x34, (byte) 0x6c, (byte) 0x66, (byte) 0x9c, (byte) 0x17, (byte) 0xf4, (byte) 0x34, (byte) 0xc8, (byte) 0xce, (byte) 0x0e};

        byte CLIENT1_ID_KEY[] = new byte[] {(byte) 0x05, (byte) 0xeb, (byte) 0x63, (byte) 0x7c, (byte) 0xbd, (byte) 0x3f, (byte) 0x33, (byte) 0x69, (byte) 0x1d, (byte) 0x74, (byte) 0x3c, (byte) 0x2a, (byte) 0x39, (byte) 0xaf, (byte) 0xee, (byte) 0xda, (byte) 0x5e, (byte) 0xc9, (byte) 0x45, (byte) 0xad};
        byte CLIENT2_ID_KEY[] = new byte[] {(byte) 0xf6, (byte) 0xcb, (byte) 0x6d, (byte) 0x9d, (byte) 0xb0, (byte) 0x98, (byte) 0x91, (byte) 0x9b, (byte) 0x2d, (byte) 0x39, (byte) 0x55, (byte) 0x11, (byte) 0x41, (byte) 0xc5, (byte) 0xcb, (byte) 0xe7, (byte) 0x67, (byte) 0xb5, (byte) 0x06, (byte) 0xd6};
        byte CLIENT3_ID_KEY[] = new byte[] {(byte) 0xe4, (byte) 0x9a, (byte) 0xa7, (byte) 0x79, (byte) 0x2c, (byte) 0xf4, (byte) 0xfd, (byte) 0x09, (byte) 0x6c, (byte) 0x10, (byte) 0x3f, (byte) 0x4b, (byte) 0xa4, (byte) 0x63, (byte) 0xe2, (byte) 0x7b, (byte) 0x91, (byte) 0x60, (byte) 0x9e, (byte) 0x6b};

        Client cl1 = new Client(CLIENT1_ID);
        Client cl2 = new Client(CLIENT2_ID);
        Client cl3 = new Client(CLIENT3_ID);

        // Validate the IDs and keys of the structs created
        assertEquals(cl1.id, CLIENT1_ID);
        assertEquals(cl2.id, CLIENT2_ID);
        assertEquals(cl3.id, CLIENT3_ID);
        assertEquals(cl1.key, CLIENT1_ID_KEY);
        assertEquals(cl2.key, CLIENT2_ID_KEY);
        assertEquals(cl3.key, CLIENT3_ID_KEY);
    }
}
