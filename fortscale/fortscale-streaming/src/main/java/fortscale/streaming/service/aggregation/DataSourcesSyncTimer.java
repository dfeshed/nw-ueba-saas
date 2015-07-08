package fortscale.streaming.service.aggregation;
import fortscale.utils.ConversionUtils;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import java.util.*;
@Configurable(preConstruction = true)
public class DataSourcesSyncTimer {
    private static final int DEFAULT_INITIAL_CAPACITY = 100;
    @Value("${impala.table.fields.epochtime}")
    private String epochtimeFieldName;
    // Number of seconds between cycles
    private long cycleLengthInSeconds;
    // System start time of last cycle
    private long lastCycleTime;
    // The epochtime of the latest event ever processed
    private long lastEventEpochtime;
    // Time to wait before notifying listeners that are ready to be notified
    private long waitingTimeBeforeNotification;
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
    public DataSourcesSyncTimer(long cycleLengthInSeconds, long waitingTimeBeforeNotification) {
        // Validate input
        Assert.isTrue(cycleLengthInSeconds > 0);
        Assert.isTrue(waitingTimeBeforeNotification >= 0);
        this.cycleLengthInSeconds = cycleLengthInSeconds;
        lastCycleTime = -1;
        lastEventEpochtime = 0;
        this.waitingTimeBeforeNotification = waitingTimeBeforeNotification;
        pending = new PriorityQueue<>(DEFAULT_INITIAL_CAPACITY, new EpochtimeComparator());
        readyForNotification = new PriorityQueue<>(DEFAULT_INITIAL_CAPACITY, new SendingSystemTimeComparator());
        idToRegistrationMap = new HashMap<>();
        nextRegistrationId = 0;
    }
    public void process(JSONObject message) {
        Long epochtime = ConversionUtils.convertToLong(message.get(epochtimeFieldName));
        if (epochtime != null && epochtime > lastEventEpochtime) {
            lastEventEpochtime = epochtime;
        }
    }
    public long notifyWhenDataSourcesReachTime(List<String> dataSources, long epochtime, DataSourcesSyncTimerListener listener) {
        // Validate input
        Assert.notEmpty(dataSources);
        Assert.isTrue(epochtime >= 0);
        Assert.notNull(listener);
        Registration registration = new Registration(listener, dataSources, epochtime, nextRegistrationId);
        pending.add(registration);
        idToRegistrationMap.put(nextRegistrationId, registration);
        return nextRegistrationId++;
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
    public void timeCheck() {
        long currentSystemTime = System.currentTimeMillis() / 1000;
        if (currentSystemTime >= lastCycleTime + cycleLengthInSeconds) {
            lastCycleTime = currentSystemTime;
            handlePendingQueue(currentSystemTime);
            handleReadyForNotificationQueue(currentSystemTime);
        }
    }
    private void handlePendingQueue(long currentSystemTime) {
        while (!pending.isEmpty() && pending.peek().getEpochtime() <= lastEventEpochtime) {
            Registration registration = pending.poll();
            registration.setSendingSystemTime(currentSystemTime + waitingTimeBeforeNotification);
            readyForNotification.add(registration);
        }
    }
    private void handleReadyForNotificationQueue(long currentSystemTime) {
        while (!readyForNotification.isEmpty() && readyForNotification.peek().getSendingSystemTime() <= currentSystemTime) {
            Registration registration = readyForNotification.poll();
            idToRegistrationMap.remove(registration.getId());
            registration.getListener().dataSourcesReachedTime(registration.getEpochtime());
        }
    }
    private static final class Registration {
        // The listener that needs to be notified
        private DataSourcesSyncTimerListener listener;
        // The data sources that need to be tracked
        private List<String> dataSources;
        // The epochtime that needs to be reached
        private long epochtime;
        // The system time in which the listener should be notified
        private long sendingSystemTime;
        // This registration's ID
        private long id;
        public Registration(DataSourcesSyncTimerListener listener, List<String> dataSources, long epochtime, long id) {
            this.listener = listener;
            this.dataSources = dataSources;
            this.epochtime = epochtime;
            this.id = id;
            // Updated dynamically and not upon instantiation
            this.sendingSystemTime = -1;
        }
        public DataSourcesSyncTimerListener getListener() {
            return listener;
        }
        public List<String> getDataSources() {
            return dataSources;
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
            return Long.compare(reg1.getSendingSystemTime(), reg2.getSendingSystemTime());
        }
    }
}