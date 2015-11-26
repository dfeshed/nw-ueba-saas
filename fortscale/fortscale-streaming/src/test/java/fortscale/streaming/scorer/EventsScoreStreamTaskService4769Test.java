package fortscale.streaming.scorer;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import org.junit.Test;

public class EventsScoreStreamTaskService4769Test extends EventsScoreStreamTaskServiceTest{

	@Test
	public void testSanity() throws Exception{
		createEventsScoreStreamTaskService("config/raw-events-prevalence-stats-task.properties", new StreamingTaskDataSourceConfigKey("kerberos_logins", "Sec4769EventsFilterStreamTask"));
	}	
}
