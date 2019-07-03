package fortscale.domain.lastoccurrenceinstant;

import fortscale.common.general.Schema;
import fortscale.utils.data.LfuCache;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.time.Instant;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class LastOccurrenceInstantWriterCacheImpl implements LastOccurrenceInstantWriter {
    private final LastOccurrenceInstantWriter lastOccurrenceInstantWriter;
    private final LfuCache<Triple<Schema, String, String>, Instant> lfuCache;
    private final double entriesToRemovePercentage;

    /**
     * Constructor.
     *
     * @param lastOccurrenceInstantWriter The underlying store to which information is written.
     * @param maximumSize                 The maximum number of entries of this cache.
     * @param entriesToRemovePercentage   The percentage of entries to remove (and flush to the underlying store)
     *                                    when this cache is full.
     */
    public LastOccurrenceInstantWriterCacheImpl(
            LastOccurrenceInstantWriter lastOccurrenceInstantWriter,
            int maximumSize, double entriesToRemovePercentage) {

        Validate.notNull(lastOccurrenceInstantWriter, "lastOccurrenceInstantWriter cannot be null.");
        Validate.isTrue(maximumSize > 0, "maximumSize must be greater than zero but is %d.", maximumSize);
        LfuCache.assertPercentage(entriesToRemovePercentage);
        this.lastOccurrenceInstantWriter = lastOccurrenceInstantWriter;
        this.lfuCache = new LfuCache<>(maximumSize);
        this.entriesToRemovePercentage = entriesToRemovePercentage;
    }

    @Override
    public void write(Schema schema, String entityType, String entityId, Instant lastOccurrenceInstant) {
        Triple<Schema, String, String> key = Triple.of(schema, entityType, entityId);
        if (!lfuCache.containsKey(key) && lfuCache.isFull()) flush(entriesToRemovePercentage);
        lfuCache.put(key, lastOccurrenceInstant);
    }

    @Override
    public void writeAll(Schema schema, String entityType, Map<String, Instant> entityIdToLastOccurrenceInstantMap) {
        entityIdToLastOccurrenceInstantMap.forEach((entityId, lastOccurrenceInstant) ->
                write(schema, entityType, entityId, lastOccurrenceInstant)
        );
    }

    @Override
    public void close() {
        flush(100.0);
    }

    private void flush(double percentage) {
        // The removed LFU entries are of type {schema, entityType, entityId} -> lastOccurrenceInstant.
        lfuCache.removeLfuEntries(percentage).entrySet().stream()
                // Create for each {schema, entityType} pair a map from entityId to lastOccurrenceInstant.
                .collect(groupingBy(
                        entry -> Pair.of(entry.getKey().getLeft(), entry.getKey().getMiddle()),
                        toMap(entry -> entry.getKey().getRight(), Entry::getValue)
                ))
                // Flush the map from entityId to lastOccurrenceInstant of each {schema, entityType} pair.
                .forEach((pair, map) -> lastOccurrenceInstantWriter.writeAll(pair.getLeft(), pair.getRight(), map));
    }
}
