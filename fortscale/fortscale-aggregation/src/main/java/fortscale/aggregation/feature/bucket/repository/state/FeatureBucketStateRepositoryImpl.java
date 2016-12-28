package fortscale.aggregation.feature.bucket.repository.state;

import fortscale.aggregation.feature.bucket.state.FeatureBucketState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Created by alexp on 13/12/16.
 */
public class FeatureBucketStateRepositoryImpl implements FeatureBucketStateRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public FeatureBucketState getState() {
        return mongoTemplate.findOne(new Query(), FeatureBucketState.class);
    }
}
