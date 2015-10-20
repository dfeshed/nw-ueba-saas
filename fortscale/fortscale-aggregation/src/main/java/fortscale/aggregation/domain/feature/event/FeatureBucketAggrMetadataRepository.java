package fortscale.aggregation.domain.feature.event;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeatureBucketAggrMetadataRepository extends MongoRepository<FeatureBucketAggrMetadata, String>, FeatureBucketAggrMetadataRepositoryCustom{
	public List<FeatureBucketAggrMetadata> findByEndTimeLessThan(Long endTime);
}
