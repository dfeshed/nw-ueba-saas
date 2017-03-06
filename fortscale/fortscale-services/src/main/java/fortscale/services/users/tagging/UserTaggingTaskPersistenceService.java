package fortscale.services.users.tagging;


import fortscale.domain.rest.SystemSetupFileConf;

import java.util.Map;

public interface UserTaggingTaskPersistenceService {
    String USER_TAGGING_RESULT_ID = "result";
    String RESULTS_KEY_NAME = "user_tagging";

    UserTaggingTaskPersistencyServiceImpl.UserTaggingResult getTaskResults(String resultsKey);

    void writeTaskResults(String taskName, String resultsId, boolean result, Map<String, Long> deltaPerTag);

    Long getLastExecutionTime();

    void setLastExecutionTime(Long lastExecutionTime);

    Long getExecutionStartTime();

    void setExecutionStartTime(Long executionStartTime);

    String createResultKey(String resultsId);

    SystemSetupFileConf getSystemSetupFileConf();

    void saveSystemSetupFileConf(SystemSetupFileConf systemSetupFileConf);

    void deleteSystemSetupFileConf();
}
