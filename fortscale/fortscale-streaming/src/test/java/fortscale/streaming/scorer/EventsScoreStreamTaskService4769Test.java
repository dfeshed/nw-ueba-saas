package fortscale.streaming.scorer;

import org.junit.Test;

public class EventsScoreStreamTaskService4769Test extends EventsScoreStreamTaskServiceTest{

	@Test
	public void testSanity() throws Exception{
		createEventsScoreStreamTaskService("config/4769-prevalance-stats.properties");
	}
}
