package fortscale.collection.jobs.activity;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * @author gils
 * 24/05/2016
 */
public interface UserActivityHandler {
    void handle(long startTime, long endTime, UserActivityConfigurationService userActivityConfigurationService, MongoTemplate mongoTemplate);
    String getActivityName();
}
