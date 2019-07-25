package fortscale.domain.lastoccurrenceinstant.reader;

import fortscale.common.general.Schema;
import fortscale.utils.data.LfuCache;
import org.apache.commons.lang3.Validate;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

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
    public Map<String, Instant> readAll(Schema schema, String entityType, Collection<String> entityIds) {
        return entityIds.stream().collect(toMap(identity(), entityId -> read(schema, entityType, entityId)));
    }

    private static String getKey(Schema schema, String entityType, String entityId) {
        return String.format("%s_%s_%s", schema.getName(), entityType, entityId);
    }
}
