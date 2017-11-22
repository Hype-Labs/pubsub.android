package hypelabs.com.hypepubsub;

import com.hypelabs.hype.Instance;

import java.lang.reflect.Field;


public class FakeHypeInstance extends Instance
{
    protected FakeHypeInstance(byte[] identifier, byte[] announcement, boolean isResolved)
    {
        super(identifier, announcement, isResolved);
    }
}
