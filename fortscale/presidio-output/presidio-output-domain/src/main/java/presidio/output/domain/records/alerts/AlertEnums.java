package presidio.output.domain.records.alerts;


public class AlertEnums {


    public enum AlertSeverity {
        CRITICAL("critical"), HIGH("high"), MEDIUM("medium"), LOW("low");

        private String value;

        AlertSeverity(String name) {
            this.value = value;
        }
    }

    public enum AlertTimeframe {
        HOURLY, DAILY
    }

    public enum AlertType {
        GLOBAL, DATA_EXFILTRATION, BRUTE_FORCE, ANOMALOUS_ADMIN_ACTIVITY, SNOOPING
    }

}
