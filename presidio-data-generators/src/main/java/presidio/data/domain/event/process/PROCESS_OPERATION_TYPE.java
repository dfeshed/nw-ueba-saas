package presidio.data.domain.event.process;

import java.util.Arrays;

public enum PROCESS_OPERATION_TYPE {
    OPEN_PROCESS("OPEN_PROCESS"),
    CREATE_PROCESS("CREATE_PROCESS"),
    CREATE_REMOTE_THREAD("CREATE_REMOTE_THREAD");

    public final String value;

    PROCESS_OPERATION_TYPE(String value) {
        this.value = value;
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
