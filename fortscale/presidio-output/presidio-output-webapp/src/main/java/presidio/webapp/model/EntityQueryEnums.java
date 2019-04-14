package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import presidio.output.domain.records.entity.Entity;

public class EntityQueryEnums {

    public enum EntityQuerySortFieldName {
        SCORE(Entity.SCORE_FIELD_NAME), ALERT_NUM(Entity.ALERTS_COUNT_FIELD_NAME), ENTITY_NAME(Entity.ENTITY_NAME_FIELD_NAME),
        ENTITY_TYPE_NAME(Entity.ENTITY_TYPE_FIELD_NAME);

        private String value;

        EntityQuerySortFieldName(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static EntityQuerySortFieldName fromValue(String text) {
            for (EntityQuerySortFieldName b : EntityQuerySortFieldName.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public enum EntityQueryAggregationFieldName {
        SEVERITY(Entity.SEVERITY_FIELD_NAME), TAGS(Entity.TAGS_FIELD_NAME), ALERT_CLASSIFICATIONS(Entity.ALERT_CLASSIFICATIONS_FIELD_NAME),
        INDICATORS(Entity.INDICATORS_FIELD_NAME);

        private String value;

        EntityQueryAggregationFieldName(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static EntityQueryAggregationFieldName fromValue(String text) {
            for (EntityQueryAggregationFieldName b : EntityQueryAggregationFieldName.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    public enum EntitySeverity {

        CRITICAL("CRITICAL"),

        HIGH("HIGH"),

        MEDIUM("MEDIUM"),

        LOW("LOW");

        private String value;

        EntitySeverity(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static EntitySeverity fromValue(String text) {
            for (EntitySeverity b : EntitySeverity.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }
}
