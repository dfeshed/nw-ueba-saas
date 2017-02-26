package fortscale.services.ad;

import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.AdTaskType;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.BaseTaskPersistencyService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdTaskPersistencyServiceImpl extends BaseTaskPersistencyService implements AdTaskPersistencyService {

    private static final Logger logger = Logger.getLogger(AdTaskPersistencyServiceImpl.class);

    public static final String RESULTS_KEY_DELIMITER = "_";
    public static final String EXECUTION_TIME_KEY_DELIMITER = "_";

    private final String SYSTEM_SETUP_AD_LAST_EXECUTION_TIME_PREFIX ="system_setup_ad.last_execution_time";
    private final String SYSTEM_SETUP_AD_EXECUTION_START_TIME_PREFIX ="system_setup_ad.execution_start_time";

    @Autowired
    public AdTaskPersistencyServiceImpl(ApplicationConfigurationService applicationConfigurationService) {
        super(applicationConfigurationService);
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
