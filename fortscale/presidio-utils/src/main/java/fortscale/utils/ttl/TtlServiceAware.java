package fortscale.utils.ttl;


import java.time.Instant;

/**
 * stores, who use Ttl service should extends TtlServiceAware.
 */
public interface TtlServiceAware {

    /**
     * Set tTtlService
     *
     * @param ttlService
     */
    void setTtlService(TtlService ttlService);

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

