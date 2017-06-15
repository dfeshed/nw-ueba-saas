package fortscale.aggregation;

import fortscale.common.event.EventMessage;
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
		long epochtime1 = System.currentTimeMillis() / 1000 + 3600;
		DataSourcesSyncTimerListener listener1 = Mockito.mock(DataSourcesSyncTimerListener.class);

		// Second registration
		List<String> dataSources2 = new ArrayList<>();
		dataSources2.add(DEFAULT_DATA_SOURCE);
		long epochtime2 = epochtime1 + 3600;
		DataSourcesSyncTimerListener listener2 = Mockito.mock(DataSourcesSyncTimerListener.class);

		// Third registration
		List<String> dataSources3 = new ArrayList<>();
		dataSources3.add(DEFAULT_DATA_SOURCE);
		long epochtime3 = epochtime2 + 3600;
		DataSourcesSyncTimerListener listener3 = Mockito.mock(DataSourcesSyncTimerListener.class);

		// Register all
		timer.notifyWhenDataSourcesReachTime(dataSources1, epochtime1, listener1);
		long registration2 = timer.notifyWhenDataSourcesReachTime(dataSources2, epochtime2, listener2);
		timer.notifyWhenDataSourcesReachTime(dataSources3, epochtime3, listener3);

		// None of the listeners should be notified
		long systemTime = System.currentTimeMillis();
		timer.timeCheck(systemTime);
		Mockito.verify(listener1, Mockito.never()).dataSourcesReachedTime();
		Mockito.verify(listener2, Mockito.never()).dataSourcesReachedTime();
		Mockito.verify(listener3, Mockito.never()).dataSourcesReachedTime();

		// Process event with epochtime later than epochtime1
		message.put(epochtimeFieldName, epochtime1 + 600); // add 10 minutes
		timer.process(new EventMessage(message));

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
		epochtime2 = epochtime3 + 3600;
		timer.updateNotificationRegistration(registration2, epochtime2);

		// Process event with epochtime later than epochtime3 (but earlier than updated epochtime2)
		message.put(epochtimeFieldName, epochtime3 + 1800); // add 30 minutes
		timer.process(new EventMessage(message));

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
		timer.process(new EventMessage(message));

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

	/**
	 * Testing the calling order of 1000 listeners that are registred to the same time.
	 */
	static long listenerCallingTimes[] = new long[1000];
	static DataSourcesSyncTimerListener dataSourceLinsteners[] = new DataSourcesSyncTimerListener[1000];
	class DataSourcesSyncTimerListenerImpl implements DataSourcesSyncTimerListener {
		int listnereNumber;

		public DataSourcesSyncTimerListenerImpl(int listnereNumber) {
			this.listnereNumber = listnereNumber;
		}

		@Override
		public void dataSourcesReachedTime() throws Exception {
			listenerCallingTimes[listnereNumber] = System.currentTimeMillis();
			Thread.currentThread().sleep(1);
		}
	}

	@Test
	public void test_listneres_calling_order() throws Exception{
		List<String> dataSources = new ArrayList<>();
		dataSources.add(DEFAULT_DATA_SOURCE);
		long timeToregister = timer.getLastEventEpochtime()+1;
		for(int i=0; i<1000; i++) {
			dataSourceLinsteners[i] = new DataSourcesSyncTimerListenerImpl(i);
			timer.notifyWhenDataSourcesReachTime(dataSources, timeToregister, dataSourceLinsteners[i]);
		}

		JSONObject message = new JSONObject();
		message.put(epochtimeFieldName, timeToregister+1);
		timer.process(new EventMessage(message));
		long systemTime = (timeToregister + 2) * 1000;
		timer.timeCheck(systemTime);
		systemTime += (cycleLengthInSeconds + waitingTimeBeforeNotification) * 1000;
		timer.timeCheck(systemTime);

		for(int i=1; i<1000; i++) {
			Assert.assertTrue(listenerCallingTimes[i-i] < listenerCallingTimes[i]);
		}
	}
}
