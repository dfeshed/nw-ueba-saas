package fortscale.collection.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

public abstract class BaseUserActivityConfigurationService implements UserActivityConfigurationService {

    private static final Logger logger = Logger.getLogger(BaseUserActivityConfigurationService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public abstract UserActivityConfiguration createUserActivityConfiguration();

}