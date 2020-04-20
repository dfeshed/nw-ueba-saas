package fortscale.utils.store;

import fortscale.utils.store.record.StoreMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreMetadataRepository extends MongoRepository<StoreMetadata, String> {
    /**
     * Get StoreMetadata records by application name
     * @param applicationName application name
     * @return List<StoreMetadata>
     */
    List<StoreMetadata> findByApplicationName(String applicationName);

    /**
     * Get StoreMetadata records by store name
     * @param storeName store name
     * @return List<StoreMetadata>
     */
    List<StoreMetadata> findByStoreName(String storeName);
}

