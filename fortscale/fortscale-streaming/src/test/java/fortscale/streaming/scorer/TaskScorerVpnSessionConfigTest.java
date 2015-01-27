package fortscale.streaming.scorer;

import java.io.IOException;

import org.junit.Test;

public class TaskScorerVpnSessionConfigTest extends TaskScorerConfigTest{

	@Test
	public void testSanity() throws IOException{
		buildScorersFromTaskConfig("config/vpnsession-prevalence-stats.properties");
	}
}
