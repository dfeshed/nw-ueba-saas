package fortscale.domain.lastoccurrenceinstant.writer;

import fortscale.common.general.Schema;

import java.time.Instant;
import java.util.Map;

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

    /**
     * Associate in the store the given entities with the given {@link Instant}s of their last occurrences.
     *
     * @param schema                             The schema that the entities belong to (e.g. {@link Schema#TLS}).
     * @param entityType                         The type of the entities (e.g. "domain").
     * @param entityIdToLastOccurrenceInstantMap A map from an entity ID to the {@link Instant} of its last occurrence.
     */
    void writeAll(Schema schema, String entityType, Map<String, Instant> entityIdToLastOccurrenceInstantMap);

    /**
     * Close the store, release any resources associated with it and persist any buffered or cached information.
     */
    void close();
}
