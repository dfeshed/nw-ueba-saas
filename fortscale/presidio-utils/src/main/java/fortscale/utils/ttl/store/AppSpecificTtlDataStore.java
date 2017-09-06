package fortscale.utils.ttl.store;

import fortscale.utils.ttl.record.TtlData;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/**
 * AppSpecificTtlDataStore use ttlDataStore for get and save ttlData records.
 * On initiation, AppSpecificTtlDataStore get ttlData list by application name.
 * AppSpecificTtlDataStore save new records and update changed records.
 */
public class AppSpecificTtlDataStore {

    //Map<storeName, Map<collectionName, TtlData>>
    private Map<String, Map<String, TtlData>> ttlDataMap;
    private String appName;
    private TtlDataStore ttlDataStore;


    public AppSpecificTtlDataStore(String appName, TtlDataStore ttlDataStore) {
        this.appName = appName;
        this.ttlDataStore = ttlDataStore;
        List<TtlData> ttlDataList = ttlDataStore.getTtlDataList(appName);
        ttlDataMap = ttlDataList.stream().collect(groupingBy(ttlData -> ttlData.getStoreName(), toMap(ttlData -> ttlData.getCollectionName(), ttlData -> ttlData)));
    }

    /**
     * Save new ttlData records and update changed ttlData records.
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl duration of records
     * @param cleanupInterval cleanup interval
     */
    public void save(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        if (!ttlDataMap.isEmpty()) {
            Map<String, TtlData> storeNameToTtlData = ttlDataMap.get(storeName);
            if (storeNameToTtlData != null) {
                TtlData ttlData = storeNameToTtlData.get(collectionName);
                if (ttlData != null) {
                    //update exist ttlData if ttl or cleanupInterval changed
                    if (!ttlData.getTtlDuration().equals(ttl) || !ttlData.getCleanupInterval().equals(cleanupInterval)) {
                        ttlData.setTtlDuration(ttl);
                        ttlData.setCleanupInterval(cleanupInterval);
                        ttlDataStore.save(ttlData);
                    }
                } else {
                    ttlData = new TtlData(appName, storeName, collectionName, ttl, cleanupInterval);
                    ttlDataMap.get(storeName).put(collectionName, ttlData);
                    ttlDataStore.save(ttlData);
                }
            } else {
                //create new record if store is not exist in the Map.
                createNewTtlData(storeName, collectionName, ttl, cleanupInterval);
            }
        } else {
            //create new record if ttlDataMap is empty
            createNewTtlData(storeName, collectionName, ttl, cleanupInterval);
        }
    }

    /**
     * get ttlData list
     *
     * @return List<TtlData>
     */
    public List<TtlData> getTtlDataList() {
        return ttlDataMap.values().stream().flatMap(m -> m.values().stream()).collect(Collectors.toList());
    }


    /**
     * Create TtlData, update the ttlDataList and add ttlData record to the store.
     *
     * @param storeName       store name
     * @param collectionName  collection name
     * @param ttl             ttl
     * @param cleanupInterval cleanup interval
     */
    private void createNewTtlData(String storeName, String collectionName, Duration ttl, Duration cleanupInterval) {
        TtlData ttlData = new TtlData(appName, storeName, collectionName, ttl, cleanupInterval);
        Map<String, TtlData> collectionToTtlData = new HashMap<>();
        collectionToTtlData.put(collectionName, ttlData);
        ttlDataMap.put(storeName, collectionToTtlData);
        ttlDataStore.save(ttlData);
    }

}
