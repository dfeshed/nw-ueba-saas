package fortscale.services.ad;

import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.AdTaskType;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AdTaskPersistencyServiceImpl implements AdTaskPersistencyService {

    private static final Logger logger = Logger.getLogger(AdTaskPersistencyServiceImpl.class);

    public static final String RESULTS_DELIMITER = "=";
    public static final String RESULTS_KEY_DELIMITER = "_";
    public static final String EXECUTION_TIME_KEY_DELIMITER = "_";
    public static final String RESULTS_KEY_SUCCESS = "success";

    private final String SYSTEM_SETUP_AD_LAST_EXECUTION_TIME_PREFIX ="system_setup_ad.last_execution_time";
    private final String SYSTEM_SETUP_AD_EXECUTION_START_TIME_PREFIX ="system_setup_ad.execution_start_time";
    private final ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    public AdTaskPersistencyServiceImpl(ApplicationConfigurationService applicationConfigurationService) {
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

    public void writeTaskResults(String dataSource, String taskTypeName, String resultsId, boolean result) {
        String resultsKey = createResultKey(dataSource, taskTypeName, resultsId);
        logger.debug("Inserting status to application configuration in key {}", resultsKey);
        applicationConfigurationService.insertConfigItem(resultsKey, RESULTS_KEY_SUCCESS + RESULTS_DELIMITER + result);
    }

    public Long getLastExecutionTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource) {
        return applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_AD_LAST_EXECUTION_TIME_PREFIX + EXECUTION_TIME_KEY_DELIMITER + adTaskType + EXECUTION_TIME_KEY_DELIMITER + dataSource.toString(), Long.class);
    }

    public void setLastExecutionTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource, Long lastExecutionTime) {
        applicationConfigurationService.updateConfigItemAsObject(SYSTEM_SETUP_AD_LAST_EXECUTION_TIME_PREFIX + EXECUTION_TIME_KEY_DELIMITER + adTaskType + EXECUTION_TIME_KEY_DELIMITER + dataSource.toString(), lastExecutionTime);
    }

    public Long getExecutionStartTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource) {
        return applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_AD_EXECUTION_START_TIME_PREFIX + EXECUTION_TIME_KEY_DELIMITER + adTaskType + EXECUTION_TIME_KEY_DELIMITER + dataSource.toString(), Long.class);
    }

    public void setExecutionStartTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource, Long executionStartTime) {
        applicationConfigurationService.updateConfigItemAsObject(SYSTEM_SETUP_AD_EXECUTION_START_TIME_PREFIX + EXECUTION_TIME_KEY_DELIMITER + adTaskType + EXECUTION_TIME_KEY_DELIMITER + dataSource.toString(), executionStartTime);
    }

    @Override
    public String createResultKey(AdObject.AdObjectType dataSource, AdTaskType adTaskType, UUID resultsId) {
        return createResultKey(dataSource.name(), adTaskType.getType(), resultsId.toString());
    }

    private String createResultKey(String dataSource, String taskTypeName, String resultsId) {
        return dataSource.toLowerCase() + RESULTS_KEY_DELIMITER + taskTypeName.toLowerCase() + applicationConfigurationService.getKeyDelimiter() + resultsId;

    }

}
