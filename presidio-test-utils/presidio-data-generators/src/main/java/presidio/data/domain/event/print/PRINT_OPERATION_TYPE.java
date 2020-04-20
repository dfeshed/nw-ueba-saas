package presidio.data.domain.event.print;

import java.util.Arrays;

public enum PRINT_OPERATION_TYPE {
    //TODO: fill PRINT_OPERATION_TYPE according to list
    DOCUMENT_PRINTED ("DOCUMENT_PRINTED");

    public final String value;
    PRINT_OPERATION_TYPE(String value){
            this.value = value;
        }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
