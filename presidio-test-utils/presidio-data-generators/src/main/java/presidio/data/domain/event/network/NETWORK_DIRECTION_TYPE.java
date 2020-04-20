package presidio.data.domain.event.network;

import java.util.Arrays;

public enum NETWORK_DIRECTION_TYPE {
    INBOUND("inbound"),
    OUTBOUND("outbound");

    public final String value;

    NETWORK_DIRECTION_TYPE(String value) {
        this.value = value;
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
