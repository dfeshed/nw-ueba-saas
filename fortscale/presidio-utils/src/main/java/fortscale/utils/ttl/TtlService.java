package fortscale.utils.ttl;

import fortscale.utils.ttl.record.TtlData;
import fortscale.utils.ttl.store.AppSpecificTtlDataStore;
import fortscale.utils.ttl.store.TtlDataStore;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TtlService responsible for:
 * saving new or changed TtlData records.
 * cleaning up collections based on ttl and cleanupInterval.
 */
public class TtlService {
    private AppSpecificTtlDataStore appSpecificTtlDataStore;
    private Duration defaultTtl;
    private Duration defaultCleanupInterval;
    private Map<String, TtlServiceAware> storeToTtlServiceAware;

    public TtlService(String appName, Collection<TtlServiceAware> ttlServiceAwares, Duration defaultTtl, Duration defaultCleanupInterval, TtlDataStore ttlDataStore) {
        this.defaultTtl = defaultTtl;
        this.defaultCleanupInterval = defaultCleanupInterval;
        appSpecificTtlDataStore = new AppSpecificTtlDataStore(appName, ttlDataStore);
        buildStoreNameToTtlServiceAwareMap(ttlServiceAwares);
    }

    /**
     * Build store to ttlServiceAware map.
     * Set the ttlService for each ttlServiceAware.
     * @param ttlServiceAwares ttlServiceAware stores
     */
    private void buildStoreNameToTtlServiceAwareMap(Collection<TtlServiceAware> ttlServiceAwares) {
        storeToTtlServiceAware = new HashMap<>();

        ttlServiceAwares.forEach((ttlServiceAware) -> {
            ttlServiceAware.setTtlService(this);
            storeToTtlServiceAware.put(ttlServiceAware.getStoreName(),ttlServiceAware);
        });
    }

    /**
     * Save TtlData
     * @param storeName store name
     * @param collectionName collection name
     * @param ttl ttl
     * @param cleanupInterval clean up interval
     */
    public void save(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        appSpecificTtlDataStore.save(storeName, collectionName, ttl, cleanupInterval);
    }

    /**
     * TtlData with default ttl and cleanupInterval.
     * @param storeName store name
     * @param collectionName collection name
     */
    public void save(String storeName, String collectionName) {
        appSpecificTtlDataStore.save(storeName, collectionName, defaultTtl, defaultCleanupInterval);
    }

    /**
     * Remove collections based on ttl and cleanupInterval.
     * e.g:
     * 1. cleanupInterval = PT24H => Remove collections every 24 logical hours.
     * 2. ttl = PT48H => records of 48 logical hours are stored.
     *
     * @param instant logical time, which store use to remove records until the instant minus ttl.
     *                e.g: startInstant => store remove all records, where startInstant is less than (startInstant - tll)
     *                e.g: endInstant => store remove all records, where endInstant is less or equal than (endInstant - tll)
     */
    public void cleanupCollections(Instant instant) {
        List<TtlData> ttlDataList = appSpecificTtlDataStore.getTtlDataList();
        ttlDataList.forEach(ttlData -> {
                    TtlServiceAware ttlServiceAware = storeToTtlServiceAware.get(ttlData.getStoreName());
                    String collectionName = ttlData.getCollectionName();

                    Duration ttl = ttlData.getTtlDuration();
                    Duration cleanupInterval = ttlData.getCleanupInterval();

                    if (instant.getEpochSecond() % cleanupInterval.getSeconds() == 0) {
                        ttlServiceAware.remove(collectionName, instant.minus(ttl));
                    }
                }
        );
    }
}
