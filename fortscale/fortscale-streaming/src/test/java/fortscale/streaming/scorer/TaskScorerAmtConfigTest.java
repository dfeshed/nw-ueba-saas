package fortscale.streaming.scorer;

import java.io.IOException;

import org.junit.Test;

public class TaskScorerAmtConfigTest extends TaskScorerConfigTest{

	
	@Test
	public void testSanity() throws IOException{
		buildScorersFromTaskConfig("config/amtsessions-prevalance-stats.properties");
	}
}
