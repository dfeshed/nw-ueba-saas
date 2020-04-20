package fortscale.common.general;

import org.apache.commons.lang3.Validate;

public class SchemaEntityCount {
    public static final String SCHEMA_FIELD = "schema";
    public static final String ENTITY_TYPE_FIELD = "entityType";
    public static final String ENTITY_ID_FIELD = "entityId";
    public static final String COUNT_FIELD = "count";

    private final Schema schema;
    private final String entityType;
    private final String entityId;
    private final int count;

    public SchemaEntityCount(Schema schema, String entityType, String entityId, int count) {
        Validate.notNull(schema, "schema cannot be null.");
        Validate.notBlank(entityType, "entityType cannot be blank.");
        Validate.notBlank(entityId, "entityId cannot be blank.");
        Validate.isTrue(count >= 0, "count cannot be negative.");
        this.schema = schema;
        this.entityType = entityType;
        this.entityId = entityId;
        this.count = count;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public int getCount() {
        return count;
    }
}
