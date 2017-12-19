package hypelabs.com.hypepubsub;


import java.util.ArrayList;
import java.util.Arrays;

public class HpsConstants
{
    static public final String APP_IDENTIFIER = "db2b109d";
    static public final String HASH_ALGORITHM = "SHA-1";
    static public final int HASH_ALGORITHM_DIGEST_LENGTH = 20;
    static public final String ENCODING_STANDARD = "UTF-8";
    static public final String NOTIFICATIONS_CHANNEL = "HypePubSub";
    static public final String NOTIFICATIONS_TITLE = "HypePubSub";
    static public final String LOG_PREFIX = " :: HpsApplication :: ";
    static public final ArrayList<String> STANDARD_HYPE_SERVICES = new ArrayList<>(Arrays.asList(
            "hype-jobs", "hype-sports", "hype-news", "hype-weather", "hype-music", "hype-movies"));
}
