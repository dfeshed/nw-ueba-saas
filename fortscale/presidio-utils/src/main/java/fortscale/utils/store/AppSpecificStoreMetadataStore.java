package fortscale.utils.store;

import fortscale.utils.store.record.StoreMetadata;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * AppSpecificStoreMetadataStore uses StoreMetadataRepository for get and save StoreMetadata records.
 * On initiation, AppSpecificStoreMetadataStore get StoreMetadata list by application name.
 * AppSpecificStoreMetadataStore save new records and update changed records.
 */
public class AppSpecificStoreMetadataStore {

    //Map<storeName, Map<collectionName, StoreMetadata>>
    private Map<String, Map<String, StoreMetadata>> StoreMetadataMap;
    private String appName;
    private StoreMetadataRepository storeMetadataRepository;


    public AppSpecificStoreMetadataStore(String appName, StoreMetadataRepository storeMetadataRepository) {
        this.appName = appName;
        this.storeMetadataRepository = storeMetadataRepository;
        List<StoreMetadata> storeMetadataList = storeMetadataRepository.findByApplicationName(appName);
        StoreMetadataMap = storeMetadataList.stream().collect(groupingBy(storeMetadata -> storeMetadata.getStoreName(), toMap(storeMetadata -> storeMetadata.getCollectionName(), storeMetadata -> storeMetadata)));
    }

    /**
     * Save new StoreMetadata records and update changed StoreMetadata records.
     * Null values are available for properties in order to save collections without properties.
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl duration of records
     * @param cleanupInterval cleanup interval
     */
    public void save(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        Map<String, StoreMetadata> storeNameToStoreMetadata = StoreMetadataMap.get(storeName);
        if (storeNameToStoreMetadata != null) {
            StoreMetadata storeMetadata = storeNameToStoreMetadata.get(collectionName);
            if (storeMetadata != null) {
                //update exist storeMetadata if ttl or cleanupInterval changed
                if (!isPropertiesEqual(storeMetadata, ttl, cleanupInterval)) {
                    storeMetadata.setTtlDuration(ttl);
                    storeMetadata.setCleanupInterval(cleanupInterval);
                    storeMetadataRepository.save(storeMetadata);
                }
            } else {
                //create new storeMetadata if collection is not exist
                storeMetadata = new StoreMetadata(appName, storeName, collectionName, ttl, cleanupInterval);
                storeNameToStoreMetadata.put(collectionName, storeMetadata);
                storeMetadataRepository.save(storeMetadata);
            }
        } else {
            //create new record if store is not exist in the Map.
            createNewStoreData(storeName, collectionName, ttl, cleanupInterval);
        }
    }

    /**
     * is ttl and cleanupInterval equals to storeMetadata properties, include null values.
     *
     * @param storeMetadata   storeMetadata
     * @param ttl             ttl
     * @param cleanupInterval cleanupInterval
     * @return is ttl and cleanupInterval equals to storeMetadata properties
     */
    public boolean isPropertiesEqual(StoreMetadata storeMetadata, Duration ttl, Duration cleanupInterval) {
        if (ttl != null && cleanupInterval != null && storeMetadata.getTtlDuration() != null && storeMetadata.getCleanupInterval() != null) {
            if (!storeMetadata.getTtlDuration().equals(ttl) || !storeMetadata.getCleanupInterval().equals(cleanupInterval)) {
                return false;
            }
        } else {
            if (storeMetadata.getTtlDuration() != ttl || storeMetadata.getCleanupInterval() != cleanupInterval) {
                return false;
            }
        }

        return true;
    }


    /**
     * get StoreMetadata list
     *
     * @return List<StoreMetadata>
     */
    public List<StoreMetadata> getStoreDataList() {
        return StoreMetadataMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
    }


    /**
     * Create StoreMetadata, update the StoreMetadataMap and add storeData record to the store.
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl
     * @param cleanupInterval cleanup interval
     */
    private void createNewStoreData(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        StoreMetadata storeMetadata = new StoreMetadata(appName, storeName, collectionName, ttl, cleanupInterval);
        Map<String, StoreMetadata> collectionToStoreMetadata = new HashMap<>();
        collectionToStoreMetadata.put(collectionName, storeMetadata);
        StoreMetadataMap.put(storeName, collectionToStoreMetadata);
        storeMetadataRepository.save(storeMetadata);
    }

}
