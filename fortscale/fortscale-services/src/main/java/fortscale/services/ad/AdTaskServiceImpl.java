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

@Service
public class AdTaskServiceImpl implements AdTaskService {

    private static final Logger logger = Logger.getLogger(AdTaskServiceImpl.class);

    public static final String RESULTS_DELIMITER = "=";
    public static final String RESULTS_KEY_SUCCESS = "success";

    private final String SYSTEM_SETUP_AD_LAST_EXECUTION_TIME_PREFIX ="system_setup_ad.last_execution_time";
    private final String SYSTEM_SETUP_AD_EXECUTION_START_TIME_PREFIX ="system_setup_ad.execution_start_time";

    private final ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    public AdTaskServiceImpl(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    @Override
    public Map<String, String> getTaskResults(String resultsKey) {
        Map<String, String> taskResults = new HashMap<>();
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

    @Override
    public void writeTaskResults(String resultsKey, String value) {
        applicationConfigurationService.insertConfigItem(resultsKey, value);
    }

    public Long getLastExecutionTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource) {
        return applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_AD_LAST_EXECUTION_TIME_PREFIX + "_" + adTaskType + "_" + dataSource.toString(), Long.class);
    }

    public void setLastExecutionTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource, Long lastExecutionTime) {
        applicationConfigurationService.updateConfigItemAsObject(SYSTEM_SETUP_AD_LAST_EXECUTION_TIME_PREFIX + "_" + adTaskType + "_" + dataSource.toString(), lastExecutionTime);
    }

    public Long getExecutionStartTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource) {
        return applicationConfigurationService.getApplicationConfigurationAsObject(SYSTEM_SETUP_AD_EXECUTION_START_TIME_PREFIX + "_" + adTaskType + "_" + dataSource.toString(), Long.class);
    }

    public void setExecutionStartTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource, Long executionStartTime) {
        applicationConfigurationService.updateConfigItemAsObject(SYSTEM_SETUP_AD_EXECUTION_START_TIME_PREFIX + "_" + adTaskType + "_" + dataSource.toString(), executionStartTime);
    }
}
