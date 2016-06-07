package fortscale.domain.core.dao;

import fortscale.domain.core.UserActivity;
import fortscale.domain.core.UserActivityLocation;
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

    private static final Logger logger = Logger.getLogger(UserActivityLocationRepositoryImpl.class);

    public static final String COLLECTION_NAME = UserActivityLocation.COLLECTION_NAME;
    private static final String RESERVED_RANGE_COUNTRY_VALUE = "Reserved Range";

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserActivityLocationRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<UserActivityLocation> getUserActivityLocationEntries(String username, int timeRangeInDays) {
        List<UserActivityLocation> userActivityLocations;
        if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
            Criteria idCriteria = Criteria.where(UserActivityLocation.USER_NAME_FIELD_NAME).is(username);
            Criteria startTimeCriteria = Criteria.where(UserActivityLocation.START_TIME_FIELD_NAME).gte(TimestampUtils.convertToSeconds(getStartTime(timeRangeInDays)));
            Query query = new Query(idCriteria.andOperator(startTimeCriteria));
            userActivityLocations = mongoTemplate.find(query, UserActivityLocation.class, COLLECTION_NAME);
        }
        else {
            final String errorMessage = String.format("Could not find collection '%s' in database", COLLECTION_NAME);
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        filterUnknownCountries(userActivityLocations);

        return userActivityLocations;
    }

    private void filterUnknownCountries(List<UserActivityLocation> userActivityLocations) {
        userActivityLocations.forEach(a -> a.getLocations().getCountryHistogram().remove(RESERVED_RANGE_COUNTRY_VALUE));
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
