package fortscale.services.users.tagging;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserTaggingTaskPersistencyServiceImpl implements UserTaggingTaskPersistenceService {

    private static final Logger logger = Logger.getLogger(UserTaggingTaskPersistencyServiceImpl.class);

    public static final String RESULTS_DELIMITER = "=";
    public static final String RESULTS_KEY_SUCCESS = "success";
    public static final String RESULTS_KEY_NAME = "user_tagging";

    private final String SYSTEM_SETUP_USER_TAGGING_LAST_EXECUTION_TIME_PREFIX ="system_setup_user_tagging.last_execution_time";
    private final String SYSTEM_SETUP_USER_TAGGING_EXECUTION_START_TIME_PREFIX ="system_setup_user_tagging.execution_start_time";

    private final ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    public UserTaggingTaskPersistencyServiceImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public Map<String, String> getTaskResults(String resultsKey) {
        Map<String, String> taskResults = new HashMap<>();
        logger.info("getting result for key {}", resultsKey);
        ApplicationConfiguration queryResult = applicationConfigurationService.getApplicationConfiguration(resultsKey);
        if (queryResult == null) {
            logger.error("No result found for result key {}", resultsKey);
            taskResults.put(RESULTS_KEY_SUCCESS, Boolean.FALSE.toString());
            return taskResults;
        }

        final String taskExecutionResult = queryResult.getValue();
        final String[] split = taskExecutionResult.split(RESULTS_DELIMITER);
        final String key = split[0];
        final String value = split[1];
        taskResults.put(key, value);
        if (applicationConfigurationService.delete(resultsKey) == 0) {
            logger.warn("Failed to delete query result with key {}.", resultsKey);
        }

        return taskResults;
    }

    public void writeTaskResults(String taskTypeName, String resultsId, boolean result) {
        String resultsKey = createResultKey(resultsId);
        logger.debug("Inserting status to application configuration in key {}", resultsKey);
        applicationConfigurationService.insertConfigItem(resultsKey, RESULTS_KEY_SUCCESS + RESULTS_DELIMITER + result);
    }

    public Long getLastExecutionTime() {
        return applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_USER_TAGGING_LAST_EXECUTION_TIME_PREFIX, Long.class);
    }

    public void setLastExecutionTime(Long lastExecutionTime) {
        applicationConfigurationService.updateConfigItemAsObject(SYSTEM_SETUP_USER_TAGGING_LAST_EXECUTION_TIME_PREFIX, lastExecutionTime);
    }

    public Long getExecutionStartTime() {
        return applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_USER_TAGGING_EXECUTION_START_TIME_PREFIX, Long.class);
    }

    public void setExecutionStartTime(Long executionStartTime) {
        applicationConfigurationService.updateConfigItemAsObject(SYSTEM_SETUP_USER_TAGGING_EXECUTION_START_TIME_PREFIX, executionStartTime);
    }

    @Override
    public String createResultKey(UUID resultsId) {
        return createResultKey(resultsId.toString());
    }

    private String createResultKey(String resultsId) {
        return  String.format("%s%s%s", RESULTS_KEY_NAME, applicationConfigurationService.getKeyDelimiter(),resultsId);
    }
}
