package fortscale.aggregation.feature.bucket.repository.state;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureBucketStateRepository extends MongoRepository<FeatureBucketState, String>, FeatureBucketStateRepositoryCustom{
	FeatureBucketState findByStateType(FeatureBucketState.StateType stateType);
}
