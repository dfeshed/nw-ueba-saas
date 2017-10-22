package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import presidio.output.domain.records.users.User;

public class UserQueryEnums {

    public enum UserQuerySortFieldName {
        SCORE(User.SCORE_FIELD_NAME), ALERT_NUM(User.ALERTS_COUNT_FIELD_NAME), USER_NAME(User.INDEXED_USER_NAME_FIELD_NAME);

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
        SEVERITY(User.SEVERITY_FIELD_NAME), TAGS(User.TAGS_FIELD_NAME), ALERT_CLASSIFICATIONS(User.ALERT_CLASSIFICATIONS_FIELD_NAME),
        INDICATORS(User.INDICATORS_FIELD_NAME);

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
