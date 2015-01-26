package fortscale.streaming.scorer;

import java.io.IOException;

import org.junit.Test;

public class TaskScorerSshConfigTest extends TaskScorerConfigTest{

	@Test
	public void testSanity() throws IOException{
		testSanity("config/ssh-prevalance-stats.properties");
	}
}
