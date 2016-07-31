package fortscale.collection.jobs.aggregation.events;

import fortscale.aggregation.feature.event.ScoredEventsCounterReader;
import fortscale.entity.event.EntityEventMetaDataCountReader;
import fortscale.utils.kafka.KafkaEventsWriter;
import fortscale.utils.kafka.SimpleMetricsReader;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by amira on 17/04/2016.
 */
@Configurable(preConstruction = true)
public class AggregationEventSynchronizer {
    private static final Logger logger = Logger.getLogger(AggregationEventSynchronizer.class);
    private static final long MILLIS_TO_SLEEP = 60000;
    private static final String ENTITY_EVENT_STREAM_TASK_CONTROL_TOPIC = "fortscale-entity-event-stream-control";

    private SimpleMetricsReader entityEventsTaskMetricsReader;

    @Autowired
    private ScoredEventsCounterReader scoredAggrEventsCounterReader;
    @Autowired
    private EntityEventMetaDataCountReader entityEventMetaDataCountReader;

    private KafkaEventsWriter entityEventStreamTaskControlTopicWriter;

    private long timeoutInMillis;
    private String metric;
    private long initialNumberOfProcessedEvents;
    private long initialNumberOfScoredAggrEvents;

    public AggregationEventSynchronizer(String jobClassToMonitor, String jobToMonitor, long timeToWaitInMilliseconds) {
        this.timeoutInMillis = timeToWaitInMilliseconds;
        metric = String.format("%s-received-message-count", jobToMonitor);
        
        entityEventsTaskMetricsReader = new SimpleMetricsReader("entityEventsTaskMetricsReader", 0,
                jobToMonitor, jobClassToMonitor, Collections.singleton(metric));
    }

    public void init() throws TimeoutException{
        entityEventStreamTaskControlTopicWriter = new KafkaEventsWriter(ENTITY_EVENT_STREAM_TASK_CONTROL_TOPIC);

        entityEventsTaskMetricsReader.start();
        setInitialCounters();
    }

    public void close(){
        entityEventsTaskMetricsReader.end();
        entityEventStreamTaskControlTopicWriter.close();
    }

    private void setInitialCounters() throws TimeoutException {
        long startTimeInMillis = System.currentTimeMillis();
        while (entityEventsTaskMetricsReader.getLong(metric) == null) {
            if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
                throwTimeoutException();
            }
            countSheeps("waiting for initial counters, going to sleep for awhile...");
        }
        initialNumberOfProcessedEvents = entityEventsTaskMetricsReader.getLong(metric);
        logger.info(String.format("initialNumberOfProcessedEvents =  %d",initialNumberOfProcessedEvents));

        initialNumberOfScoredAggrEvents = scoredAggrEventsCounterReader.getTotalNumberOfScoredEvents();
        logger.info(String.format("initialNumberOfScoredAggrEvents = %d", initialNumberOfScoredAggrEvents));

    }

    private void countSheeps(String msg) {
        logger.info(msg);
        try {
            Thread.sleep(MILLIS_TO_SLEEP);
        } catch (InterruptedException e) {}
    }

    private boolean isNumberOfProcessedEventsInEntityEventTaskGTE(long numberOfEvents) {
        Long numberOfProcessedEvents = entityEventsTaskMetricsReader.getLong(metric);
        logger.info(String.format("Entity event streaming task processed %d events", numberOfProcessedEvents));
        logger.info(String.format("Synchronizer waiting for the number of processed events to reach %d", initialNumberOfProcessedEvents + numberOfEvents));
        return (numberOfProcessedEvents!=null &&
                numberOfProcessedEvents >= initialNumberOfProcessedEvents + numberOfEvents);
    }

    private boolean isNumberOfNewScoredAggrEventsGTE(long numberOfEvents) {
        long totalNumberOfScoredEvents = scoredAggrEventsCounterReader.getTotalNumberOfScoredEvents();
        logger.info(String.format("Total numbe of scored aggregated events in mongo: %d", totalNumberOfScoredEvents));
        logger.info(String.format("Synchronizer waiting for the number of processed events to reach %d", initialNumberOfScoredAggrEvents + numberOfEvents));
        return (totalNumberOfScoredEvents >= initialNumberOfScoredAggrEvents + numberOfEvents);
    }


    public boolean throttle(long numberOfEvents, long numberOfFtypeEvents) throws TimeoutException {
        long startTimeInMillis = System.currentTimeMillis();

        while(!isNumberOfProcessedEventsInEntityEventTaskGTE(numberOfEvents) ||
              !isNumberOfNewScoredAggrEventsGTE(numberOfFtypeEvents) ) {
            if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
                throwTimeoutException();
            }
            countSheeps("throttling... going to sleep for awhile...");
        }
        logger.info("throttler woke up");
        return true;
    }


    private void throwTimeoutException() throws TimeoutException {
        String msg = String.format("Got timeout exception while waiting for the entity events task to process aggregated events. Waited %d seconds",
                TimeUnit.MILLISECONDS.toSeconds(timeoutInMillis));
        throw new TimeoutException(msg);
    }

    private void sendSyncCommandToEntityEventStreamTask() {
        JSONObject command = new JSONObject();
        command.put("command", "sync");
        command.put("requester", AggregationEventSynchronizer.class.getName());
        command.put("time", System.currentTimeMillis() / 1000);
        entityEventStreamTaskControlTopicWriter.send(null, command.toJSONString(JSONStyle.NO_COMPRESS));
    }

    public void waitForAllEntityEventDataToBeSavedInMongo() {
        sendSyncCommandToEntityEventStreamTask();
        long counter = 0;
        while ((counter = entityEventMetaDataCountReader.getTotalNumberOfEntityEventMetaDataEntries()) > 0) {
            countSheeps("waiting for entity event data to be saved in mongo, remaining: "+counter);
        }
    }
}
