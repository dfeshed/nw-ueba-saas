package fortscale.streaming.scorer;

import org.junit.Test;

public class EventsScoreStreamTaskServiceAmtTest  extends EventsScoreStreamTaskServiceTest{

	@Test
	public void testSanity() throws Exception{
		createEventsScoreStreamTaskService("config/amtsessions-prevalance-stats.properties");
	}
}
