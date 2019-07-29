package presidio.input.core.services.transformation.factory;

import fortscale.common.general.Schema;
import fortscale.utils.factory.FactoryConfig;

import java.time.Duration;
import java.time.Instant;

public class NewOccurrenceTransformerConf implements FactoryConfig {
    public static final String NEW_OCCURRENCE_TRANSFORMER_FACTORY_NAME = "new_occurrence_transformer";

    private final Schema schema;
    private final String entityType;
    private final String instantFieldName;
    private final Duration expirationDelta;
    private final String booleanFieldName;

    /**
     * Constructor.
     *
     * @param schema           The schema that the raw event belongs to.
     * @param entityType       The name of the field whose value is checked to see if it is a new occurrence.
     * @param instantFieldName The name of the field whose value is the logical {@link Instant} of the raw event.
     * @param expirationDelta  If the last occurrence of the entity is older than this {@link Duration}
     *                         (compared to the logical {@link Instant} of the raw event),
     *                         it is considered as a new occurrence.
     * @param booleanFieldName The name of the boolean field whose value is set:
     *                         true if the entity is a new occurrence, false otherwise.
     */
    public NewOccurrenceTransformerConf(
            Schema schema,
            String entityType,
            String instantFieldName,
            Duration expirationDelta,
            String booleanFieldName) {

        this.schema = schema;
        this.entityType = entityType;
        this.instantFieldName = instantFieldName;
        this.expirationDelta = expirationDelta;
        this.booleanFieldName = booleanFieldName;
    }

    @Override
    public String getFactoryName() {
        return NEW_OCCURRENCE_TRANSFORMER_FACTORY_NAME;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getInstantFieldName() {
        return instantFieldName;
    }

    public Duration getExpirationDelta() {
        return expirationDelta;
    }

    public String getBooleanFieldName() {
        return booleanFieldName;
    }
}
