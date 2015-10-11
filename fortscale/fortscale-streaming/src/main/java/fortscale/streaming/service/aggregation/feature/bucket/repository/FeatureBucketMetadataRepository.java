package fortscale.streaming.service.aggregation.feature.bucket.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeatureBucketMetadataRepository extends MongoRepository<FeatureBucketMetadata, String>, FeatureBucketMetadataRepositoryCustom{
	public List<FeatureBucketMetadata> findByisSyncedFalseAndEndTimeLessThan(long epochtime);
	
	

}
