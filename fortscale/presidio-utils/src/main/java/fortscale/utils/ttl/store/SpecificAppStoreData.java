package fortscale.utils.ttl.store;

import fortscale.utils.ttl.record.StoreData;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * SpecificAppStoreData use StoreData for get and save StoreData records.
 * On initiation, SpecificAppStoreData get StoreData list by application name.
 * SpecificAppStoreData save new records and update changed records.
 */
public class SpecificAppStoreData {

    //Map<storeName, Map<collectionName, StoreData>>
    private Map<String, Map<String, StoreData>> storeDataMap;
    private String appName;
    private StoreDataRepository storeDataRepository;


    public SpecificAppStoreData(String appName, StoreDataRepository storeDataRepository) {
        this.appName = appName;
        this.storeDataRepository = storeDataRepository;
        List<StoreData> storeDataList = storeDataRepository.findByApplicationName(appName);
        storeDataMap = storeDataList.stream().collect(groupingBy(storeData -> storeData.getStoreName(), toMap(storeData -> storeData.getCollectionName(), storeData -> storeData)));
    }

    /**
     * Save new StoreData records and update changed StoreData records.
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl duration of records
     * @param cleanupInterval cleanup interval
     */
    public void save(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        Map<String, StoreData> storeNameToTtlData = storeDataMap.get(storeName);
        if (storeNameToTtlData != null) {
            StoreData storeData = storeNameToTtlData.get(collectionName);
            if (storeData != null) {
                //update exist storeData if ttl or cleanupInterval changed
                if (!storeData.getTtlDuration().equals(ttl) || !storeData.getCleanupInterval().equals(cleanupInterval)) {
                    storeData.setTtlDuration(ttl);
                    storeData.setCleanupInterval(cleanupInterval);
                    storeDataRepository.save(storeData);
                }
            } else {
                //create new storeData if collection is not exist
                storeData = new StoreData(appName, storeName, collectionName, ttl, cleanupInterval);
                storeNameToTtlData.put(collectionName, storeData);
                storeDataRepository.save(storeData);
            }
        } else {
            //create new record if store is not exist in the Map.
            createNewStoreData(storeName, collectionName, ttl, cleanupInterval);
        }
    }

    /**
     * Save StoreData records without ttl and without cleanupInterval
     * @param storeName store name
     * @param collectionName collection name
     */
    public void save(String storeName, String collectionName) {
        Map<String, StoreData> storeNameToTtlData = storeDataMap.get(storeName);
        if (storeNameToTtlData != null) {
            StoreData storeData = storeNameToTtlData.get(collectionName);
            if (storeData == null) {
                //create new storeData if collection is not exist
                storeData = new StoreData(appName, storeName, collectionName, null, null);
                storeNameToTtlData.put(collectionName, storeData);
                storeDataRepository.save(storeData);
            }
        } else {
            //create new record if store is not exist in the Map.
            createNewStoreData(storeName, collectionName, null, null);
        }
    }

    /**
     * get StoreData list
     *
     * @return List<StoreData>
     */
    public List<StoreData> getStoreDataList() {
        return storeDataMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
    }


    /**
     * Create StoreData, update the storeDataList and add storeData record to the store.
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl
     * @param cleanupInterval cleanup interval
     */
    private void createNewStoreData(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        StoreData storeData = new StoreData(appName, storeName, collectionName, ttl, cleanupInterval);
        Map<String, StoreData> collectionToTtlData = new HashMap<>();
        collectionToTtlData.put(collectionName, storeData);
        storeDataMap.put(storeName, collectionToTtlData);
        storeDataRepository.save(storeData);
    }

}
