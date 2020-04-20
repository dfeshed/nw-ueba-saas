package presidio.nw.flume.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 *
 */
public enum SchemaEnum {

    FILE("FILE"),

    ACTIVE_DIRECTORY("ACTIVE_DIRECTORY"),

    AUTHENTICATION("AUTHENTICATION"),

    PRINT("PRINT");

    private String value;

    SchemaEnum(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SchemaEnum fromValue(String text) {
        for (SchemaEnum b : SchemaEnum.values()) {
            if (String.valueOf(b.value).equals(text.toUpperCase())) {
                return b;
            }
        }
        return null;
    }
}

