package fortscale.utils.ttl.store;

import fortscale.utils.ttl.record.StoreData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreDataRepository extends MongoRepository<StoreData, String> {
    /**
     * Get StoreData records by application name
     * @param applicationName application name
     * @return List<StoreData>
     */
    List<StoreData> findByApplicationName(String applicationName);

    /**
     * Get StoreData records by store name
     * @param storeName store name
     * @return List<StoreData>
     */
    List<StoreData> findByStoreName(String storeName);
}

