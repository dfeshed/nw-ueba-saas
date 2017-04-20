package fortscale.collection.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

public abstract class BaseUserActivityConfigurationService implements UserActivityConfigurationService {

    private static final Logger logger = Logger.getLogger(BaseUserActivityConfigurationService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    protected Map<String, UserActivityDataSourceConfiguration> activityDataSourceConfigurationMap = new HashMap<>();

    @Autowired
    protected ApplicationConfigurationService applicationConfigurationService;

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        if (applicationConfigurationService != null) {
            saveUserActivityConfigurationToDatabase();
        } else {
            throw new RuntimeException(String.format("Failed to inject ApplicationConfigurationService. User Activity %s Configuration was not set", getActivityName()));
        }
    }

    public UserActivityConfiguration getUserActivityConfigurationFromDatabase() {
        final Optional<String> optionalUserActivityConfiguration = applicationConfigurationService.
                getApplicationConfigurationAsString(getConfigurationKey());
        if (optionalUserActivityConfiguration.isPresent()) {
            try {
                return objectMapper.readValue(optionalUserActivityConfiguration.get(), UserActivityConfiguration.class);
            } catch (IOException e) {
                throw new RuntimeException(String.format("Failed to get user activity %s from database",
                        getActivityName()), e);
            }
        } else {
            throw new RuntimeException(String.format("Failed to get user activity %s from database. Got empty response",
                    getActivityName()));
        }
    }

    public void saveUserActivityConfigurationToDatabase() throws JsonProcessingException {
        UserActivityConfiguration userActivityConfiguration = createUserActivityConfiguration();
        String userActivityConfigurationAsJsonString = objectMapper.writeValueAsString(userActivityConfiguration);
        final String configurationKey = getConfigurationKey();
        applicationConfigurationService.delete(configurationKey);
        applicationConfigurationService.insertConfigItem(configurationKey, userActivityConfigurationAsJsonString);
    }

    @Override
    public UserActivityConfiguration getUserActivityConfiguration() {
        try {
            return getUserActivityConfigurationFromDatabase();
        } catch (RuntimeException e) {
            logger.error(e.getLocalizedMessage());
            throw e;
        }
    }

    @Override
    public abstract String getActivityName();

    protected abstract String getConfigurationKey();

    public UserActivityConfiguration createUserActivityConfiguration() {
        final Set<String> activities = new HashSet<>();
        final Map<String, String> dataSourceToCollection = new HashMap<>();
        final Map<String, List<String>> activityToDataSources = new HashMap<>();

        for (UserActivityDataSourceConfiguration activity : activityDataSourceConfigurationMap.values()) {
            activities.add(activity.getPropertyName());
            dataSourceToCollection.put(activity.getDatasource(), activity.getCollectionName());
            activityToDataSources.put(activity.getPropertyName(), new ArrayList<>(Collections.singletonList(activity.getDatasource())));
        }


        return new UserActivityConfiguration(activities, dataSourceToCollection, activityToDataSources);
    }

}