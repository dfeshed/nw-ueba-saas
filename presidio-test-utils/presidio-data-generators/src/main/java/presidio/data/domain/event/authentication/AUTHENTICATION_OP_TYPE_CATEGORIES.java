package presidio.data.domain.event.authentication;

import java.util.Arrays;

public enum AUTHENTICATION_OP_TYPE_CATEGORIES {

    INTERACTIVE_REMOTE ("INTERACTIVE_REMOTE");

    public final String value;
    AUTHENTICATION_OP_TYPE_CATEGORIES(String value){
        this.value = value;
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
