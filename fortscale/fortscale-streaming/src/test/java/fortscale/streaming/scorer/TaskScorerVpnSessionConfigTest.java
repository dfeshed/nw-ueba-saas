package fortscale.streaming.scorer;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import org.junit.Test;

import java.io.IOException;

public class TaskScorerVpnSessionConfigTest extends TaskScorerConfigTest{

	@Test
	public void testSanity() throws IOException{
		buildScorersFromTaskConfig("config/raw-events-prevalence-stats-task.properties", new StreamingTaskDataSourceConfigKey("vpn", "VPNSessionEventsFilterStreamTask"));
	}
}
