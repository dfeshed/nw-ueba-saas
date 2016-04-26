package fortscale.aggregation;

import fortscale.common.event.Event;
import fortscale.utils.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.util.*;

public class DataSourcesSyncTimer implements InitializingBean {
	private static final int DEFAULT_INITIAL_CAPACITY = 100;

	@Value("${impala.table.fields.epochtime}")
	private String epochtimeFieldName;
	@Value("${fortscale.aggregation.sync.timer.cycle.length.in.seconds}")
	private long cycleLengthInSeconds;
	@Value("${fortscale.aggregation.sync.timer.waiting.time.before.notification}")
	private long waitingTimeBeforeNotification;

	private long lastCycleTime;
	private long lastEventEpochtime;

	// Priority queue of pending listeners (and their data sources),
	// sorted according to the awaited notification epochtime
	private PriorityQueue<Registration> pending;
	// Priority queue of listeners (and their data sources) that are ready to be notified. These listeners
	// aren't notified immediately, but only after 'waitingTimeBeforeNotification' seconds have passed
	private PriorityQueue<Registration> readyForNotification;
	// Mapping from registration ID to registration (in either queue)
	private Map<Long, Registration> idToRegistrationMap;
	// The ID that will be given in the next registration
	private long nextRegistrationId;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.isTrue(StringUtils.isNotBlank(epochtimeFieldName));
		Assert.isTrue(cycleLengthInSeconds > 0);
		Assert.isTrue(waitingTimeBeforeNotification >= 0);

		reset();
	}

	public void reset(){
		lastCycleTime = -1;
		lastEventEpochtime = 0;

		pending = new PriorityQueue<>(DEFAULT_INITIAL_CAPACITY, new EpochtimeComparator());
		readyForNotification = new PriorityQueue<>(DEFAULT_INITIAL_CAPACITY, new SendingSystemTimeComparator());
		idToRegistrationMap = new HashMap<>();
		nextRegistrationId = 0;
	}

	public void process(Event event) {
		Long epochtime = ConversionUtils.convertToLong(event.get(epochtimeFieldName));

		if (epochtime != null) {
			advanceLastEventEpochtime(epochtime);
		}
	}

	/**
	 * Registers a listener that will be notified when all given data sources reach the input epochtime.
	 * NOTE: Currently the timer keeps a 'last event epochtime' for all the events (regardless of their data source).
	 * In the future, such epochtime will be kept for each data source separately,
	 * then the list of data sources will be needed.
	 *
	 * @param dataSources list of all the data sources that need to reach the awaited epochtime. Notice: currently it is not used.
	 * @param epochtime   the awaited epochtime that needs to be reached.
	 * @param listener    the listener that will be notified.
	 * @return the ID of the new registration.
	 */
	public long notifyWhenDataSourcesReachTime(List<String> dataSources, long epochtime, DataSourcesSyncTimerListener listener) {
		// Validate input
		Assert.notEmpty(dataSources);
		Assert.isTrue(epochtime >= 0);
		Assert.notNull(listener);

		Registration registration = new Registration(listener, epochtime, nextRegistrationId);
		pending.add(registration);
		idToRegistrationMap.put(nextRegistrationId, registration);
		nextRegistrationId++;
		return registration.getId();
	}

	public long updateNotificationRegistration(long registrationId, long epochtime) {
		Registration registration = idToRegistrationMap.get(registrationId);

		if (registration != null && epochtime >= 0) {
			if (pending.contains(registration)) {
				pending.remove(registration);
			} else if (readyForNotification.contains(registration)) {
				readyForNotification.remove(registration);
			}

			registration.setEpochtime(epochtime);
			pending.add(registration);
			return registrationId;
		} else {
			return -1;
		}
	}

	public void timeCheck(long systemTimeInMillis) throws Exception {
		long systemTimeInSeconds = systemTimeInMillis / 1000;
		if (systemTimeInSeconds >= lastCycleTime + cycleLengthInSeconds) {
			lastCycleTime = systemTimeInSeconds;
			handlePendingQueue(systemTimeInSeconds);
			handleReadyForNotificationQueue(systemTimeInSeconds);
		}
	}

	private void handlePendingQueue(long currentSystemTime) {
		while (!pending.isEmpty() && pending.peek().getEpochtime() <= lastEventEpochtime) {
			Registration registration = pending.poll();
			registration.setSendingSystemTime(currentSystemTime + waitingTimeBeforeNotification);
			readyForNotification.add(registration);
		}
	}

	private void handleReadyForNotificationQueue(long currentSystemTime) throws Exception {
		while (!readyForNotification.isEmpty() && readyForNotification.peek().getSendingSystemTime() <= currentSystemTime) {
			Registration registration = readyForNotification.poll();
			idToRegistrationMap.remove(registration.getId());
			registration.getListener().dataSourcesReachedTime();
		}
	}

	public long getLastEventEpochtime() {
		return lastEventEpochtime;
	}

	public void advanceLastEventEpochtime(long lastEventEpochtime) {
		if (lastEventEpochtime > this.lastEventEpochtime) {
			this.lastEventEpochtime = lastEventEpochtime;
		}
	}

	private static final class Registration {
		// The listener that needs to be notified
		private DataSourcesSyncTimerListener listener;
		// The epochtime that needs to be reached
		private long epochtime;
		// The system time in which the listener should be notified
		private long sendingSystemTime;
		// This registration's ID
		private long id;

		public Registration(DataSourcesSyncTimerListener listener, long epochtime, long id) {
			this.listener = listener;
			this.epochtime = epochtime;
			this.id = id;

			// Updated dynamically and not upon instantiation
			this.sendingSystemTime = -1;
		}

		public DataSourcesSyncTimerListener getListener() {
			return listener;
		}

		public long getEpochtime() {
			return epochtime;
		}

		public void setEpochtime(long epochtime) {
			this.epochtime = epochtime;
		}

		public long getSendingSystemTime() {
			return sendingSystemTime;
		}

		public void setSendingSystemTime(long sendingSystemTime) {
			this.sendingSystemTime = sendingSystemTime;
		}

		public long getId() {
			return id;
		}
	}

	private static final class EpochtimeComparator implements Comparator<Registration> {
		@Override
		public int compare(Registration reg1, Registration reg2) {
			return Long.compare(reg1.getEpochtime(), reg2.getEpochtime());
		}
	}

	private static final class SendingSystemTimeComparator implements Comparator<Registration> {
		@Override
		public int compare(Registration reg1, Registration reg2) {
			int res = Long.compare(reg1.getSendingSystemTime(), reg2.getSendingSystemTime());
			return res == 0 ? Long.compare(reg1.getEpochtime(), reg2.getEpochtime()) : res;
		}
	}
}
