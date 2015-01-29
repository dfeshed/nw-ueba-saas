package fortscale.streaming.scorer;

import org.junit.Test;

public class EventsScoreStreamTaskServiceSshTest extends EventsScoreStreamTaskServiceTest{

	@Test
	public void testSanity() throws Exception{
		createEventsScoreStreamTaskService("config/ssh-prevalance-stats.properties");
	}
}
