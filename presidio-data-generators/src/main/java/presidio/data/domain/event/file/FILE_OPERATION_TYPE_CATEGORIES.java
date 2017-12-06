package presidio.data.domain.event.file;

import java.util.Arrays;

public enum FILE_OPERATION_TYPE_CATEGORIES {

    FILE_ACTION ("FILE_ACTION"),
    FILE_PERMISSION_CHANGE ("FILE_PERMISSION_CHANGE"),
    TRIVIAL_FILE_OPERATION ("TRIVIAL_FILE_OPERATION");

    public final String value;
    FILE_OPERATION_TYPE_CATEGORIES(String value){
            this.value = value;
        }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
