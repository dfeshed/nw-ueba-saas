package fortscale.services.ad;


import fortscale.domain.ad.AdObject;
import fortscale.domain.ad.AdTaskType;

import java.util.UUID;

public interface AdTaskPersistencyService {

    void writeTaskResults(String dataSource, String taskName, String resultsId, boolean result);

    Long getLastExecutionTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource);

    void setLastExecutionTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource, Long lastExecutionTime);

    Long getExecutionStartTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource);

    void setExecutionStartTime(AdTaskType adTaskType, AdObject.AdObjectType dataSource, Long executionStartTime);

    String createResultKey(AdObject.AdObjectType dataSource, AdTaskType adTaskType, UUID resultsId);
}
