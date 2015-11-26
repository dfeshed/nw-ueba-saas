package fortscale.streaming.scorer;

import java.io.IOException;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import org.junit.Test;

public class TaskScorerVpnSessionConfigTest extends TaskScorerConfigTest{

	@Test
	public void testSanity() throws IOException{
		buildScorersFromTaskConfig("config/raw-events-prevalence-stats-task.properties", new StreamingTaskDataSourceConfigKey("vpn_session", "VPNSessionEventsFilterStreamTask"));
	}
}
