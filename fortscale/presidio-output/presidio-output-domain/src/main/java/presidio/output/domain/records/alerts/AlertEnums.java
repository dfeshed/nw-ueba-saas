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
        HOURLY, DAILY;

        public static AlertTimeframe valueOfIgnoreCase(String value){
            for (AlertTimeframe timeframe:AlertTimeframe.values()){
                if (timeframe.name().toLowerCase().equals(value.toLowerCase())){
                    return timeframe;
                }
            }
            return null;
        }

        public static AlertTimeframe getAlertTimeframe(String value) {
            return AlertTimeframe.valueOf(value.toUpperCase());
        }
    }

}
