package fortscale.utils.store;

import fortscale.utils.store.record.StoreMetadata;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * AppSpecificStoreMetadataStore use StoreMetadata for get and save StoreMetadata records.
 * On initiation, AppSpecificStoreMetadataStore get StoreMetadata list by application name.
 * AppSpecificStoreMetadataStore save new records and update changed records.
 */
public class AppSpecificStoreMetadataStore {

    //Map<storeName, Map<collectionName, StoreMetadata>>
    private Map<String, Map<String, StoreMetadata>> storeDataMap;
    private String appName;
    private StoreMetadataRepository storeMetadataRepository;


    public AppSpecificStoreMetadataStore(String appName, StoreMetadataRepository storeMetadataRepository) {
        this.appName = appName;
        this.storeMetadataRepository = storeMetadataRepository;
        List<StoreMetadata> storeMetadataList = storeMetadataRepository.findByApplicationName(appName);
        storeDataMap = storeMetadataList.stream().collect(groupingBy(storeMetadata -> storeMetadata.getStoreName(), toMap(storeMetadata -> storeMetadata.getCollectionName(), storeMetadata -> storeMetadata)));
    }

    /**
     * Save new StoreMetadata records and update changed StoreMetadata records.
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl duration of records
     * @param cleanupInterval cleanup interval
     */
    public void save(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        Map<String, StoreMetadata> storeNameToTtlData = storeDataMap.get(storeName);
        if (storeNameToTtlData != null) {
            StoreMetadata storeMetadata = storeNameToTtlData.get(collectionName);
            if (storeMetadata != null) {
                //update exist storeMetadata if ttl or cleanupInterval changed
                if (!storeMetadata.getTtlDuration().equals(ttl) || !storeMetadata.getCleanupInterval().equals(cleanupInterval)) {
                    storeMetadata.setTtlDuration(ttl);
                    storeMetadata.setCleanupInterval(cleanupInterval);
                    storeMetadataRepository.save(storeMetadata);
                }
            } else {
                //create new storeMetadata if collection is not exist
                storeMetadata = new StoreMetadata(appName, storeName, collectionName, ttl, cleanupInterval);
                storeNameToTtlData.put(collectionName, storeMetadata);
                storeMetadataRepository.save(storeMetadata);
            }
        } else {
            //create new record if store is not exist in the Map.
            createNewStoreData(storeName, collectionName, ttl, cleanupInterval);
        }
    }

    /**
     * Save StoreMetadata records without ttl and without cleanupInterval
     * @param storeName store name
     * @param collectionName collection name
     */
    public void save(String storeName, String collectionName) {
        Map<String, StoreMetadata> storeNameToTtlData = storeDataMap.get(storeName);
        if (storeNameToTtlData != null) {
            StoreMetadata storeMetadata = storeNameToTtlData.get(collectionName);
            if (storeMetadata == null) {
                //create new storeMetadata if collection is not exist
                storeMetadata = new StoreMetadata(appName, storeName, collectionName, null, null);
                storeNameToTtlData.put(collectionName, storeMetadata);
                storeMetadataRepository.save(storeMetadata);
            }
        } else {
            //create new record if store is not exist in the Map.
            createNewStoreData(storeName, collectionName, null, null);
        }
    }

    /**
     * get StoreMetadata list
     *
     * @return List<StoreMetadata>
     */
    public List<StoreMetadata> getStoreDataList() {
        return storeDataMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
    }


    /**
     * Create StoreMetadata, update the storeDataList and add storeData record to the store.
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl
     * @param cleanupInterval cleanup interval
     */
    private void createNewStoreData(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        StoreMetadata storeMetadata = new StoreMetadata(appName, storeName, collectionName, ttl, cleanupInterval);
        Map<String, StoreMetadata> collectionToTtlData = new HashMap<>();
        collectionToTtlData.put(collectionName, storeMetadata);
        storeDataMap.put(storeName, collectionToTtlData);
        storeMetadataRepository.save(storeMetadata);
    }

}
