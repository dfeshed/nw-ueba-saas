package presidio.output.domain.records.alerts;


public class AlertEnums {


    public enum AlertSeverity {
        CRITICAL("critical"), HIGH("high"), MEDIUM("medium"), LOW("low");

        private String value;

        AlertSeverity(String value) {
            this.value = value;
        }
    }

    public enum AlertTimeframe {
        HOURLY("hourly"), DAILY("daily");

        private String value;

        AlertTimeframe(String value) {
            this.value = value;
        }

        public static AlertTimeframe getAlertTimeframe(String value) {
            return AlertTimeframe.valueOf(value.toUpperCase());
        }
    }

}
