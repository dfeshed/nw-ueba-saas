package fortscale.collection.services;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface UserActivityConfigurationService {

    UserActivityConfiguration getUserActivityConfigurationFromDatabase();
    void saveUserActivityConfigurationToDatabase() throws JsonProcessingException;
    UserActivityConfiguration createUserActivityConfiguration();
    UserActivityConfiguration getUserActivityConfiguration();
    String getActivityName();

}