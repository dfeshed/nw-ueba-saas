package fortscale.utils.ttl.store;

import fortscale.utils.ttl.record.TtlData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TtlDataRepository extends MongoRepository<TtlData, String> {
    /**
     * Get TtlData records by application name
     * @param applicationName application name
     * @return List<TtlData>
     */
    List<TtlData> findByApplicationNameIn(String applicationName);

    /**
     * Get TtlData records by store name
     * @param storeName application name
     * @return List<TtlData>
     */
    List<TtlData> findByStoreNameIn(String storeName);
}

