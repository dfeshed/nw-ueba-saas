package fortscale.streaming.scorer;

import org.junit.Test;

import java.io.IOException;


public class TaskScorer4769ConfigTest extends TaskScorerConfigTest{

	
	@Test
	public void testSanity() throws IOException{
		buildScorersFromTaskConfig("config/raw-events-prevalence-stats-task.properties", "kerberos_logins");
	}
	
}
