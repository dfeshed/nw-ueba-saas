package fortscale.services.users.tagging;

import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserTaggingTaskPersistencyServiceImpl implements UserTaggingTaskPersistenceService {

    private static final Logger logger = Logger.getLogger(UserTaggingTaskPersistencyServiceImpl.class);

    public static final String RESULTS_KEY_NAME = "user_tagging";

    private final String SYSTEM_SETUP_USER_TAGGING_LAST_EXECUTION_TIME ="system_setup_user_tagging.last_execution_time";
    private final String SYSTEM_SETUP_USER_TAGGING_EXECUTION_START_TIME ="system_setup_user_tagging.execution_start_time";
    private final String SYSTEM_SETUP_USER_TAGGING_MONITOR_FILE_DAILY ="system_setup_user_tagging.monitor_file_daily";

    private final ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    public UserTaggingTaskPersistencyServiceImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public UserTaggingResult getTaskResults(String resultsKey) {
        return applicationConfigurationService.getApplicationConfigurationAsObject(createResultKey(resultsKey), UserTaggingResult.class);
    }

    public void writeTaskResults(String taskTypeName, String resultsId, boolean result, Map<String, Long> deltaPerTag) {
        String resultsKey = createResultKey(resultsId);
        logger.debug("Inserting status to application configuration in key {}", resultsKey);
        UserTaggingResult userTaggingResult = new UserTaggingResult(result, deltaPerTag);
        applicationConfigurationService.insertConfigItemAsObject(resultsKey, userTaggingResult);
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
    public Boolean isMonitorFileDaily() {
        return Boolean.valueOf(applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_USER_TAGGING_MONITOR_FILE_DAILY, Boolean.class));
    }

    public static class UserTaggingResult{
        private boolean success;
        private Map<String, Long> usersAffected;

        public UserTaggingResult() {
        }

        public UserTaggingResult(boolean success, Map<String, Long> usersAffected) {
            this.success = success;
            this.usersAffected = usersAffected;
        }

        public boolean isSuccess() {
            return success;
        }

        public Map<String, Long> getUsersAffected() {
            return usersAffected;
        }
    }
}
