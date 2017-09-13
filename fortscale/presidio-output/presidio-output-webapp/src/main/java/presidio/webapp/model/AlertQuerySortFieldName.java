package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
