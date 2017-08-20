package presidio.output.domain.records.alerts;


public class AlertEnums {

    private static double criticalScore;
    private static double highScore;
    private static double midScore;
    private static double lowScore;

    public AlertEnums(double criticalScore, double highScore, double midScore, double lowScore) {
        this.criticalScore = criticalScore;
        this.highScore = highScore;
        this.midScore = midScore;
        this.lowScore = lowScore;
    }

    public enum AlertSeverity {
        CRITICAL, HIGH, MEDIUM, LOW;


        public static AlertSeverity severity(double score) {
            if (lowScore <= score && score < midScore)
                return LOW;
            if (midScore <= score && score < highScore)
                return MEDIUM;
            if (highScore <= score && score < criticalScore)
                return HIGH;
            return CRITICAL;
        }
    }

    public enum AlertTimeframe {
        HOURLY, DAILY
    }

    public enum AlertType {
        GLOBAL, DATA_EXFILTRATION, BRUTE_FORCE, ANOMALOUS_ADMIN_ACTIVITY, SNOOPING
    }

}
