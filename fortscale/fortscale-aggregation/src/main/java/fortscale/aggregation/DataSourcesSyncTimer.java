package fortscale.aggregation;

import fortscale.aggregation.feature.extraction.Event;
import fortscale.utils.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import java.util.*;

public class DataSourcesSyncTimer implements InitializingBean {
	private static final int DEFAULT_INITIAL_CAPACITY = 100;

	@Value("${impala.table.fields.data.source}")
	private String dataSourceFieldName;
	@Value("${impala.table.fields.epochtime}")
	private String epochtimeFieldName;
	@Value("${fortscale.aggregation.sync.timer.cycle.length.in.seconds}")
	private long cycleLengthInSeconds;
	@Value("${fortscale.aggregation.sync.timer.waiting.time.before.notification}")
	private long waitingTimeBeforeNotification;

	private long lastCycleTime;
	// Epochtime of latest event received, per data source
	private Map<String, Long> dataSourceToLastEventEpochtimeMap;
	// Priority queue of pending listeners, sorted according to their awaited notification epochtime, per data source
	private Map<String, PriorityQueue<Registration>> dataSourceToPendingQueueMap;
	// Priority queue of listeners that their awaited notification epochtime has been reached, per data source
	private Map<String, PriorityQueue<Registration>> dataSourceToReadyForNotificationQueueMap;
	// Mapping from registration ID to registration (and data source)
	private Map<Long, Pair<Registration, String>> idToRegistrationMap;
	// The ID that will be given in the next registration
	private long nextRegistrationId;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.isTrue(StringUtils.isNotBlank(dataSourceFieldName));
		Assert.isTrue(StringUtils.isNotBlank(epochtimeFieldName));
		Assert.isTrue(cycleLengthInSeconds > 0);
		Assert.isTrue(waitingTimeBeforeNotification >= 0);

		lastCycleTime = -1;
		dataSourceToLastEventEpochtimeMap = new HashMap<>();
		dataSourceToPendingQueueMap = new HashMap<>();
		dataSourceToReadyForNotificationQueueMap = new HashMap<>();
		idToRegistrationMap = new HashMap<>();
		nextRegistrationId = 0;
	}

	public void process(Event event) {
		String dataSource = ConversionUtils.convertToString(event.get(dataSourceFieldName));
		Long epochtime = ConversionUtils.convertToLong(event.get(epochtimeFieldName));

		if (StringUtils.isNotBlank(dataSource) && epochtime != null) {
			Long lastEventEpochtime = dataSourceToLastEventEpochtimeMap.get(dataSource);
			if (lastEventEpochtime == null || epochtime > lastEventEpochtime) {
				dataSourceToLastEventEpochtimeMap.put(dataSource, epochtime);
			}
		}
	}

	/**
	 * Registers a listener that will be notified when the given data source reaches the input epochtime.
	 * NOTE: Currently multiple data sources is not supported, so only the first data source in the list is considered.
	 *
	 * @param dataSources list of all the data sources that need to reach the awaited epochtime.
	 * @param epochtime   the awaited epochtime that needs to be reached.
	 * @param listener    the listener that will be notified.
	 * @return the ID of the new registration.
	 */
	public long notifyWhenDataSourcesReachTime(List<String> dataSources, long epochtime, DataSourcesSyncTimerListener listener) {
		// Validate input
		Assert.notEmpty(dataSources);
		Assert.isTrue(epochtime >= 0);
		Assert.notNull(listener);

		String dataSource = dataSources.get(0);
		Assert.hasText(dataSource);
		PriorityQueue<Registration> pendingQueue = dataSourceToPendingQueueMap.get(dataSource);
		if (pendingQueue == null) {
			pendingQueue = new PriorityQueue<>(DEFAULT_INITIAL_CAPACITY, new EpochtimeComparator());
			dataSourceToPendingQueueMap.put(dataSource, pendingQueue);

			if (!dataSourceToLastEventEpochtimeMap.containsKey(dataSource)) {
				dataSourceToLastEventEpochtimeMap.put(dataSource, 0L);
			}
		}

		Registration registration = new Registration(listener, epochtime, nextRegistrationId);
		pendingQueue.add(registration);
		idToRegistrationMap.put(nextRegistrationId, Pair.of(registration, dataSource));
		nextRegistrationId++;
		return registration.getId();
	}

	public long updateNotificationRegistration(long registrationId, long epochtime) {
		Pair<Registration, String> pair = idToRegistrationMap.get(registrationId);

		if (pair != null && epochtime >= 0) {
			Registration registration = pair.getLeft();
			String dataSource = pair.getRight();

			PriorityQueue<Registration> pendingQueue = dataSourceToPendingQueueMap.get(dataSource);
			PriorityQueue<Registration> readyForNotificationQueue = dataSourceToReadyForNotificationQueueMap.get(dataSource);

			if (pendingQueue.contains(registration)) {
				pendingQueue.remove(registration);
			} else {
				readyForNotificationQueue.remove(registration);
			}

			registration.setEpochtime(epochtime);
			pendingQueue.add(registration);
			return registrationId;
		} else {
			return -1;
		}
	}

	public void timeCheck(long systemTimeInMillis) {
		long systemTimeInSeconds = systemTimeInMillis / 1000;
		if (systemTimeInSeconds >= lastCycleTime + cycleLengthInSeconds) {
			lastCycleTime = systemTimeInSeconds;
			handlePendingQueues(systemTimeInSeconds);
			handleReadyForNotificationQueues(systemTimeInSeconds);
		}
	}

	private void handlePendingQueues(long currentSystemTime) {
		for (Map.Entry<String, PriorityQueue<Registration>> entry : dataSourceToPendingQueueMap.entrySet()) {
			String dataSource = entry.getKey();
			Long lastEventEpochtime = dataSourceToLastEventEpochtimeMap.get(dataSource);
			PriorityQueue<Registration> pendingQueue = entry.getValue();

			while (!pendingQueue.isEmpty() && pendingQueue.peek().getEpochtime() <= lastEventEpochtime) {
				Registration registration = pendingQueue.poll();
				registration.setSendingSystemTime(currentSystemTime + waitingTimeBeforeNotification);

				PriorityQueue<Registration> readyForNotificationQueue = dataSourceToReadyForNotificationQueueMap.get(dataSource);
				if (readyForNotificationQueue == null) {
					readyForNotificationQueue = new PriorityQueue<>(DEFAULT_INITIAL_CAPACITY, new SendingSystemTimeComparator());
					dataSourceToReadyForNotificationQueueMap.put(dataSource, readyForNotificationQueue);
				}

				readyForNotificationQueue.add(registration);
			}
		}
	}

	private void handleReadyForNotificationQueues(long currentSystemTime) {
		for (PriorityQueue<Registration> readyForNotificationQueue : dataSourceToReadyForNotificationQueueMap.values()) {
			while (!readyForNotificationQueue.isEmpty() && readyForNotificationQueue.peek().getSendingSystemTime() <= currentSystemTime) {
				Registration registration = readyForNotificationQueue.poll();
				idToRegistrationMap.remove(registration.getId());
				registration.getListener().dataSourcesReachedTime();
			}
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
