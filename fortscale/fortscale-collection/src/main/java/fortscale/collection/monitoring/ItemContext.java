package fortscale.collection.monitoring;

import fortscale.collection.morphlines.metrics.MorphlineMetrics;
import fortscale.streaming.task.monitor.TaskMonitoringHelper;

/**
 * Created by shays on 03/01/2016.
 */
public class ItemContext {

    private String sourceName;
    private TaskMonitoringHelper<String> taskMonitoringHelper;

	private MorphlineMetrics morphlineMetrics;

    public ItemContext() {
    }

    public ItemContext(String sourceName, TaskMonitoringHelper<String> taskMonitoringHelper,MorphlineMetrics morphlineMetrics) {
        this.sourceName = sourceName;
        this.taskMonitoringHelper = taskMonitoringHelper;
		this.morphlineMetrics = morphlineMetrics;
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

	public MorphlineMetrics getMorphlineMetrics() {
		return morphlineMetrics;
	}

	public void setMorphlineMetrics(MorphlineMetrics morphlineMetrics) {
		this.morphlineMetrics = morphlineMetrics;
	}
}
