package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class AlertQueryEnums {
    public enum AlertQuerySortFieldName {
        SCORE("score"), START_DATE("startDate"), END_DATE("endDate");

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
        SEVERITY("severity"), CLASSIFICATIONS("classifications");
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
