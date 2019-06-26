package fortscale.domain.lastoccurrenceinstant;

import fortscale.common.general.Schema;

import java.time.Instant;

public interface LastOccurrenceInstantReader {
    /**
     * Read from the store the {@link Instant} of the last occurrence of the given entity.
     *
     * @param schema     The schema that the entity belongs to (e.g. {@link Schema#TLS}).
     * @param entityType The type of the entity (e.g. "domain").
     * @param entityId   The ID of the entity (e.g. "amazon.com").
     * @return The {@link Instant} of the last occurrence of the entity.
     */
    Instant read(Schema schema, String entityType, String entityId);
}
