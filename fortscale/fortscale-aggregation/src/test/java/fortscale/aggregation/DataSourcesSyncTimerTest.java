package fortscale.aggregation;

import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/bucketconf-context-test.xml"})
public class DataSourcesSyncTimerTest {
	private static final String DEFAULT_DATA_SOURCE = "ssh";

	@Value("${impala.table.fields.epochtime}")
	private String epochtimeFieldName;
	@Value("${fortscale.aggregation.sync.timer.cycle.length.in.seconds}")
	private long cycleLengthInSeconds;
	@Value("${fortscale.aggregation.sync.timer.waiting.time.before.notification}")
	private long waitingTimeBeforeNotification;

	@Autowired
	private DataSourcesSyncTimer timer;

	@Test
	public void timer_should_notify_listeners_when_their_awaited_epochtime_is_reached() throws Exception {
		Assert.assertNotNull(timer);
		JSONObject message = new JSONObject();

		// First registration
		List<String> dataSources1 = new ArrayList<>();
		dataSources1.add(DEFAULT_DATA_SOURCE);
		long epochtime1 = 1435752000; // 12:00
		DataSourcesSyncTimerListener listener1 = Mockito.mock(DataSourcesSyncTimerListener.class);

		// Second registration
		List<String> dataSources2 = new ArrayList<>();
		dataSources2.add(DEFAULT_DATA_SOURCE);
		long epochtime2 = 1435755600; // 13:00
		DataSourcesSyncTimerListener listener2 = Mockito.mock(DataSourcesSyncTimerListener.class);

		// Third registration
		List<String> dataSources3 = new ArrayList<>();
		dataSources3.add(DEFAULT_DATA_SOURCE);
		long epochtime3 = 1435759200; // 14:00
		DataSourcesSyncTimerListener listener3 = Mockito.mock(DataSourcesSyncTimerListener.class);

		// Register all
		Assert.assertEquals(0, timer.notifyWhenDataSourcesReachTime(dataSources1, epochtime1, listener1));
		Assert.assertEquals(1, timer.notifyWhenDataSourcesReachTime(dataSources2, epochtime2, listener2));
		Assert.assertEquals(2, timer.notifyWhenDataSourcesReachTime(dataSources3, epochtime3, listener3));

		// None of the listeners should be notified
		long systemTime = System.currentTimeMillis();
		timer.timeCheck(systemTime);
		Mockito.verify(listener1, Mockito.never()).dataSourcesReachedTime();
		Mockito.verify(listener2, Mockito.never()).dataSourcesReachedTime();
		Mockito.verify(listener3, Mockito.never()).dataSourcesReachedTime();

		// Process event with epochtime later than epochtime1
		message.put(epochtimeFieldName, epochtime1 + 600); // add 10 minutes
		timer.process(message);

		// listener1 shouldn't be notified yet
		systemTime += cycleLengthInSeconds * 1000;
		timer.timeCheck(systemTime);
		Mockito.verify(listener1, Mockito.never()).dataSourcesReachedTime();
		Mockito.verify(listener2, Mockito.never()).dataSourcesReachedTime();
		Mockito.verify(listener3, Mockito.never()).dataSourcesReachedTime();

		// Now listener1 should be notified
		systemTime += (cycleLengthInSeconds + waitingTimeBeforeNotification) * 1000;
		timer.timeCheck(systemTime);
		Mockito.verify(listener1, Mockito.times(1)).dataSourcesReachedTime();
		Mockito.verify(listener2, Mockito.never()).dataSourcesReachedTime();
		Mockito.verify(listener3, Mockito.never()).dataSourcesReachedTime();

		// Change epochtime2
		epochtime2 = 1435762800; // 15:00
		timer.updateNotificationRegistration(1, epochtime2);

		// Process event with epochtime later than epochtime3 (but earlier than updated epochtime2)
		message.put(epochtimeFieldName, epochtime3 + 1800); // add 30 minutes
		timer.process(message);

		// listener3 shouldn't be notified yet
		systemTime += cycleLengthInSeconds * 1000;
		timer.timeCheck(systemTime);
		Mockito.verify(listener1, Mockito.times(1)).dataSourcesReachedTime();
		Mockito.verify(listener2, Mockito.never()).dataSourcesReachedTime();
		Mockito.verify(listener3, Mockito.never()).dataSourcesReachedTime();

		// Now listener3 should be notified
		systemTime += (cycleLengthInSeconds + waitingTimeBeforeNotification) * 1000;
		timer.timeCheck(systemTime);
		Mockito.verify(listener1, Mockito.times(1)).dataSourcesReachedTime();
		Mockito.verify(listener2, Mockito.never()).dataSourcesReachedTime();
		Mockito.verify(listener3, Mockito.times(1)).dataSourcesReachedTime();

		// Process event with epochtime later than updated epochtime2
		message.put(epochtimeFieldName, epochtime2 + 1200); // add 20 minutes
		timer.process(message);

		// listener2 shouldn't be notified yet
		systemTime += cycleLengthInSeconds * 1000;
		timer.timeCheck(systemTime);
		Mockito.verify(listener1, Mockito.times(1)).dataSourcesReachedTime();
		Mockito.verify(listener2, Mockito.never()).dataSourcesReachedTime();
		Mockito.verify(listener3, Mockito.times(1)).dataSourcesReachedTime();

		// Now listener2 should be notified
		systemTime += (cycleLengthInSeconds + waitingTimeBeforeNotification) * 1000;
		timer.timeCheck(systemTime);
		Mockito.verify(listener1, Mockito.times(1)).dataSourcesReachedTime();
		Mockito.verify(listener2, Mockito.times(1)).dataSourcesReachedTime();
		Mockito.verify(listener3, Mockito.times(1)).dataSourcesReachedTime();
	}
}
