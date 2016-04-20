package fortscale.collection.jobs.aggregation.events;

import fortscale.aggregation.feature.event.ScoredAggrEventsCounterReader;
import fortscale.utils.kafka.IKafkaSynchronizer;
import fortscale.utils.kafka.SimpleMetricsReader;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by amira on 17/04/2016.
 */
public class AggregationEventSynchronizer implements IKafkaSynchronizer {
    private static final Logger logger = Logger.getLogger(AggregationEventSynchronizer.class);
    private static final long MILLIS_TO_SLEEP = 60000;

    private SimpleMetricsReader entityEventsTaskMetricsReader;

    @Autowired
    private ScoredAggrEventsCounterReader scoredAggrEventsCounterReader;

    private long timeoutInMillis;
    private String metric;
    private long initialNumberOfProcessedEvents;
    private long initialNumberOfScoredAggrEvents;

    public AggregationEventSynchronizer(String jobClassToMonitor, String jobToMonitor, long timeToWaitInMilliseconds) throws TimeoutException {
        this.timeoutInMillis = timeToWaitInMilliseconds;
        metric = String.format("%s-received-message-count", jobToMonitor);
        
        entityEventsTaskMetricsReader = new SimpleMetricsReader("entityEventsTaskMetricsReader", 0,
                jobToMonitor, jobClassToMonitor, Collections.singleton(metric));

        entityEventsTaskMetricsReader.start();
        setInitialCounters();
    }

    private void setInitialCounters() throws TimeoutException {
        long startTimeInMillis = System.currentTimeMillis();
        while (entityEventsTaskMetricsReader.getLong(metric) == null) {
            if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
                throwTimeoutException();
            }
            logger.info("AggregationEventSynchoronizer is going to sleep for awhile...");
            try {
                Thread.sleep(MILLIS_TO_SLEEP);
            } catch (InterruptedException e) {}
        }
        initialNumberOfProcessedEvents = entityEventsTaskMetricsReader.getLong(metric);
        logger.info(String.format("initialNumberOfProcessedEvents =  %d",initialNumberOfProcessedEvents));

        initialNumberOfScoredAggrEvents = scoredAggrEventsCounterReader.getTotalNumberOfScoredEvents();
        logger.info(String.format("initialNumberOfScoredAggrEvents = %d", initialNumberOfScoredAggrEvents));

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


    @Override
    public boolean synchronize(long numberOfEvents) throws TimeoutException {
        long startTimeInMillis = System.currentTimeMillis();

        while(isNumberOfProcessedEventsInEntityEventTaskGTE(numberOfEvents) ||
              isNumberOfNewScoredAggrEventsGTE(numberOfEvents) ) {
            if (timeoutInMillis > 0 && System.currentTimeMillis() - startTimeInMillis > timeoutInMillis) {
                throwTimeoutException();
            }

            logger.info("AggregationEventSynchoronizer is going to sleep for awhile...");

            try {
                Thread.sleep(MILLIS_TO_SLEEP);
            } catch (InterruptedException e) {}
        }
        logger.info("AggregationEventSynchoronizer woke up");
        return true;
    }


    private void throwTimeoutException() throws TimeoutException {
        String msg = String.format("Got timeout exception while waiting for the entity events task to process aggregated events. Waited %d seconds",
                TimeUnit.MILLISECONDS.toSeconds(timeoutInMillis));
        throw new TimeoutException(msg);
    }
}
