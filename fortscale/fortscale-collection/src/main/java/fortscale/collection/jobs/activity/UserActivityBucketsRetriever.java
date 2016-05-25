package fortscale.collection.jobs.activity;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gils
 * 24/05/2016
 */
@Configurable(preConstruction = true)
@Component
public class UserActivityBucketsRetriever implements UserActivityRawDataRetriever<FeatureBucket> {

    private static final String HYPHEN = "_";

    @Autowired
    private MongoTemplate mongoTemplate;

    private final static String USER_AGGREGATION_COLLECTION_PREFIX = "aggr_normalized_username";
    private final static String BUCKET_TIMEFRAME_SUFFIX = "daily";

    public List<FeatureBucket> retrieve(String dataSource, Long startTime, Long endTime) {
        String collectionName = extractCollectionName(dataSource);
        if (mongoTemplate.collectionExists(collectionName)) {
            Criteria startTimeCriteria = Criteria.where(fortscale.aggregation.feature.bucket.FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
            Criteria endTimeCriteria = Criteria.where(fortscale.aggregation.feature.bucket.FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
            Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria));
            return mongoTemplate.find(query, FeatureBucket.class, collectionName);
        }
        else {
            throw new RuntimeException("Could not fetch feature buckets from collection " + collectionName);
        }
    }

    private String extractCollectionName(String dataSource) {
        return USER_AGGREGATION_COLLECTION_PREFIX + HYPHEN + dataSource + HYPHEN + BUCKET_TIMEFRAME_SUFFIX;
    }
}
