package fortscale.aggregation.feature.event;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation of basic query functionality for aggregated events, based on mongo persistence
 *
 * @author Amir Keren
 * Date: 15/11/2015
 */
public class FeatureBucketQueryMongoService implements FeatureBucketQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(String featureName, String contextType, String ContextName, Long startTime, Long endTime) {
        String collectionName = featureName;
        if (mongoTemplate.collectionExists(collectionName)) {
            Criteria startTimeCriteria = Criteria.where(FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
            Criteria endTimeCriteria = Criteria.where(FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
            Criteria contextCriteria = createContextCriteria(contextType, ContextName);
            Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria,contextCriteria));
            return mongoTemplate.find(query, FeatureBucket.class, collectionName);
        }
        else {
            throw new RuntimeException("Could not fetch feature buckets from collection " + collectionName);
        }
    }

    private Criteria createContextCriteria(String contextType, String contextName) {
        Map<String, String> contextMap = new HashMap(1);
        contextMap.put(contextType, contextName);
        return Criteria.where(FeatureBucket.CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD).in(contextMap);
    }
}
