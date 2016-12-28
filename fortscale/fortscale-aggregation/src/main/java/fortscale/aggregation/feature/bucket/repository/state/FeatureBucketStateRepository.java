package fortscale.aggregation.feature.bucket.repository.state;

import fortscale.aggregation.feature.bucket.state.FeatureBucketState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureBucketStateRepository extends MongoRepository<FeatureBucketState, String>, FeatureBucketStateRepositoryCustom{
}
