package presidio.output.forwarder.handlers.presidio.output.forwarder.handlers.syslog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SyslogEventsEnum {

    STREAMING_USERS_START("STREAMING-USERS-START"),
    STREAMING_USERS_END("STREAMING-USERS-END"),
    STREAMING_ALERTS_START("STREAMING-ALERTS-START"),
    STREAMING_ALERTS_END("STREAMING-ALERTS-END"),
    ALERT_ADDED("ALERT"),
    INDICATOR_ADDED("INDICATOR"),
    USER_CHANGED("USER");

    private String value;

    SyslogEventsEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SyslogEventsEnum fromValue(String text) {
        for (SyslogEventsEnum b : SyslogEventsEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
