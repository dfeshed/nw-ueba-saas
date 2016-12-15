package fortscale.aggregation.feature.bucket.repository.state;

import org.springframework.cglib.core.Predicate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureBucketStateRepository extends MongoRepository<FeatureBucketState, String>, FeatureBucketStateRepositoryCustom{
}
