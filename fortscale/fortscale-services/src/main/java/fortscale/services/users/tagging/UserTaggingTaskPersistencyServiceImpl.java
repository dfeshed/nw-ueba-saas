package fortscale.services.users.tagging;

import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserTaggingTaskPersistencyServiceImpl implements UserTaggingTaskPersistenceService {

    private static final Logger logger = Logger.getLogger(UserTaggingTaskPersistencyServiceImpl.class);
    public static final String FILE_CONF_KEY = "file_conf";

    private final String SYSTEM_SETUP_USER_TAGGING_LAST_EXECUTION_TIME ="system_setup_user_tagging.last_execution_time";
    private final String SYSTEM_SETUP_USER_TAGGING_EXECUTION_START_TIME ="system_setup_user_tagging.execution_start_time";

    private final ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    public UserTaggingTaskPersistencyServiceImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public UserTaggingResult getTaskResults(String resultsKey) {
        return applicationConfigurationService.getApplicationConfigurationAsObject(createResultKey(resultsKey), UserTaggingResult.class);
    }

    public void writeTaskResults(String taskTypeName, String resultsId, boolean result, Map<String, Long> deltaPerTag, String errorMessage) {
        String resultsKey = createResultKey(resultsId);
        logger.debug("Inserting status to application configuration in key {}", resultsKey);
        UserTaggingResult userTaggingResult = new UserTaggingResult(result, deltaPerTag, errorMessage);
        applicationConfigurationService.insertOrUpdateConfigItemAsObject(resultsKey, userTaggingResult);
    }

    public Long getLastExecutionTime() {
        return applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_USER_TAGGING_LAST_EXECUTION_TIME, Long.class);
    }

    public void setLastExecutionTime(Long lastExecutionTime) {
        applicationConfigurationService.updateConfigItemAsObject(SYSTEM_SETUP_USER_TAGGING_LAST_EXECUTION_TIME, lastExecutionTime);
    }

    public Long getExecutionStartTime() {
        return applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_USER_TAGGING_EXECUTION_START_TIME, Long.class);
    }

    public void setExecutionStartTime(Long executionStartTime) {
        applicationConfigurationService.updateConfigItemAsObject(SYSTEM_SETUP_USER_TAGGING_EXECUTION_START_TIME, executionStartTime);
    }

    @Override
    public String createResultKey(String resultsId) {
        return  String.format("%s%s%s", RESULTS_KEY_NAME, applicationConfigurationService.getKeyDelimiter(), resultsId);
    }

    @Override
    public String getSystemSetupUserTaggingFilePath() {
        return applicationConfigurationService.getApplicationConfigurationAsObject(createResultKey(FILE_CONF_KEY), String.class);
    }

    @Override
    public void saveSystemSetupTaggingFilePath(String filePath) {
        applicationConfigurationService.insertOrUpdateConfigItemAsObject(createResultKey(FILE_CONF_KEY), filePath);
    }

    @Override
    public void deleteSystemSetupTaggingFilePath() {
        applicationConfigurationService.delete(createResultKey(FILE_CONF_KEY));
    }

    public static class UserTaggingResult{
        private String errorMessage;
        private boolean success;
        private Map<String, Long> usersAffected;

        public UserTaggingResult() {
        }

        public UserTaggingResult(boolean success, Map<String, Long> usersAffected, String errorMessage) {
            this.success = success;
            this.usersAffected = usersAffected;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() {
            return success;
        }

        public Map<String, Long> getUsersAffected() {
            return usersAffected;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
