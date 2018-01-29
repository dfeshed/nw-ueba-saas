package fortscale.utils.store;

import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.store.record.StoreMetadata;
import fortscale.utils.time.TimeRange;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * StoreManager responsible for:
 * saving new or changed StoreMetadata records.
 * cleaning up collections based on ttl and cleanupInterval.
 * cleanup records in time range.
 */
public class StoreManager {
    private AppSpecificStoreMetadataStore appSpecificStoreMetadataStore;
    private Duration defaultTtl;
    private Duration defaultCleanupInterval;
    private Map<String, StoreManagerAware> storeNameToStoreManagerAware;
    private StoreMetadataRepository storeMetadataRepository;
    private Boolean executeTtlCleanup;

    public StoreManager(String appName, Collection<StoreManagerAware> storeManagerAwares, Duration defaultTtl, Duration defaultCleanupInterval, StoreMetadataRepository storeMetadataRepository, Boolean executeTtlCleanup) {
        this.defaultTtl = defaultTtl;
        this.defaultCleanupInterval = defaultCleanupInterval;
        this.storeMetadataRepository = storeMetadataRepository;
        this.executeTtlCleanup = executeTtlCleanup;
        appSpecificStoreMetadataStore = new AppSpecificStoreMetadataStore(appName, storeMetadataRepository);
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
     * Register StoreMetadata - storeName and collectionName
     *
     * @param storeName      store name
     * @param collectionName collection name
     */
    public void register(String storeName, String collectionName, StoreMetadataProperties storeMetadataProperties) {
        appSpecificStoreMetadataStore.save(storeName, collectionName, null, null, storeMetadataProperties.getProperties());
    }


    /**
     * Register StoreMetadata -  storeName, collectionName, ttl and cleanup interval.
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl
     * @param cleanupInterval clean up interval
     */
    public void registerWithTtl(String storeName, String collectionName, Duration ttl, Duration cleanupInterval, StoreMetadataProperties storeMetadataProperties) {
        appSpecificStoreMetadataStore.save(storeName, collectionName, ttl, cleanupInterval, storeMetadataProperties.getProperties());
    }

    /**
     * Register StoreMetadata - storeName and collectionName with default ttl and default cleanup interval.
     *
     * @param storeName      store name
     * @param collectionName collection name
     */
    public void registerWithTtl(String storeName, String collectionName, StoreMetadataProperties storeMetadataProperties) {
        appSpecificStoreMetadataStore.save(storeName, collectionName, defaultTtl, defaultCleanupInterval, storeMetadataProperties.getProperties());
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
    public void cleanupCollections(Map<String, String> properties, Instant instant) {
        if (executeTtlCleanup) {
            List<StoreMetadata> storeMetadataList = appSpecificStoreMetadataStore.getStoreDataList();
            storeMetadataList = storeMetadataList.stream().filter(storeMetadata -> storeMetadata.getProperties().equals(properties)).collect(Collectors.toList());
            storeMetadataList.forEach(storeMetadata -> {
                        StoreManagerAware storeManagerAware = storeNameToStoreManagerAware.get(storeMetadata.getStoreName());
                        String collectionName = storeMetadata.getCollectionName();
                        Duration ttl = storeMetadata.getTtlDuration();
                        Duration cleanupInterval = storeMetadata.getCleanupInterval();

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
    public void cleanupCollections(Map<String, String> properties, Instant start, Instant end) {
        List<StoreMetadata> storeMetadataList = appSpecificStoreMetadataStore.getStoreDataList();
        storeMetadataList = storeMetadataList.stream().filter(storeMetadata -> storeMetadata.getProperties().equals(properties)).collect(Collectors.toList());
        storeMetadataList.forEach(storeMetadata -> {
                    StoreManagerAware storeManagerAware = storeNameToStoreManagerAware.get(storeMetadata.getStoreName());
                    String collectionName = storeMetadata.getCollectionName();
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
                List<StoreMetadata> storeMetadataList = storeMetadataRepository.findByStoreName(storeName);
                StoreManagerAware storeManagerAware = storeNameToStoreManagerAware.get(storeName);
                storeMetadataList.forEach(storeMetadata -> {
                            String collectionName = storeMetadata.getCollectionName();
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
    public void cleanupCollections(String storeName, TimeRange timeRange, StoreMetadataProperties storeMetadataProperties) {
        List<StoreMetadata> storeMetadataList = storeMetadataRepository.findByStoreName(storeName);
        StoreManagerAware storeManagerAware = storeNameToStoreManagerAware.get(storeName);
        storeMetadataList = storeMetadataList.stream().filter(storeMetadata -> storeMetadata.getProperties().equals(storeMetadataProperties.getProperties())).collect(Collectors.toList());

        storeMetadataList.forEach(storeMetadata -> {
                    String collectionName = storeMetadata.getCollectionName();
                    storeManagerAware.remove(collectionName, timeRange.getStart(), timeRange.getEnd());
                }
        );
    }
}
