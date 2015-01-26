package fortscale.streaming.scorer;

import java.io.IOException;

import org.junit.Test;


public class TaskScorer4769ConfigTest extends TaskScorerConfigTest{

	
	@Test
	public void testSanity() throws IOException{
		testSanity("config/4769-prevalance-stats.properties");
	}
}
