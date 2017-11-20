package fortscale.utils.ttl;


import java.time.Instant;

/**
 * stores, who use StoreManager should extends StoreManagerAware.
 */
public interface StoreManagerAware {

    /**
     * Set StoreManager
     *
     * @param storeManager
     */
    void setStoreManager(StoreManager storeManager);

    /**
     * Remove collection
     *
     * @param collectionName collectionName to remove
     * @param until          remove collection records until given instant.
     */
    void remove(String collectionName, Instant until);

    /**
     * Remove collection between start and end instants
     * @param collectionName collection name
     * @param start start instant
     * @param end end instant
     */
    void remove(String collectionName, Instant start, Instant end);

    /**
     * @return get store name
     */
    default String getStoreName(){
        return getClass().getSimpleName();
    }
}

