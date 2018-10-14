package fortscale.domain.core.ioc;

public enum Level {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public static Level getLevel(String level) {
        return Level.valueOf(level.toUpperCase());
    }
}
