package fortscale.services.users.tagging;


import java.util.UUID;

public interface UserTaggingTaskPersistenceService {

    void writeTaskResults(String taskName, String resultsId, boolean result);

    Long getLastExecutionTime();

    void setLastExecutionTime(Long lastExecutionTime);

    Long getExecutionStartTime();

    void setExecutionStartTime(Long executionStartTime);

    String createResultKey(UUID resultsId);
}
