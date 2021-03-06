package presidio.data.domain.event.authentication;

import java.util.Arrays;

public enum AUTHENTICATION_OPERATION_TYPE {

    USER_FAILED_TO_LOG_ON_INTERACTIVELY ("USER_FAILED_TO_LOG_ON_INTERACTIVELY"),
    USER_FAILED_TO_LOG_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER ("USER_FAILED_TO_LOG_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER"),
    USER_FAILED_TO_AUTHENTICATE_THROUGH_KERBEROS ("USER_FAILED_TO_AUTHENTICATE_THROUGH_KERBEROS"),
    INTERACTIVE("INTERACTIVE"),
    REMOTE_INTERACTIVE("REMOTE_INTERACTIVE"),
    USER_AUTHENTICATED_THROUGH_KERBEROS ("USER_AUTHENTICATED_THROUGH_KERBEROS");

    public final String value;
    AUTHENTICATION_OPERATION_TYPE(String value){
            this.value = value;
        }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
