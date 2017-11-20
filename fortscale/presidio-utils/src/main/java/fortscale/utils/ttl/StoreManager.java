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
 * StoreManager responsible for:
 * saving new or changed TtlData records.
 * cleaning up collections based on ttl and cleanupInterval.
 */
public class StoreManager {
    private AppSpecificTtlDataStore appSpecificTtlDataStore;
    private Duration defaultTtl;
    private Duration defaultCleanupInterval;
    private Map<String, StoreManagerAware> storeNameToStoreManagerAware;
    private TtlDataRepository ttlDataRepository;
    private Boolean executeTtlCleanup;

    public StoreManager(String appName, Collection<StoreManagerAware> storeManagerAwares, Duration defaultTtl, Duration defaultCleanupInterval, TtlDataRepository ttlDataRepository, Boolean executeTtlCleanup) {
        this.defaultTtl = defaultTtl;
        this.defaultCleanupInterval = defaultCleanupInterval;
        this.ttlDataRepository = ttlDataRepository;
        this.executeTtlCleanup =  executeTtlCleanup;
        appSpecificTtlDataStore = new AppSpecificTtlDataStore(appName, ttlDataRepository);
        buildStoreNameToStoreManagerAwareMap(storeManagerAwares);
    }

    /**
     * Build store name to StoreManagerAware map.
     * Set the StoreManager for each StoreManagerAware.
     *
     * @param storeManagerAwares storeManagerAware stores
     */
    private void buildStoreNameToStoreManagerAwareMap(Collection<StoreManagerAware> storeManagerAwares) {
        storeNameToStoreManagerAware = new HashMap<>();

        storeManagerAwares.forEach((storeManagerAware) -> {
            storeManagerAware.setStoreManager(this);
            storeNameToStoreManagerAware.put(storeManagerAware.getStoreName(), storeManagerAware);
        });
    }

    /**
     * Register TtlData
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl
     * @param cleanupInterval clean up interval
     */
    public void registerWithTtl(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        appSpecificTtlDataStore.save(storeName, collectionName, ttl, cleanupInterval);
    }

    /**
     *  Register TtlData - storeName and collectionName with default ttl and default cleanup.
     *
     * @param storeName      store name
     * @param collectionName collection name
     */
    public void registerWithTtl(String storeName, String collectionName) {
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
        if(executeTtlCleanup) {
            List<TtlData> ttlDataList = appSpecificTtlDataStore.getTtlDataList();
            ttlDataList.forEach(ttlData -> {
                        StoreManagerAware storeManagerAware = storeNameToStoreManagerAware.get(ttlData.getStoreName());
                        String collectionName = ttlData.getCollectionName();

                        Duration ttl = ttlData.getTtlDuration();
                        Duration cleanupInterval = ttlData.getCleanupInterval();

                        if (instant.getEpochSecond() % cleanupInterval.getSeconds() == 0) {
                            storeManagerAware.remove(collectionName, instant.minus(ttl));
                        }
                    }
            );
        }
    }


    /**
     * Cleanup all collections of the given Store until the given instant according to ttl and cleanupInterval.
     *
     * @param storeName       store name
     * @param until           remove records until the given instant according to ttl and cleanupInterval.
     * @param ttl             logical duration to store records
     * @param cleanupInterval cleanup interval
     */
    public void cleanupCollections(String storeName, Instant until, Duration ttl, Duration cleanupInterval) {
        if(executeTtlCleanup) {
            if (until.getEpochSecond() % cleanupInterval.getSeconds() == 0) {
                List<TtlData> ttlDataList = ttlDataRepository.findByStoreName(storeName);
                StoreManagerAware storeManagerAware = storeNameToStoreManagerAware.get(storeName);
                ttlDataList.forEach(ttlData -> {
                            String collectionName = ttlData.getCollectionName();
                            storeManagerAware.remove(collectionName, until.minus(ttl));
                        }
                );
            }
        }
    }
}
