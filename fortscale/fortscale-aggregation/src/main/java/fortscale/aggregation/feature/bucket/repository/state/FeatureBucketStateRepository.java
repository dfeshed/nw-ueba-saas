package fortscale.aggregation.feature.bucket.repository.state;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeatureBucketStateRepository extends MongoRepository<FeatureBucketState, String>, FeatureBucketStateRepositoryCustom{
}
