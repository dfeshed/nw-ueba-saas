package fortscale.domain.lastoccurrenceinstant.reader;

import fortscale.common.general.Schema;
import fortscale.utils.data.LfuCache;
import org.apache.commons.lang3.Validate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class LastOccurrenceInstantReaderCacheImpl implements LastOccurrenceInstantReader {
    private final LastOccurrenceInstantReader lastOccurrenceInstantReader;
    private final LfuCache<String, Instant> lfuCache;
    private final double entriesToRemovePercentage;

    /**
     * Constructor.
     *
     * @param lastOccurrenceInstantReader The underlying store from which information is read.
     * @param maximumSize                 The maximum number of entries of this cache.
     * @param entriesToRemovePercentage   The percentage of entries to remove when this cache is full.
     */
    public LastOccurrenceInstantReaderCacheImpl(
            LastOccurrenceInstantReader lastOccurrenceInstantReader,
            int maximumSize, double entriesToRemovePercentage) {

        Validate.notNull(lastOccurrenceInstantReader, "lastOccurrenceInstantReader cannot be null.");
        Validate.isTrue(maximumSize > 0, "maximumSize must be greater than zero but is %d.", maximumSize);
        LfuCache.assertPercentage(entriesToRemovePercentage);
        this.lastOccurrenceInstantReader = lastOccurrenceInstantReader;
        this.lfuCache = new LfuCache<>(maximumSize);
        this.entriesToRemovePercentage = entriesToRemovePercentage;
    }

    @Override
    public Instant read(Schema schema, String entityType, String entityId) {
        String key = getKey(schema, entityType, entityId);

        if (lfuCache.containsKey(key)) {
            return lfuCache.get(key);
        } else {
            if (lfuCache.isFull()) lfuCache.removeLfuEntries(entriesToRemovePercentage);
            Instant lastOccurrenceInstant = lastOccurrenceInstantReader.read(schema, entityType, entityId);
            lfuCache.put(key, lastOccurrenceInstant);
            return lastOccurrenceInstant;
        }
    }

    @Override
    public Map<String, Instant> readAll(Schema schema, String entityType, List<String> entityIds) {
        Map<String, Instant> entityIdToLastOccurrenceInstantMap = new HashMap<>();
        // The missingEntityIds and missingKeys lists are guaranteed to have the same size.
        // Using an ArrayList in both cases guarantees element access in O(1) complexity time.
        List<String> missingEntityIds = new ArrayList<>();
        List<String> missingKeys = new ArrayList<>();

        for (String entityId : entityIds) {
            String key = getKey(schema, entityType, entityId);

            if (lfuCache.containsKey(key)) {
                entityIdToLastOccurrenceInstantMap.put(entityId, lfuCache.get(key));
            } else {
                missingEntityIds.add(entityId);
                missingKeys.add(key);
            }
        }

        entityIdToLastOccurrenceInstantMap.putAll(readAllMissing(schema, entityType, missingEntityIds, missingKeys));
        return entityIdToLastOccurrenceInstantMap;
    }

    private Map<String, Instant> readAllMissing(
            Schema schema, String entityType, List<String> missingEntityIds, List<String> missingKeys) {

        Map<String, Instant> map = lastOccurrenceInstantReader.readAll(schema, entityType, missingEntityIds);
        int difference = map.size() - lfuCache.calculateFreeSpace();
        if (difference > 0) lfuCache.removeLfuEntries(difference);
        IntStream.range(0, map.size()).forEach(i -> lfuCache.put(missingKeys.get(i), map.get(missingEntityIds.get(i))));
        return map;
    }

    private static String getKey(Schema schema, String entityType, String entityId) {
        return String.format("%s_%s_%s", schema.getName(), entityType, entityId);
    }
}
