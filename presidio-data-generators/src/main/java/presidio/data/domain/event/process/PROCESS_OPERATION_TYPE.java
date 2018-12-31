package presidio.data.domain.event.process;

import java.util.Arrays;

public enum PROCESS_OPERATION_TYPE {
    OPEN_PROCESS("openProcess"),
    CREATE_PROCESS("createProcess"),
    CREATE_REMOTE_THREAD("createRemoteThread");

    public final String value;

    PROCESS_OPERATION_TYPE(String value) {
        this.value = value;
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
