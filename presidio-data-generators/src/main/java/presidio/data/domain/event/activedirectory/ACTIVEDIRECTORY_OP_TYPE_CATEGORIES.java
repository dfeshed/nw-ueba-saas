package presidio.data.domain.event.activedirectory;

import java.util.Arrays;

public enum ACTIVEDIRECTORY_OP_TYPE_CATEGORIES {

    SECURITY_SENSITIVE_OPERATION ("SECURITY_SENSITIVE_OPERATION"),
    GROUP_MEMBERSHIP_OPERATION("GROUP_MEMBERSHIP_OPERATION"),
    GROUP_MEMBERSHIP_ADD_OPERATION("GROUP_MEMBERSHIP_ADD_OPERATION"),
    GROUP_MEMBERSHIP_REMOVE_OPERATION("GROUP_MEMBERSHIP_REMOVE_OPERATION"),
    OBJECT_MANAGEMENT("OBJECT_MANAGEMENT"),
    USER_MANAGEMENT("USER_MANAGEMENT");

    public final String value;
    ACTIVEDIRECTORY_OP_TYPE_CATEGORIES(String value){
            this.value = value;
        }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
