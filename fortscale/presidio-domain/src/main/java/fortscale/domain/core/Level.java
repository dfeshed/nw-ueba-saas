package fortscale.domain.core;

public enum Level {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static Level getLevel(String result) {
        return Level.valueOf(result.toUpperCase());
    }
}
