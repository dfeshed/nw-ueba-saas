package fortscale.services.ad;


import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.AdTaskType;

import java.util.Map;
import java.util.UUID;

public interface AdTaskPersistencyService {
    String RESULTS_KEY_DELIMITER = "_";
    String EXECUTION_TIME_KEY_DELIMITER = "_";
    String RESULTS_DELIMITER = "=";
    String RESULTS_KEY_SUCCESS = "success";

    Map<String, String> getTaskResults(String resultsKey);

    void writeTaskResults(String dataSource, String taskName, String resultsId, boolean result);

    Long getLastExecutionTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource);

    void setLastExecutionTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource, Long lastExecutionTime);

    Long getExecutionStartTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource);

    void setExecutionStartTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource, Long executionStartTime);

    String createResultKey(AdObject.AdObjectType dataSource, AdTaskType adTaskType, UUID resultsId);
}
