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
     * @return get store name
     */
    default String getStoreName(){
        return getClass().getSimpleName();
    }
}

