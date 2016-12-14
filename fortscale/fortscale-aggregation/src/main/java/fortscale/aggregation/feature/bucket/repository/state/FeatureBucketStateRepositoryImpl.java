package fortscale.aggregation.feature.bucket.repository.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by alexp on 13/12/16.
 */
public class FeatureBucketStateRepositoryImpl implements FeatureBucketStateRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void dosomeStuffupdateFeatureBucketState(FeatureBucketState featureBucketState) {
        mongoTemplate.save(featureBucketState);
    }
}
