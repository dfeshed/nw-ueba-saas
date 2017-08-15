package presidio.output.domain.records.alerts;

public class AlertEnums {

    public static enum AlertSeverity {
        CRITICAL, HIGH, MEDIUM, LOW;

        public static AlertSeverity severity(double score) {
            if (50 <= score && score < 70)
                return LOW;
            if (70 <= score && score < 85)
                return MEDIUM;
            if (85 <= score && score < 95)
                return HIGH;
            return CRITICAL;
        }
    }

    public static enum AlertTimeframe {
        HOURLY, DAILY
    }

    public static enum AlertType {
        GLOBAL, DATA_EXFILTRATION, BRUTE_FORCE, ANOMALOUS_ADMIN_ACTIVITY, SNOOPING
    }

}
