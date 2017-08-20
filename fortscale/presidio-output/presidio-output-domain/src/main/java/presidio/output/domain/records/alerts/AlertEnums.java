package presidio.output.domain.records.alerts;

import org.springframework.beans.factory.annotation.Value;

public class AlertEnums {

    public static enum AlertSeverity {
        CRITICAL, HIGH, MEDIUM, LOW;

        @Value("${severity.critical.score}")
        private static double CRITICAL_SCORE;
        @Value("${severity.high.score}")
        private static double HIGH_SCORE;
        @Value("${severity.mid.score}")
        private static double MID_SCORE;
        @Value("${severity.low.score}")
        private static double LOW_SCORE;

        public static AlertSeverity severity(double score) {
            if (LOW_SCORE <= score && score < MID_SCORE)
                return LOW;
            if (MID_SCORE <= score && score < HIGH_SCORE)
                return MEDIUM;
            if (HIGH_SCORE <= score && score < CRITICAL_SCORE)
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
