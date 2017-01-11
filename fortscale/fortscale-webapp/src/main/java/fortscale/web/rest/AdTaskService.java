package fortscale.web.rest;


import fortscale.domain.ad.AdObject;
import fortscale.web.tasks.ControllerInvokedAdTask;

import java.util.Map;

public interface AdTaskService {

    Map<String, String> getTaskResults(String resultsKey);

    Long getLastExecutionTime(ControllerInvokedAdTask.AdTaskType adTaskType, AdObject.AdObjectType dataSource);

    void setLastExecutionTime(ControllerInvokedAdTask.AdTaskType adTaskType, AdObject.AdObjectType dataSource, Long lastExecutionTime);

    Long getExecutionStartTime(ControllerInvokedAdTask.AdTaskType adTaskType, AdObject.AdObjectType dataSource);

    void setExecutionStartTime(ControllerInvokedAdTask.AdTaskType adTaskType, AdObject.AdObjectType dataSource, Long executionStartTime);

}
