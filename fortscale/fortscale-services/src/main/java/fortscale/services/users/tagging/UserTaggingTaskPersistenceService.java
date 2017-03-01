package fortscale.services.users.tagging;


import java.util.Map;
import java.util.UUID;

public interface UserTaggingTaskPersistenceService {
    UserTaggingTaskPersistencyServiceImpl.UserTaggingResult getTaskResults(String resultsKey);

    void writeTaskResults(String taskName, String resultsId, boolean result, Map<String, Long> deltaPerTag);

    Long getLastExecutionTime();

    void setLastExecutionTime(Long lastExecutionTime);

    Long getExecutionStartTime();

    void setExecutionStartTime(Long executionStartTime);

    String createResultKey(UUID resultsId);

    Boolean isMonitorFileDaily();
}
