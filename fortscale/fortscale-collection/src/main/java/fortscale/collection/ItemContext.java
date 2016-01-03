package fortscale.collection;

import fortscale.streaming.task.monitor.TaskMonitoringHelper;

/**
 * Created by shays on 03/01/2016.
 */
public class ItemContext {

    private String sourceName;
    private TaskMonitoringHelper<String> taskMonitoringHelper;

    public ItemContext(String sourceName, TaskMonitoringHelper<String> taskMonitoringHelper) {
        this.sourceName = sourceName;
        this.taskMonitoringHelper = taskMonitoringHelper;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public TaskMonitoringHelper<String> getTaskMonitoringHelper() {
        return taskMonitoringHelper;
    }

    public void setTaskMonitoringHelper(TaskMonitoringHelper<String> taskMonitoringHelper) {
        this.taskMonitoringHelper = taskMonitoringHelper;
    }
}
