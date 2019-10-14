package presidio.data.generators.common.list.content;

import java.util.function.Function;

import static com.google.common.net.InetAddresses.fromInteger;

public class Ipv4 {

    // 167772160 -> 10.0.0.0
    public static final int START_POSITION = 167772160;

    public static Function<Integer, String> indexToIpv4 = index -> fromInteger(index + START_POSITION).getHostAddress();
}
