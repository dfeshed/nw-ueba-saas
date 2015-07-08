package fortscale.streaming.service.aggregation;

import net.minidev.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/bucketconf-context-test.xml"})
public class DataSourcesSyncTimerTest {
	@Value("${impala.table.fields.epochtime}")
	private String epochtimeFieldName;

	private static final long DEFAULT_CYCLE_LENGTH_IN_SECONDS = 3;
	private static final long DEFAULT_WAITING_TIME_BEFORE_NOTIFICATION = 1;
	private static final String DEFAULT_DATA_SOURCE = "ssh";

	@Test
	public void timer_should_notify_listeners_when_their_awaited_epochtime_is_reached() throws Exception {
		JSONObject message = new JSONObject();

		// Create data sources sync timer
		DataSourcesSyncTimer timer = new DataSourcesSyncTimer(DEFAULT_CYCLE_LENGTH_IN_SECONDS, DEFAULT_WAITING_TIME_BEFORE_NOTIFICATION);
		Assert.assertNotNull(timer);

		// First registration
		List<String> dataSources1 = new ArrayList<>();
		dataSources1.add(DEFAULT_DATA_SOURCE);
		long epochtime1 = 1435752000; // 12:00
		DataSourcesSyncTimerListener listener1 = mock(DataSourcesSyncTimerListener.class);

		// Second registration
		List<String> dataSources2 = new ArrayList<>();
		dataSources2.add(DEFAULT_DATA_SOURCE);
		long epochtime2 = 1435755600; // 13:00
		DataSourcesSyncTimerListener listener2 = mock(DataSourcesSyncTimerListener.class);

		// Third registration
		List<String> dataSources3 = new ArrayList<>();
		dataSources3.add(DEFAULT_DATA_SOURCE);
		long epochtime3 = 1435759200; // 14:00
		DataSourcesSyncTimerListener listener3 = mock(DataSourcesSyncTimerListener.class);

		// Register all
		Assert.assertEquals(0, timer.notifyWhenDataSourcesReachTime(dataSources1, epochtime1, listener1));
		Assert.assertEquals(1, timer.notifyWhenDataSourcesReachTime(dataSources2, epochtime2, listener2));
		Assert.assertEquals(2, timer.notifyWhenDataSourcesReachTime(dataSources3, epochtime3, listener3));

		// None of the listeners should be notified
		timer.timeCheck();
		verify(listener1, never()).dataSourcesReachedTime(dataSources1, epochtime1);
		verify(listener2, never()).dataSourcesReachedTime(dataSources2, epochtime2);
		verify(listener3, never()).dataSourcesReachedTime(dataSources3, epochtime3);

		// Make sure an entire cycle has passed
		Thread.sleep(DEFAULT_CYCLE_LENGTH_IN_SECONDS * 1000);

		// Process event with epochtime later than epochtime1
		message.put(epochtimeFieldName, epochtime1 + 600); // add 10 minutes
		timer.process(message);

		// listener1 shouldn't be notified yet
		timer.timeCheck();
		verify(listener1, never()).dataSourcesReachedTime(dataSources1, epochtime1);
		verify(listener2, never()).dataSourcesReachedTime(dataSources2, epochtime2);
		verify(listener3, never()).dataSourcesReachedTime(dataSources3, epochtime3);

		// Wait an entire cycle for timer to act again (make sure actual notification time has been reached)
		Thread.sleep((DEFAULT_CYCLE_LENGTH_IN_SECONDS + DEFAULT_WAITING_TIME_BEFORE_NOTIFICATION) * 1000);

		// Now listener1 should be notified
		timer.timeCheck();
		verify(listener1, times(1)).dataSourcesReachedTime(dataSources1, epochtime1);
		verify(listener2, never()).dataSourcesReachedTime(dataSources2, epochtime2);
		verify(listener3, never()).dataSourcesReachedTime(dataSources3, epochtime3);

		// Make sure an entire cycle has passed
		Thread.sleep(DEFAULT_CYCLE_LENGTH_IN_SECONDS * 1000);

		// Change epochtime2
		epochtime2 = 1435762800; // 15:00
		timer.updateNotificationRegistration(1, epochtime2);

		// Process event with epochtime later than epochtime3 (but earlier than updated epochtime2)
		message.put(epochtimeFieldName, epochtime3 + 1800); // add 30 minutes
		timer.process(message);

		// listener3 shouldn't be notified yet
		timer.timeCheck();
		verify(listener1, times(1)).dataSourcesReachedTime(dataSources1, epochtime1);
		verify(listener2, never()).dataSourcesReachedTime(dataSources2, epochtime2);
		verify(listener3, never()).dataSourcesReachedTime(dataSources3, epochtime3);

		// Wait an entire cycle for timer to act again (make sure actual notification time has been reached)
		Thread.sleep((DEFAULT_CYCLE_LENGTH_IN_SECONDS + DEFAULT_WAITING_TIME_BEFORE_NOTIFICATION) * 1000);

		// Now listener3 should be notified
		timer.timeCheck();
		verify(listener1, times(1)).dataSourcesReachedTime(dataSources1, epochtime1);
		verify(listener2, never()).dataSourcesReachedTime(dataSources2, epochtime2);
		verify(listener3, times(1)).dataSourcesReachedTime(dataSources3, epochtime3);

		// Make sure an entire cycle has passed
		Thread.sleep(DEFAULT_CYCLE_LENGTH_IN_SECONDS * 1000);

		// Process event with epochtime later than updated epochtime2
		message.put(epochtimeFieldName, epochtime2 + 1200); // add 20 minutes
		timer.process(message);

		// listener2 shouldn't be notified yet
		timer.timeCheck();
		verify(listener1, times(1)).dataSourcesReachedTime(dataSources1, epochtime1);
		verify(listener2, never()).dataSourcesReachedTime(dataSources2, epochtime2);
		verify(listener3, times(1)).dataSourcesReachedTime(dataSources3, epochtime3);

		// Wait an entire cycle for timer to act again (make sure actual notification time has been reached)
		Thread.sleep((DEFAULT_CYCLE_LENGTH_IN_SECONDS + DEFAULT_WAITING_TIME_BEFORE_NOTIFICATION) * 1000);

		// Now listener2 should be notified
		timer.timeCheck();
		verify(listener1, times(1)).dataSourcesReachedTime(dataSources1, epochtime1);
		verify(listener2, times(1)).dataSourcesReachedTime(dataSources2, epochtime2);
		verify(listener3, times(1)).dataSourcesReachedTime(dataSources3, epochtime3);
	}
}
