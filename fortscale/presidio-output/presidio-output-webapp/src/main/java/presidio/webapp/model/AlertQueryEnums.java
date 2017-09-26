package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import presidio.output.domain.records.alerts.Alert;

public class AlertQueryEnums {
    public enum AlertQuerySortFieldName {
        SCORE(Alert.SCORE), START_DATE(Alert.START_DATE), END_DATE(Alert.END_DATE), INDICATORS_NUM(Alert.INDICATORS_NUM);

        private String value;

        AlertQuerySortFieldName(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static AlertQuerySortFieldName fromValue(String text) {
            for (AlertQuerySortFieldName b : AlertQuerySortFieldName.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public enum AlertQueryAggregationFieldName {
        SEVERITY(Alert.SEVERITY), CLASSIFICATIONS(Alert.CLASSIFICATIONS), SEVERITY_DAILY(Alert.AGGR_SEVERITY_PER_DAY), INDICATOR_NAMES(Alert.INDICATOR_NAMES);
        private String value;

        AlertQueryAggregationFieldName(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static AlertQueryAggregationFieldName fromValue(String text) {
            for (AlertQueryAggregationFieldName b : AlertQueryAggregationFieldName.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public enum AlertSeverity {

        CRITICAL("CRITICAL"),

        HIGH("HIGH"),

        MEDIUM("MEDIUM"),

        LOW("LOW");

        private String value;

        AlertSeverity(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static AlertSeverity fromValue(String text) {
            for (AlertSeverity b : AlertSeverity.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }
}
