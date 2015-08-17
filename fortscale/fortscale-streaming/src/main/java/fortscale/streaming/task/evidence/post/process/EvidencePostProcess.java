package fortscale.streaming.task.evidence.post.process;

import fortscale.streaming.task.EvidenceCreationTask;
import net.minidev.json.JSONObject;

/**
 * Interface for running post evidence creation logic
 */
public interface EvidencePostProcess {
	JSONObject run(JSONObject message, EvidenceCreationTask.DataSourceConfiguration dataSourceConfiguration);
}
