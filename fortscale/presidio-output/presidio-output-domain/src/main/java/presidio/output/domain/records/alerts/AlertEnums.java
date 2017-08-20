package presidio.output.domain.records.alerts;


public class AlertEnums {


    public enum AlertSeverity {
        CRITICAL, HIGH, MEDIUM, LOW
    }

    public enum AlertTimeframe {
        HOURLY, DAILY
    }

    public enum AlertType {
        GLOBAL, DATA_EXFILTRATION, BRUTE_FORCE, ANOMALOUS_ADMIN_ACTIVITY, SNOOPING
    }

}
