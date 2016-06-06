package fortscale.collection.jobs.activity;

import fortscale.domain.core.UserActivityJobState;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.TreeSet;

/**
 * Abstract class to provide basic functionality of user activity handlers
 *
 * @author gils
 * 31/05/2016
 */
@Configurable(preConstruction = true)
@Component
public abstract class UserActivityBaseHandler implements UserActivityHandler {
    static final String CONTEXT_ID_FIELD_NAME = "contextId";

    final static String CONTEXT_ID_USERNAME_PREFIX = "normalized_username###";
    static int CONTEXT_ID_USERNAME_PREFIX_LENGTH;

    final static int MONGO_READ_WRITE_BULK_SIZE = 10000;

    @Autowired
    protected MongoTemplate mongoTemplate;

    static {
        CONTEXT_ID_USERNAME_PREFIX_LENGTH = CONTEXT_ID_USERNAME_PREFIX.length();
    }

    protected UserActivityJobState loadAndUpdateJobState(int numOfLastDaysToCalculate) {
        Query query = new Query();
        UserActivityJobState userActivityJobState = mongoTemplate.findOne(query, UserActivityJobState.class);

        if (userActivityJobState == null) {
            userActivityJobState = new UserActivityJobState();
            userActivityJobState.setLastRun(System.currentTimeMillis());

            mongoTemplate.save(userActivityJobState, UserActivityJobState.COLLECTION_NAME);
        }
        else {
            Update update = new Update();
            update.set(UserActivityJobState.LAST_RUN_FIELD, System.currentTimeMillis());

            mongoTemplate.upsert(query, update, UserActivityJobState.class);

            TreeSet<Long> completedExecutionDays = userActivityJobState.getCompletedExecutionDays();

            long endTime = System.currentTimeMillis();
            long startingTime = TimeUtils.calculateStartingTime(endTime, numOfLastDaysToCalculate);

            completedExecutionDays.removeIf(a -> (a < startingTime));

            removeRelatedDocuments(startingTime);
        }

        return userActivityJobState;
    }

    protected abstract void removeRelatedDocuments(Object startingTime);

    protected abstract String getActivityName();
}
