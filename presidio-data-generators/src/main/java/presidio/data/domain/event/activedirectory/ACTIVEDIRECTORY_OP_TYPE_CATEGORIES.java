package presidio.data.domain.event.activedirectory;

import java.util.Arrays;

public enum ACTIVEDIRECTORY_OP_TYPE_CATEGORIES {

    SECURITY_SENSITIVE_OPERATION ("SECURITY_SENSITIVE_OPERATION"),
    GROUP_MEMBERSHIP ("GROUP_MEMBERSHIP"),
    GROUP_MEMBERSHIP_ADD ("GROUP_MEMBERSHIP_ADD"),
    GROUP_MEMBERSHIP_REMOVE ("GROUP_MEMBERSHIP_REMOVE"),
    OBJECT_CHANGE_OPERATION("OBJECT_CHANGE_OPERATION"),
    USER_CHANGE_OPERATION("USER_CHANGE_OPERATION");

    public final String value;
    ACTIVEDIRECTORY_OP_TYPE_CATEGORIES(String value){
            this.value = value;
        }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
