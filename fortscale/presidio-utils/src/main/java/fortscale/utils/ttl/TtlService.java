package fortscale.utils.ttl;

import fortscale.utils.ttl.record.TtlData;
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
    private String appName;
    private TtlDataStore ttlDataStore;
    private Duration defaultTtl;
    private Duration defaultCleanupInterval;
    private Map<String, TtlServiceAware> storeToTtlServiceAware;

    public TtlService(String appName, Collection<TtlServiceAware> ttlServiceAwares, TtlDataStore ttlDataStore, Duration defaultTtl, Duration defaultCleanupInterval) {
        this.appName = appName;
        this.ttlDataStore = ttlDataStore;
        this.defaultTtl = defaultTtl;
        this.defaultCleanupInterval = defaultCleanupInterval;
        buildStoreToTtlServiceAwareMap(ttlServiceAwares);
    }

    /**
     * Build store to ttlServiceAware map.
     * Set the ttlService for each ttlServiceAware.
     * @param ttlServiceAwares ttlServiceAware stores
     */
    private void buildStoreToTtlServiceAwareMap(Collection<TtlServiceAware> ttlServiceAwares) {
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
        ttlDataStore.save(storeName, collectionName, ttl, cleanupInterval);
    }

    /**
     * TtlData with default ttl and cleanupInterval.
     * @param storeName store name
     * @param collectionName collection name
     */
    public void save(String storeName, String collectionName) {
        ttlDataStore.save(storeName, collectionName, defaultTtl, defaultCleanupInterval);
    }

    /**
     * Remove collections based on ttl and cleanupInterval.
     * (e.g: cleanupInterval = PT24H => Remove collections every 24 hours.
     * ttl = PT48H => records stored for 48 hours).
     *
     * @param instant (startInstant, endInstant)
     */
    public void cleanupCollections(Instant instant) {
        List<TtlData> ttlDataList = ttlDataStore.getTtlDataList();
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
