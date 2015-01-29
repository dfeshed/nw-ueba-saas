package fortscale.streaming.scorer;

import org.junit.Test;

public class EventsScoreStreamTaskServiceVpnTest extends EventsScoreStreamTaskServiceTest{

	@Test
	public void testSanity() throws Exception{
		createEventsScoreStreamTaskService("config/vpn-prevalence-stats.properties");
	}
}
