package fortscale.utils.ttl.store;

import fortscale.utils.ttl.record.TtlData;

import java.time.Duration;
import java.util.*;

/**
 * TtlDataStore use ttlDataRecordsStore for get and save ttlData records.
 * On initiation, TtlDataStore get ttlData list by application name.
 * TtlDataStore save new and changed records.
 */
public class TtlDataStore {

    private List<TtlData> ttlDataList;
    private String appName;
    private TtlDataRecordsStore ttlDataRecordsStore;


    public TtlDataStore(String appName, TtlDataRecordsStore ttlDataRecordsStore) {
        this.appName = appName;
        this.ttlDataRecordsStore = ttlDataRecordsStore;
        //get ttlData list by appName
        ttlDataList = ttlDataRecordsStore.findTtlData(appName);

        if (ttlDataList == null) {
            ttlDataList = new ArrayList<>();
        }
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
        if (!ttlDataList.isEmpty()) {
            // get TtlData by storeName and collectionName
            Optional<TtlData> ttlDataOptional = ttlDataList.stream().filter(t -> t.getStoreName().equals(storeName) && t.getCollectionName().equals(collectionName)).findFirst();
            if (ttlDataOptional.isPresent()) {
                TtlData ttlData = ttlDataOptional.get();
                //update exist ttlData if ttl or cleanupInterval changed
                if (!ttlData.getTtlDuration().equals(ttl) || !ttlData.getCleanupInterval().equals(cleanupInterval)) {
                    ttlData.setTtlDuration(ttl);
                    ttlData.setCleanupInterval(cleanupInterval);
                    ttlDataRecordsStore.save(ttlData);
                }
            } else {
                //create new record if ttlData is not exist.
                createNewTtlData(storeName, collectionName, ttl, cleanupInterval);
            }
        } else {
            //create new record if ttlDataList is empty
            createNewTtlData(storeName, collectionName, ttl, cleanupInterval);
        }
    }

    /**
     * get ttlData list
     *
     * @return List<TtlData>
     */
    public List<TtlData> getTtlDataList() {
        return ttlDataList;
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
        // if ttlDataList
        TtlData ttlData = new TtlData(appName, storeName, collectionName, ttl, cleanupInterval);
        ttlDataList.add(ttlData);
        ttlDataRecordsStore.save(ttlData);
    }

}
