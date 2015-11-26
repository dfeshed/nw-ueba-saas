package fortscale.streaming.scorer;

import java.io.IOException;

import org.junit.Test;

public class TaskScorerVpnSessionConfigTest extends TaskScorerConfigTest{

	@Test
	public void testSanity() throws IOException{
		buildScorersFromTaskConfig("config/raw-events-prevalence-stats-task.properties", "vpn_session");
	}
}
