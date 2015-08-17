package fortscale.streaming.task.evidence.pre.process;

import fortscale.streaming.task.EvidenceCreationTask;
import net.minidev.json.JSONObject;

/**
 * Interface for running pre evidence creation logic
 */
public interface EvidencePreProcess {

	JSONObject run(JSONObject message, EvidenceCreationTask.DataSourceConfiguration dataSourceConfiguration);
}
