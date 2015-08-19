package fortscale.streaming.task;

import fortscale.streaming.task.EvidenceCreationTask;
import net.minidev.json.JSONObject;

/**
 * Interface for running pre evidence creation logic
 */
public interface EvidenceProcessor{

	void run(JSONObject message, EvidenceCreationTask.DataSourceConfiguration dataSourceConfiguration);
}
