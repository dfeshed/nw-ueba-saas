package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class UserQueryEnums {

    public enum UserQuerySortFieldName {
        SCORE("score");

        private String value;

        UserQuerySortFieldName(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static UserQuerySortFieldName fromValue(String text) {
            for (UserQuerySortFieldName b : UserQuerySortFieldName.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public enum UserQueryAggregationFieldName {
        SEVERITY("severity"), TAGS("tags");
        private String value;

        UserQueryAggregationFieldName(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static UserQueryAggregationFieldName fromValue(String text) {
            for (UserQueryAggregationFieldName b : UserQueryAggregationFieldName.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public enum UserSeverity {

        CRITICAL("CRITICAL"),

        HIGH("HIGH"),

        MEDIUM("MEDIUM"),

        LOW("LOW");

        private String value;

        UserSeverity(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static UserSeverity fromValue(String text) {
            for (UserSeverity b : UserSeverity.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }
}
