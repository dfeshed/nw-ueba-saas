package fortscale.utils.ttl;

import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.record.StoreData;
import fortscale.utils.ttl.store.SpecificAppStoreData;
import fortscale.utils.ttl.store.StoreDataRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StoreManager responsible for:
 * saving new or changed StoreData records.
 * cleaning up collections based on ttl and cleanupInterval.
 */
public class StoreManager {
    private SpecificAppStoreData specificAppStoreData;
    private Duration defaultTtl;
    private Duration defaultCleanupInterval;
    private Map<String, StoreManagerAware> storeNameToStoreManagerAware;
    private StoreDataRepository storeDataRepository;
    private Boolean executeTtlCleanup;

    public StoreManager(String appName, Collection<StoreManagerAware> storeManagerAwares, Duration defaultTtl, Duration defaultCleanupInterval, StoreDataRepository storeDataRepository, Boolean executeTtlCleanup) {
        this.defaultTtl = defaultTtl;
        this.defaultCleanupInterval = defaultCleanupInterval;
        this.storeDataRepository = storeDataRepository;
        this.executeTtlCleanup = executeTtlCleanup;
        specificAppStoreData = new SpecificAppStoreData(appName, storeDataRepository);
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
     * Register StoreData
     *
     * @param storeName      store name
     * @param collectionName collection name
     */
    public void register(String storeName, String collectionName) {
        specificAppStoreData.save(storeName, collectionName);
    }


    /**
     * Register StoreData
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl
     * @param cleanupInterval clean up interval
     */
    public void registerWithTtl(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        specificAppStoreData.save(storeName, collectionName, ttl, cleanupInterval);
    }

    /**
     * Register StoreData - storeName and collectionName with default ttl and default cleanup.
     *
     * @param storeName      store name
     * @param collectionName collection name
     */
    public void registerWithTtl(String storeName, String collectionName) {
        specificAppStoreData.save(storeName, collectionName, defaultTtl, defaultCleanupInterval);
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
        if (executeTtlCleanup) {
            List<StoreData> storeDataList = specificAppStoreData.getStoreDataList();
            storeDataList.forEach(storeData -> {
                        StoreManagerAware storeManagerAware = storeNameToStoreManagerAware.get(storeData.getStoreName());
                        String collectionName = storeData.getCollectionName();

                        Duration ttl = storeData.getTtlDuration();
                        Duration cleanupInterval = storeData.getCleanupInterval();

                        if (instant.getEpochSecond() % cleanupInterval.getSeconds() == 0) {
                            storeManagerAware.remove(collectionName, instant.minus(ttl));
                        }
                    }
            );
        }
    }


    /**
     * cleanup collections in time range(start, end).
     * @param start start instant
     * @param end end instant
     */
    public void cleanupCollections(Instant start, Instant end) {
        List<StoreData> storeDataList = specificAppStoreData.getStoreDataList();
        storeDataList.forEach(storeData -> {
                    StoreManagerAware storeManagerAware = storeNameToStoreManagerAware.get(storeData.getStoreName());
                    String collectionName = storeData.getCollectionName();
                    storeManagerAware.remove(collectionName, start, end);
                }
        );
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
        if (executeTtlCleanup) {
            if (until.getEpochSecond() % cleanupInterval.getSeconds() == 0) {
                List<StoreData> storeDataList = storeDataRepository.findByStoreName(storeName);
                StoreManagerAware storeManagerAware = storeNameToStoreManagerAware.get(storeName);
                storeDataList.forEach(storeData -> {
                            String collectionName = storeData.getCollectionName();
                            storeManagerAware.remove(collectionName, until.minus(ttl));
                        }
                );
            }
        }
    }

    /**
     * Cleanup  all collections of the given Store in mentioned time range.
     * @param storeName store name
     * @param timeRange timeRange
     */
    public void cleanupCollections(String storeName, TimeRange timeRange) {
        List<StoreData> storeDataList = storeDataRepository.findByStoreName(storeName);
        StoreManagerAware storeManagerAware = storeNameToStoreManagerAware.get(storeName);
        storeDataList.forEach(storeData -> {
                    String collectionName = storeData.getCollectionName();
                    storeManagerAware.remove(collectionName, timeRange.getStart(), timeRange.getEnd());
                }
        );
    }
}
