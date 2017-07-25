package presidio.output.domain.records;

public class AlertEnums {

    public static enum AlertSeverity {
        CRITICAL, HIGH, MEDIUM, LOW
    }

    public static enum AlertTimeframe {
        HOURLY, DAILY
    }

    public static enum AlertType {
        GLOBAL, DATA_EXFILTRATION, BRUTE_FORCE, ANOMALOUS_ADMIN_ACTIVITY, SNOOPING
    }

}
