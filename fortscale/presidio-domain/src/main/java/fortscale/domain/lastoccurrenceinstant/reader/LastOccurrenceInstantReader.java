package fortscale.domain.lastoccurrenceinstant.reader;

import fortscale.common.general.Schema;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

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

    /**
     * Read from the store the {@link Instant} of the last occurrence of each of the given entities.
     *
     * @param schema     The schema that the entities belong to (e.g. {@link Schema#TLS}).
     * @param entityType The type of the entities (e.g. "domain").
     * @param entityIds  The IDs of the entities (e.g. "amazon.com", "google.com", "apple.com").
     * @return A map from an entity ID to the {@link Instant} of its last occurrence.
     */
    Map<String, Instant> readAll(Schema schema, String entityType, Collection<String> entityIds);
}
