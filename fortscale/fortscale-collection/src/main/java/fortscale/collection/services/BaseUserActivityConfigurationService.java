package fortscale.collection.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

public abstract class BaseUserActivityConfigurationService implements UserActivityConfigurationService {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	protected ApplicationConfigurationService applicationConfigurationService;

	public void afterPropertiesSet() throws Exception {
		if (applicationConfigurationService != null) {
			saveUserActivityConfigurationToDatabase();
		}
		else {
			throw new RuntimeException(String.format("Failed to inject ApplicationConfigurationService. User Activity %s Configuration was not set" , getActivityName()));
		}
	}

	public UserActivityConfiguration getUserActivityConfigurationFromDatabase() {
		final Optional<String> optionalUserActivityConfiguration = applicationConfigurationService.getApplicationConfigurationAsString(getConfigurationKey());
		if (optionalUserActivityConfiguration.isPresent()) {
			try {
				return objectMapper.readValue(optionalUserActivityConfiguration.get(), UserActivityConfiguration.class);
			} catch (IOException e) {
				throw new RuntimeException(String.format("Failed to get user activity %s from database", getActivityName()), e);
			}
		}
		else {
			throw new RuntimeException(String.format("Failed to get user activity %s from database. Got empty response", getActivityName()));
		}
	}

	public void saveUserActivityConfigurationToDatabase() throws JsonProcessingException {
		UserActivityConfiguration userActivityConfiguration = createUserActivityConfiguration();
		String userActivityConfigurationAsJsonString = objectMapper.writeValueAsString(userActivityConfiguration);
		//TODO: replace the saving. Saving as JSON might not work with the UI configuration. Need to be tested
		applicationConfigurationService.insertConfigItem(getConfigurationKey(), userActivityConfigurationAsJsonString);
	}

	@Override
	public UserActivityConfiguration getUserActivityConfiguration() {
		try {
			return getUserActivityConfigurationFromDatabase();
		} catch (RuntimeException e) {
			getLogger().error(e.getLocalizedMessage());
			throw e;
		}
	}

	public abstract Logger getLogger();

	protected abstract String getActivityName();

	protected abstract String getConfigurationKey();

	public abstract UserActivityConfiguration createUserActivityConfiguration();
}
