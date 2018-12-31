package presidio.data.domain.event.registry;

import java.util.Arrays;

public enum REGISTRY_OPERATION_TYPE {
    CREATE_KEY("createKey"),
    SET_VALUE("modifyRegistryKey");

    public final String value;

    REGISTRY_OPERATION_TYPE(String value) {
        this.value = value;
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
