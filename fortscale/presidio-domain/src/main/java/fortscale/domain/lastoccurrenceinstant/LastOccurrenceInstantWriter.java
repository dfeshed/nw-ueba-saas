package fortscale.domain.lastoccurrenceinstant;

import fortscale.common.general.Schema;

import java.time.Instant;

public interface LastOccurrenceInstantWriter {
    /**
     * Associate in the store the given entity with the given {@link Instant} of its last occurrence.
     *
     * @param schema                The schema that the entity belongs to (e.g. {@link Schema#TLS}).
     * @param entityType            The type of the entity (e.g. "domain").
     * @param entityId              The ID of the entity (e.g. "amazon.com").
     * @param lastOccurrenceInstant The {@link Instant} of the last occurrence of the entity.
     */
    void write(Schema schema, String entityType, String entityId, Instant lastOccurrenceInstant);
}
