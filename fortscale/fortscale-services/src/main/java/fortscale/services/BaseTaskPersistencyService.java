package fortscale.services;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexp on 26/02/2017.
 */
public abstract class BaseTaskPersistencyService {
    private static final Logger logger = Logger.getLogger(BaseTaskPersistencyService.class);
    public static final String RESULTS_DELIMITER = "=";
    public static final String RESULTS_KEY_SUCCESS = "success";

    protected final ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    public BaseTaskPersistencyService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

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
}
