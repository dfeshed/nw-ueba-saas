package fortscale.aggregation.feature.bucket.repository.state;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

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
