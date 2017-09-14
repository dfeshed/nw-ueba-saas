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
}
