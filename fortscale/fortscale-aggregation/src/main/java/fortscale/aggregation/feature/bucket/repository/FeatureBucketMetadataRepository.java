package fortscale.aggregation.feature.bucket.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FeatureBucketMetadataRepository extends MongoRepository<FeatureBucketMetadata, String>, FeatureBucketMetadataRepositoryCustom {
	List<FeatureBucketMetadata> findByIsSyncedFalseAndEndTimeLessThan(long epochtime);
}
