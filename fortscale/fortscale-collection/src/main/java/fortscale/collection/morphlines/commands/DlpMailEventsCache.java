package fortscale.collection.morphlines.commands;


import fortscale.utils.logging.Logger;
import org.kitesdk.morphline.api.Record;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DlpMailEventsCache {
    private static final Logger logger = Logger.getLogger(DlpMailEventsCache.class);

    public static final String EVENT_ID_FIELD_NAME = "event_id";

    private Map<String, List<Record>> cache = new HashMap<>();

    public DlpMailEventsCache() {
    }

    public int getCacheSize() {
        return cache.size();
    }

    public void addRecord(Record record) throws Exception {
        final String eventId = (String) record.getFirstValue(EVENT_ID_FIELD_NAME);
        final Set<String> existingEventsIds = cache.keySet();
        final boolean eventInCache = existingEventsIds.contains(eventId);
        if (existingEventsIds.size() == 2 && !eventInCache) {
            final String msg = String.format("Failed to add record with event id %s. Can't have more than two events (more than two different event ids) in the cache", eventId);
            logger.error(msg);
            throw new Exception(msg);
        }

        if (eventInCache) {
            logger.debug("Adding record to cache for new eventId {}", eventId);
            final List<Record> currRecordsForEventId = cache.get(eventId);
            currRecordsForEventId.add(record);
        }
        else {
            logger.debug("Adding record to cache for new eventId {}", eventId);
            cache.put(eventId, Stream.of(record).collect(Collectors.toCollection(ArrayList::new)));
        }
    }


    public Map.Entry<String, List<Record>> popPreviousEventRecords(Record record) throws Exception {
        final String newRecordEventId = (String) record.getFirstValue(EVENT_ID_FIELD_NAME);
        final Set<Map.Entry<String, List<Record>>> existingEntries = cache.entrySet();
        for (Map.Entry<String, List<Record>> existingEntry : existingEntries) {
            final String currEventId = existingEntry.getKey();
            if (!currEventId.equals(newRecordEventId)) {
                logger.debug("Clearing cache from events for eventId {}", currEventId);
                cache.remove(currEventId);
                return existingEntry;
            }
        }

        final String msg = String.format("Tried to get previous records but there were no records in the cache that don't belong to the current event id %s", newRecordEventId);
        logger.error(msg);
        throw new Exception(msg);
    }

    public boolean isCachedRecord(Record record) {
        final String eventId = (String) record.getFirstValue(EVENT_ID_FIELD_NAME);
        final List<Record> recordsForEventId = cache.get(eventId);
        return recordsForEventId != null && recordsForEventId.size() > 1;
    }
}
