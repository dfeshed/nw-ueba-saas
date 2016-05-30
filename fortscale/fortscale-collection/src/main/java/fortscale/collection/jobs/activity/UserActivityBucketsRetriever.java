package fortscale.collection.jobs.activity;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.domain.core.UserActivityLocation;
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

    private final static String USER_ACTIVITY_LOCATIONS_COLLECTION = "user_activity_locations";

    public List<UserActivityLocation> retrieve(String dataSource, Long startTime, Long endTime) {
        if (mongoTemplate.collectionExists(USER_ACTIVITY_LOCATIONS_COLLECTION)) {
            Criteria startTimeCriteria = Criteria.where(fortscale.aggregation.feature.bucket.FeatureBucket.START_TIME_FIELD).gte(TimestampUtils.convertToSeconds(startTime));
            Criteria endTimeCriteria = Criteria.where(fortscale.aggregation.feature.bucket.FeatureBucket.END_TIME_FIELD).lte(TimestampUtils.convertToSeconds(endTime));
            Query query = new Query(startTimeCriteria.andOperator(endTimeCriteria));
            return mongoTemplate.find(query, UserActivityLocation.class, USER_ACTIVITY_LOCATIONS_COLLECTION);
        }
        else {
            throw new RuntimeException("Could not find collection with name " + USER_ACTIVITY_LOCATIONS_COLLECTION);
        }
    }
}
