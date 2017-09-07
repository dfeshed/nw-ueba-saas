package fortscale.utils.ttl;

import fortscale.utils.ttl.record.TtlData;
import fortscale.utils.ttl.store.AppSpecificTtlDataStore;
import fortscale.utils.ttl.store.TtlDataRepository;

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
    private TtlDataRepository ttlDataRepository;

    public TtlService(String appName, Collection<TtlServiceAware> ttlServiceAwares, Duration defaultTtl, Duration defaultCleanupInterval, TtlDataRepository ttlDataRepository) {
        this.defaultTtl = defaultTtl;
        this.defaultCleanupInterval = defaultCleanupInterval;
        this.ttlDataRepository = ttlDataRepository;
        appSpecificTtlDataStore = new AppSpecificTtlDataStore(appName, ttlDataRepository);
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
     * @param instant remove records until the given instant according to ttl and cleanupInterval.
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


    /**
     * Cleanup all collections of the given Store until the given instant according to ttl and cleanupInterval.
     *
     * @param StoreName store name
     * @param instant remove records until the given instant according to ttl and cleanupInterval.
     * @param ttl logical duration to store records
     * @param cleanupInterval cleanup interval
     */
    public void cleanupCollections(String StoreName, Instant instant, Duration ttl, Duration cleanupInterval) {
        List<TtlData> ttlDataList = ttlDataRepository.findByStoreNameIn(StoreName);
        TtlServiceAware ttlServiceAware = storeToTtlServiceAware.get(StoreName);
        ttlDataList.forEach(ttlData -> {
                    String collectionName = ttlData.getCollectionName();
                    if (instant.getEpochSecond() % cleanupInterval.getSeconds() == 0) {
                        ttlServiceAware.remove(collectionName, instant.minus(ttl));
                    }
                }
        );
    }
}
