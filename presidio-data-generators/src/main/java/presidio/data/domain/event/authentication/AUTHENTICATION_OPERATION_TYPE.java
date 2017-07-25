package presidio.data.domain.event.authentication;

import java.util.Arrays;

/**
 * Created by presidio on 7/13/17.
 */
public enum AUTHENTICATION_OPERATION_TYPE {

    NETWORK_OPERATION ("NETWORK"),
    DOMAIN_OPERATION ("DOMAIN"),
    INTERACTIVE_OPERATION ("INTERACTIVE");

    public final String value;
    AUTHENTICATION_OPERATION_TYPE(String value){
            this.value = value;
        }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
