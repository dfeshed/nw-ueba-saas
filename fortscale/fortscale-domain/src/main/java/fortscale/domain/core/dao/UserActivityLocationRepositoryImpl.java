package fortscale.domain.core.dao;

import fortscale.domain.core.activities.UserActivity;
import fortscale.domain.core.activities.UserActivityLocationDocument;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository("UserActivityLocationRepository")
public class UserActivityLocationRepositoryImpl implements UserActivityLocationRepository {

    public static final String COLLECTION_NAME = UserActivityLocationDocument.COLLECTION_NAME;
    private final MongoTemplate mongoTemplate;
    private static final Logger logger = Logger.getLogger(UserActivityLocationRepositoryImpl.class);

    @Autowired
    public UserActivityLocationRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays) {
        List<UserActivityLocationDocument> userActivityLocationDocuments;
        if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
            Criteria idCriteria = Criteria.where(UserActivityLocationDocument.USER_NAME_FIELD_NAME).is(username);
            Criteria startTimeCriteria = Criteria.where(UserActivityLocationDocument.START_TIME_FIELD_NAME).gte(TimestampUtils.convertToSeconds(getStartTime(timeRangeInDays)));
            Query query = new Query(idCriteria.andOperator(startTimeCriteria));
            userActivityLocationDocuments = mongoTemplate.find(query, UserActivityLocationDocument.class, COLLECTION_NAME);
        }
        else {
            final String errorMessage = String.format("Could not find collection '%s' in database", COLLECTION_NAME);
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        return userActivityLocationDocuments;
    }

    private long getStartTime(int timeRangeInDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -timeRangeInDays);
        return TimestampUtils.toStartOfDay(calendar.getTime().getTime());
    }

    @Override
    public List<UserActivity> findAll() {
        return mongoTemplate.findAll(UserActivity.class);
    }
}
